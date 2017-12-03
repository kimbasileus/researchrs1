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
 * Part 2. ������ ������ ��Ÿ������ ������ ��ó���Ͽ� �� �����ۺ��� word list�� �����ϴ� ���
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
				System.out.println("Item "+itemID+" ó�� ��");
				if(!isSuccess) failID = failID + itemID + ", ";
			}
			
			System.out.println("ó�� ������ ������ ����Ʈ: "+failID);
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	/*
	 * �־��� �������� ��Ÿ������ �����κ��� Ư¡ (word list)�� �����Ѵ�.
	 */
	public boolean getFeatureElementList(int itemID) {
		try {
			int fIndex = (itemID/2000)+1;
			if(itemID%2000==0) fIndex--;
			
			// 0. ��ũ�κ��� ��Ÿ������ ���� �ε�
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
			
			
			// 1. �±� ����, Ư������ ����, �ߺ� ���� ��� ����, �ߺ� ���� ��� ����, Ư������/����� ����
			docu = webTagElimination_for_Wiki(docu);
			//this.file.writer(dir+"\\contentmetadata_"+fIndex+"\\AA_item_"+itemID+".doc", docu);
			
			
			// 2. �־��� ������ ���� ������ �и�
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
			
			// 3. �� ���忡 ���Ͽ� ��ū ���� + �ҿ�� ���� + ���� ��ȯ
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
			
			// 5. ��� ��ȯ
			String sql = "UPDATE `movielens`.`items_metadata` SET `words`='"+result+"' WHERE `itemID`='"+itemID+"';";
			//this.db.getStmt().executeUpdate(sql); // DB�� ����
			
			this.file.writer(dir+"\\contentmetadata_processed_"+fIndex+"\\item_processed_"+itemID+".doc", wordList.toString()); // ���Ϸ� ����
			
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	// 1. �±� ����, Ư������ ����, �ߺ� ���� ��� ����, �ߺ� ���� ��� ����, Ư������/����� ���� (WikiEncyclopedia Document ����)
	public String webTagElimination_for_Wiki(String page) {
		try {
			if(page==null) return null;
			String docu = page.toString();
			
			docu = docu.replace("Theatrical release poster", "");
			docu = docu.replace("Jump to:navigation, search", "").replace("\n", "<br>"); // ���๮�� �ӽ� ����
			docu = docu.replaceAll("<br><br>Contents<br>(.*?)(See also|References|External links)<br><br>", ""); // Content (����) ����
			
			
			docu = docu.replace("<br>", " "). // ���๮�ڴ� ��� ��ĭ���� ����
					replaceAll("\\t", "").replaceAll("\\s{2,}"," "). // �ߺ� ��, �ߺ� ���� ����
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
	
	
	
	
	// 2. �־��� ������ ���� ������ �и�
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
	
	
	// 3. �־��� ���忡 ���Ͽ� �ܾ� ��ū ����
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
			
			
			// 4. ��ȯ
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
	public int frequency; // ��� �ܾ���� �󵵼�
	public int numOfWords; // ���� �ܾ���� ���� (�ߺ� ���Ž�)
	
	String wordList; // Stemming�� �Ϸ�� ��� �ܾ���� ����Ʈ (�ߺ� ����)

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
