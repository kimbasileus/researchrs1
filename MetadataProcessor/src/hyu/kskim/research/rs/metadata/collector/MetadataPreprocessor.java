package hyu.kskim.research.rs.metadata.collector;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

import hyu.kskim.research.rs.metadata.tools.nlp.Parser;
import hyu.kskim.research.rs.metadata.tools.nlp.PorterStemmer;
import hyu.kskim.research.rs.metadata.tools.nlp.StopWordEliminator;
import hyu.kskim.research.rs.utils.*;

/*
 * Part 2. 수집된 콘텐츠 메타데이터 문서를 전처리하여 각 아이템별로 word list를 생성하는 기능
 */
public class MetadataPreprocessor {
	String schema = null;
	String dir = null;
	int numOfItems =-1;
	DBManager db = null;
	FileIO file = null;
	Parser parser = null;
	StopWordEliminator stopword = null;
	PorterStemmer stemmer = null;
	
	public MetadataPreprocessor(String schema, String dir, int numOfItems) {
		this.schema = schema;
		this.dir = dir;
		this.numOfItems = numOfItems;
		
		this.db = new DBManager(null, null, null);
		this.db.connectDB("root", "kyungsookim");
		this.file = new FileIO();
		this.parser = new Parser();
		this.stopword = new StopWordEliminator();
		this.stemmer = new PorterStemmer();
	}
	
	public boolean getFeatureElementList() {
		try {
			boolean isSuccess = false;
			String failID = "";
			for(int itemID=1; itemID <=this.numOfItems; itemID++) {
				isSuccess = getFeatureElementList(itemID);
				System.out.println("Item "+itemID+" 처리 중");
				if(!isSuccess) failID = failID + itemID + ", ";
			}
			
			System.out.println("처리 실패한 아이템 리스트: "+failID);
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	/*
	 * 주어진 아이템의 메타데이터 문서로부터 특징 (word list)을 추출한다.
	 */
	public boolean getFeatureElementList(int itemID) {
		try {
			int fIndex = (itemID/2000)+1;
			if(itemID%2000==0) fIndex--;
			
			// 0. 디스크로부터 메타데이터 문서 로드
			StringBuffer docSb = new StringBuffer();
			String docu = null;
			BufferedReader reader = new BufferedReader(new FileReader(dir+"\\contentmetadata_"+fIndex+"\\item_"+itemID+".doc"));
			String inputLine = null;
			
			while ((inputLine = reader.readLine()) != null){
				//System.out.println(inputLine);
				if(inputLine.contains("Authority control") || inputLine.contains("Transclusion expansion time report") || 
						inputLine.contains("NewPP limit report") || inputLine.contains("Retrieved from \"https://en.wikipedia.org/w/index.php?")) 
					break;
				docSb.append(inputLine).append("\n");
			}
			reader.close();
			
			docu = docSb.toString();
			
			
			// 1. 태그 제거, 특수문자 제거, 중복 공백 모두 제거, 중복 개행 모두 제거, 특수문자/공통어 제거
			docu = webTagElimination_for_Wiki(docu);
			//this.file.writer(dir+"\\contentmetadata_"+fIndex+"\\AA_item_"+itemID+".doc", docu);
			
			
			// 2. 주어진 문서를 문장 단위로 분리
			ArrayList<String> senList = splitIntoSentences(docu);
			
			/*// For debug
			StringBuffer sb2 = new StringBuffer();
			for(int i=0; i < senList.size(); i++)
			{
				sb2.append(senList.get(i)).append("\n");
			}
			this.file.writer(dir+"\\contentmetadata_"+fIndex+"\\BB_item_"+itemID+".doc", sb2.toString());
			; // For debug
			*/
			
			// 3. 각 문장에 대하여 토큰 생성 + 불용어 제거 + 원형 변환
			StringBuffer wordList = new StringBuffer();
			String sen = null;
			for(int s=0; s < senList.size(); s++) {
				sen = splitIntoTokens(senList.get(s));
				if(sen==null || sen.length() <=1) continue;
				
				if(s==senList.size()-1) wordList.append(sen);
				else wordList.append(sen).append("/");
			}
			
			
			String result = null;
			result = wordList.toString();
			if(result!=null)
				result = result.replace("'", "\\'");
			else
				return false;
			
			// 5. 결과 반환
			String sql = "UPDATE `movielens`.`items_metadata` SET `words`='"+result+"' WHERE `itemID`='"+itemID+"';";
			//this.db.getStmt().executeUpdate(sql); // DB에 저장
			
			this.file.writer(dir+"\\contentmetadata_processed_"+fIndex+"\\item_processed_"+itemID+".doc", wordList.toString()); // 파일로 저장
			
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	// 1. 태그 제거, 특수문자 제거, 중복 공백 모두 제거, 중복 개행 모두 제거, 특수문자/공통어 제거 (WikiEncyclopedia Document 전용)
	public String webTagElimination_for_Wiki(String page) {
		try {
			if(page==null) return null;
			String docu = page.toString();
			
			docu = docu.replace("Theatrical release poster", "");
			docu = docu.replace("Jump to:navigation, search", "").replace("\n", "<br>"); // 개행문자 임시 변경
			docu = docu.replaceAll("<br><br>Contents<br>(.*?)(See also|References|External links)<br><br>", ""); // Content (목차) 제거
			
			
			docu = docu.replace("<br>", " "). // 개행문자는 모두 빈칸으로 변경
					replaceAll("\\t", "").replaceAll("\\s{2,}"," "). // 중복 탭, 중복 공백 제거
					replaceAll("\\.{2,}",". ").
					replaceAll("<(section class=|li class=|ul class=|div|a href=|svg class=)(.*?)\">", "").
					replaceAll("<(path d=)(.*?)(\")>", "").
					replaceAll("<=ipl(.*?)>", "").
					replaceAll("<p>|</p>|<em>|</em>|</a>|</div>|</>|<br>|</br>|<ul>|</ul>|<li>|</li>|</svg>", "").
					replaceAll("&(.*?);", "").  
					replace("/search/title?plot_author=", "").
					replace("&view=simple&sort=alpha&ref_=ttpl_pl_5", "").
					replace("</section>", "").replace("<!--", "").replace("-->", "");
			
			docu = docu.replace("<", "").replace(">", "").replace("/", "").replace("(", "").replace(")", "").replace("!", "").replace("@", "").
					replace("$", "").replace("%", "").replace("&", "").replace("*", "").replace("|", "").
					replace("\"", "").replace(",", "").replace("{", "").replace("}", ". ").replace("^", "").
					replace("-", " ").replace("~", " ").replace("`", "").replace("'s", "").replace("'re", "").
					replace(", ", " ").replace(",", " ").replace(":", "").replace(";", "").replace("?", "").replace("no. ", "").replace("No. ", "").
					replace("pp. ", "").replace("cf. ", "").replace("p. ", "").replace("www. ", "").replace("e.g. ", "");
			
			//docu = docu.replaceAll("/^([\\x00-\\x7e]|.{2})*", "");
			//System.out.println(docu);
			
			return docu;
		}catch(Exception e) {
			System.err.println("webTagElimination Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	
	
	// 2. 주어진 문서를 문장 단위로 분리
	public ArrayList<String> splitIntoSentences(String page) {
		try {
			if(page==null) return null;
			ArrayList<String> sens = new ArrayList<String>();
			String[] lines = page.split("\\.\\s");
			
			for(int i=0 ; i < lines.length; i++) {
				sens.add(lines[i]);
			}
			
			return sens;
		}catch(Exception e) {
			System.err.println("splitIntoSentences Exception: "+e.getMessage());
			return null;
		}
	}	
	
	
	// 3. 주어진 문장에 대하여 단어 토큰 생성
	public String splitIntoTokens(String sentence) {
		try {
			if(sentence==null || sentence.length() ==0 ) return null;
			ArrayList<String> tokenList = null;
			
			// 1. Tokenization
			tokenList = this.parser.tokenization(sentence);
			if(tokenList==null) return null;
			
			// 2. Stop Word Elimination
			tokenList = this.stopword.getStopWordEliminatedSentence_LowerCase(tokenList);
			if(tokenList==null) return null;
			
			// 3. Stemming
			String rootWord = null;
			for(int i=0; i < tokenList.size(); i++) {
				rootWord = this.stemmer.stemming_word(tokenList.get(i));
				if(rootWord==null) continue;
				
				tokenList.set(i, rootWord);
			}
			
			
			// 4. 변환
			StringBuffer sb = new StringBuffer();
			for(int i=0; i < tokenList.size(); i++) {
				if(i==tokenList.size()-1) sb.append(tokenList.get(i));
				else sb.append(tokenList.get(i)).append(",");
			}
			
			String newSen = sb.toString();
			
			if(newSen.length()<=1) return null;
			
			return newSen;
		}catch(Exception e) {
			System.err.println("splitIntoTokens Exception: "+e.getMessage());
			return null;
		}
	}
	
	
}



class DocuWords{
	public int itemID;
	public int frequency; // 모든 단어들의 빈도수
	public int numOfWords; // 순수 단어들의 개수 (중복 제거시)
	
	String wordList; // Stemming이 완료된 모든 단어들의 리스트 (중복 포함)

	public DocuWords(int itemID, int frequency, int numOfWords, String wordList) {
		this.itemID = itemID;
		this.frequency = frequency;
		this.numOfWords = numOfWords;
		this.wordList = wordList;
	}

	public DocuWords(int itemID, int frequency, int numOfWords, StringBuffer wordListBuffer) {
		this.itemID = itemID;
		this.frequency = frequency;
		this.numOfWords = numOfWords;
		this.wordList = wordListBuffer.toString();
	}
}
