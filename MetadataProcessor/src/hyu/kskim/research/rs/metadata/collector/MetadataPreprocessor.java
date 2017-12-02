package hyu.kskim.research.rs.metadata.collector;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class MetadataPreprocessor {
	String dir = "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small";
	int method = 1; // 1. Wiki, 2. imdb, 3. Etc.
	
	public DocuWords getFeatureElementList(int itemID) {
		try {
			// 0. 디스크로부터 메타데이터 문서 로드
			StringBuffer docSb = new StringBuffer();
			String docu = null;
			BufferedReader reader = new BufferedReader(new FileReader(dir+"\\item_"+itemID+".doc"));
			String inputLine = null;
			
			while ((inputLine = reader.readLine()) != null){
				System.out.println(inputLine);
				docSb.append(inputLine).append("\n");
			}
			reader.close();
			
			docu = docSb.toString();
			
			
			// 1. 태그 제거, 특수문자 제거, 중복 공백 모두 제거, 중복 개행 모두 제거, 특수문자/공통어 제거
			if(this.method == 1) { // For Wiki document
				docu = webTagElimination_for_Wiki(docu);
			}
			
			// 2. 주어진 문서를 문장 단위로 분리
			ArrayList<String> sentences = splitIntoSentences(docu);
			
			
			// 3. 각 문장에 대하여 토큰 생성
			StringBuffer wordList = new StringBuffer();
			String sen = null;
			for(int s=0; s < sentences.size(); s++) {
				sen = splitIntoTokens(sentences.get(s));
				if(sen==null || sen.length() ==0) continue;
				
				if(s==sentences.size()-1) wordList.append(sen);
				else wordList.append(sen).append(",");
			}
			
			
			// 4. 해당 문서의 총 단어 빈도수, 단어의 개수 (중복 X) 등의 통계와, 추출된 단어 리스트(중복 제거 안한 순수 리스트)를 하나의 객체로 반환 
			int frequency = -1;
			int numOfWords = -1;
			
			
			// 5. 결과 반환
			DocuWords result = new DocuWords(itemID, frequency, numOfWords, wordList);
			return result;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	// 1. 태그 제거, 특수문자 제거, 중복 공백 모두 제거, 중복 개행 모두 제거, 특수문자/공통어 제거 (WikiEncyclopedia Document 전용)
	public String webTagElimination_for_Wiki(String page) {
		try {
			String docu = page;
			docu = docu.toString().replaceAll("\\t", "").replaceAll("\\s{2,}"," "); // 중복 탭, 중복 공백 제거
			docu = docu.replace("Jump to:navigation, search", "").replace("\n", "<br>"); // 개행문자 임시 변경
			docu.replaceAll("<br><br>Contents(.*?)(See also|References|External links)<br><br>", ""); // Content (목차) 제거
			
			docu = docu.replace("Theatrical release poster", "");
			docu = docu.replace("<br>", "").
					replaceAll("<(section class=|li class=|ul class=|div|a href=|svg class=)(.*?)\">", "").
					replaceAll("<(path d=)(.*?)(\")>", "").
					replaceAll("<=ipl(.*?)>", "").
					replaceAll("<p>|</p>|<em>|</em>|</a>|</div>|</>|<br>|</br>|<ul>|</ul>|<li>|</li>|</svg>", "").
					replaceAll("&(.*?);", ""). 
					replace("/search/title?plot_author=", "").
					replace("&view=simple&sort=alpha&ref_=ttpl_pl_5", "").
					replace("</section>", "").
					replace("<", "").replace(">", "").replace("/", "").replace("(", "").replace(")", "");
			
			
			
			return docu;
		}catch(Exception e) {
			System.err.println("webTagElimination Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	
	
	// 2. 주어진 문서를 문장 단위로 분리
	public ArrayList<String> splitIntoSentences(String page) {
		try {
			ArrayList<String> sens = null;
			
			
			
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
			String tokens = null;
			
			
			
			return tokens;
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
