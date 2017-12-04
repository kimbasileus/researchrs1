package hyu.kskim.research.rs.metadata.collector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import hyu.kskim.research.rs.metadata.tools.indexer.InvertedIndexer;
import hyu.kskim.research.rs.metadata.tools.indexer.WordFreq;
import hyu.kskim.research.rs.utils.DBManager;
import hyu.kskim.research.rs.utils.FileIO;

/*
 * Part 3. ��ó���� ��� �����۵��� �����κ��� ��� �ܾ���� �����Ͽ� ������ �����ϴ� ���
 */
public class WordDictionary {
	String schema = null;
	String dir = null;
	int numOfItems =-1;
	DBManager db = null;
	FileIO file = null;
	InvertedIndexer indexer = null;
	
	// ��� �����۵鿡 ���� ���� �� ���� ���/�л� ������ �����ϴ� �ڷᱸ��
	Hashtable<Integer, ItemAvgNode> itemAvgs = null;
	double sumAllRating = 0; double sumAllNum = 0; double avgAll = 0; double dev = 0; 
	
	
	public WordDictionary(String schema, String dir, int numOfItems) {
		this.schema = schema;
		this.dir = dir;
		this.numOfItems = numOfItems;
		this.db = new DBManager(null, null, null);
		this.db.connectDB("root", "kyungsookim");
		this.file = new FileIO();
		this.indexer = new InvertedIndexer();
		this.itemAvgs = new Hashtable<Integer, ItemAvgNode>();
	}
	
	
	public boolean readAllWordsFromDB() {
		try {
			// 1. 
			for(int itemID = 1; itemID <= this.numOfItems; itemID++)
			{
				readAllWordsFromDB(itemID);
				System.out.println("Item "+itemID+" ó�� �Ϸ�.");
			}
			
			System.out.println("��ü �ܾ� �ε� �Ϸ�: ��ü �ܾ� ����: "+this.indexer.getNumOfTotalWords());
			
			
			// 2. 
			this.db.getStmt().executeUpdate("delete FROM knowledgebase.dic_"+this.schema+"_small;");
			ArrayList<String> wordList = this.indexer.allWordList();
			for(int i=0; i < wordList.size(); i++) {
				computeBasicStat(i+1, wordList.get(i));
			}
			
			
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	public boolean readAllWordsFromDB(int itemID) {
		try {
			int fIndex = (itemID/2000)+1;
			if(itemID%2000==0) fIndex--;
			
			// 0. DB�κ��� ��Ÿ������ ���� �ε�
			String sql = "SELECT words FROM "+this.schema+".items_metadata where itemID = "+itemID+";";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			String line0 = "";
			while(rs0.next()) {
				line0 = rs0.getString(1);
			}
			rs0.close();
			
			String line1[] = null;
			line1 = line0.split("/");
			if(line1==null) return false;
			
			String line2[] = null;
			for(int i=0; i < line1.length; i++) { // for each line in the document
				if(line1[i]==null || line1[i].length()<=1) continue;
				
				line2 = line1[i].split(",");
				if(line2 == null) continue;
				
				for(int j=0; j < line2.length; j++) { // for each word in the line
					String word = line2[j].replace(" ", "").replaceAll("[0-9]", "")
							.replace(".", "").replace("=", "").replace("-", "").replace("_", "");
					
					if(word==null || word.length()<=2) continue;
					
					indexer.addWord(word, itemID, 1); // Index file�� �ش� �ܾ ���� (word, ����ID, �ܾ� �󵵼�)
				}
			}
			
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	public boolean computeBasicStat(int id, String word) {
		try {
			int term_freq = 0;
			int doc_freq = 0;
			ArrayList<WordFreq> list = new ArrayList<WordFreq>();
			StringBuffer list2 = new StringBuffer();
			
			term_freq = this.indexer.getNumOfTotalFreq(word);
			doc_freq = this.indexer.getNumOfTotalDocs(word);
			list = this.indexer.getNAlllDocs_and_Freq_having_word(word);
			
			for(int i=0; i < list.size(); i++) {
				WordFreq obj = list.get(i);
				
				if(i==list.size()-1) list2.append(obj.itemID+","+obj.freq);
				else list2.append(obj.itemID+","+obj.freq).append("/");
			}
			
			
			String list_final = null;
			if(list2.length()==0) list_final = "";
			else list_final = list2.toString();
			
			String sql = "INSERT INTO `knowledgebase`.`dic_"+this.schema+"_small` "
					+ "(`ID`, `word`, `term_freq`, `doc_freq`, `term_freq_docs`) "
					+ "VALUES ('"+id+"', '"+word+"', '"+term_freq+"', '"+doc_freq+"', '"+list_final+"');";
			
			this.db.getStmt().executeUpdate(sql);
			
			System.out.println(id+"] �ܾ� "+word+" �� ���� ��� ó�� �Ϸ�: "+term_freq+", "+doc_freq+", ["+list_final+"]");
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	public boolean loadItemAvgList() {
		try {
			String sql = "SELECT * FROM "+this.schema+".item_avg;";
			
			// ��� �����۵��� ���� ����� ���������� �ε��Ѵ�.
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			
			int itemID = 0; double avg = 0; int numOfRating = 0; double sumOfRating = 0;
			while(rs0.next()) {
				itemID = rs0.getInt(1);
				avg = rs0.getDouble(2);
				numOfRating = rs0.getInt(3);
				sumOfRating = rs0.getDouble(4);
				
				this.itemAvgs.put(itemID, new ItemAvgNode(avg, numOfRating, sumOfRating));
				System.out.println("Item "+itemID+" �� ���� ������ ��հ� �ε� �Ϸ�.");
			}
			rs0.close();
			
			
			// ���  �����۵��� �� ���� ����� ����Ѵ�.
			Enumeration<Integer> keys = this.itemAvgs.keys();
			while(keys.hasMoreElements()) {
				int ID = keys.nextElement();
				sumAllRating += this.itemAvgs.get(ID).sumOfRating;
				sumAllNum += this.itemAvgs.get(ID).numOfRating;
				System.out.println(ID+" ��° ������ ���� ���� ��: "+sumAllRating+"\t ���� ���� ��: "+sumAllNum);
			}			
			
			if(sumAllNum==0) 
				this.avgAll = -1;
			else 
				this.avgAll = sumAllRating/sumAllNum;
			
			System.out.println("��� ���� ���: "+this.avgAll);
			return true;
		}catch(Exception e) {
			System.err.println("loadItemAvgList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	public boolean computeAdvancedStat(int id) {
		try {
			String sql = "SELECT term_freq, doc_freq, term_freq_docs FROM knowledgebase.dic_"+this.schema+"_small where id = "+id+";";
			int term_freq = 0;
			int doc_freq = 0;
			String term_freq_docs[] = null;
			double avg_ratings = 0;
			double avg_not_ratings = 0;
			
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			while(rs0.next()) {
				term_freq = rs0.getInt(1);
				doc_freq = rs0.getInt(2);
				term_freq_docs = rs0.getString(3).split("/");
			}
			rs0.close();
			
			
			
			ArrayList<WordFreq> docList = getWordFreqList(term_freq_docs);
			// �ش� �ܾ �����ϴ� �����۵��� ���� ����� ����Ѵ�.
			double sumRating = 0; double sumNum = 0;
			for(int i=0; i < docList.size(); i++) {
				int itemID = docList.get(i).itemID;
				sumRating += this.itemAvgs.get(itemID).sumOfRating;
				sumNum += this.itemAvgs.get(itemID).numOfRating;
				System.out.println(id+" ��° �ܾ� �����ϴ� ������: "+itemID+"\t ���� ���� ��: "+sumRating+"\t ���� ���� ��: "+sumNum);
			}
			if(sumNum==0) avg_ratings = -1;
			else {
				avg_ratings = sumRating/sumNum;
			}
			
			
			// �ش� �ܾ �������� �ʴ� �����۵��� ���� ����� ����Ѵ�.
			double sumNotRating = 0; double sumNotNum = 0;
			sumNotRating = sumAllRating - sumRating;
			sumNotNum = sumAllNum - sumNum;
			if(sumNotNum == 0)  avg_not_ratings = -1;
			else
				avg_not_ratings = sumNotRating/sumNotNum;
			
			
			System.out.println(id+" ��° �ܾ� ��� ó�� �Ϸ�: [���� ������ ���]: "+avg_ratings+"\t[������ ������ ���]: "+avg_not_ratings);
			
			//System.out.println(id+"] �ܾ� "+word+" �� ���� ��� ó�� �Ϸ�: "+term_freq+", "+doc_freq+", ["+list_final+"]");
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	public ArrayList<WordFreq> getWordFreqList(String[] list){
		try {
			ArrayList<WordFreq> newList = new ArrayList<WordFreq>();
			
			String temp[] = null; int docID=0; int freq = 0;
			for(int i=0; i < list.length; i++) {
				temp = list[i].split(",");
				
				docID = Integer.parseInt(temp[0]);
				freq = Integer.parseInt(temp[1]);
				
				newList.add(new WordFreq(docID, freq));
			}
			
			if(newList.size()==0) return null;
			else
				return newList;
		}catch(Exception e) {
			System.err.println("getWordFreqList Exception: "+e.getMessage());
			return null;
		}
	}
	
}


class ItemAvgNode{
	public double average;
	public int numOfRating;
	public double sumOfRating;
	
	public ItemAvgNode(double average, int numOfRating, double sumOfRating) {
		this.average = average;
		this.numOfRating = numOfRating;
		this.sumOfRating = sumOfRating;
	}
}
