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
			// 0. ��ũ�κ��� ��Ÿ������ ���� �ε�
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
			
			
			// 1. �±� ����, Ư������ ����, �ߺ� ���� ��� ����, �ߺ� ���� ��� ����, Ư������/����� ����
			if(this.method == 1) { // For Wiki document
				docu = webTagElimination_for_Wiki(docu);
			}
			
			// 2. �־��� ������ ���� ������ �и�
			ArrayList<String> sentences = splitIntoSentences(docu);
			
			
			// 3. �� ���忡 ���Ͽ� ��ū ����
			StringBuffer wordList = new StringBuffer();
			String sen = null;
			for(int s=0; s < sentences.size(); s++) {
				sen = splitIntoTokens(sentences.get(s));
				if(sen==null || sen.length() ==0) continue;
				
				if(s==sentences.size()-1) wordList.append(sen);
				else wordList.append(sen).append(",");
			}
			
			
			// 4. �ش� ������ �� �ܾ� �󵵼�, �ܾ��� ���� (�ߺ� X) ���� ����, ����� �ܾ� ����Ʈ(�ߺ� ���� ���� ���� ����Ʈ)�� �ϳ��� ��ü�� ��ȯ 
			int frequency = -1;
			int numOfWords = -1;
			
			
			// 5. ��� ��ȯ
			DocuWords result = new DocuWords(itemID, frequency, numOfWords, wordList);
			return result;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	// 1. �±� ����, Ư������ ����, �ߺ� ���� ��� ����, �ߺ� ���� ��� ����, Ư������/����� ���� (WikiEncyclopedia Document ����)
	public String webTagElimination_for_Wiki(String page) {
		try {
			String docu = page;
			docu = docu.toString().replaceAll("\\t", "").replaceAll("\\s{2,}"," "); // �ߺ� ��, �ߺ� ���� ����
			docu = docu.replace("Jump to:navigation, search", "").replace("\n", "<br>"); // ���๮�� �ӽ� ����
			docu.replaceAll("<br><br>Contents(.*?)(See also|References|External links)<br><br>", ""); // Content (����) ����
			
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
	
	
	
	
	// 2. �־��� ������ ���� ������ �и�
	public ArrayList<String> splitIntoSentences(String page) {
		try {
			ArrayList<String> sens = null;
			
			
			
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
