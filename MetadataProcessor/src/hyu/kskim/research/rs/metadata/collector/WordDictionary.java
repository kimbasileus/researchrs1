package hyu.kskim.research.rs.metadata.collector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;

import hyu.kskim.research.rs.metadata.tools.indexer.InvertedIndexer;
import hyu.kskim.research.rs.utils.DBManager;
import hyu.kskim.research.rs.utils.FileIO;

/*
 * Part 3. 전처리된 모든 아이템들의 문서로부터 모든 단어들을 추출하여 사전을 생성하는 기능
 */
public class WordDictionary {
	String schema = null;
	String dir = null;
	int numOfItems =-1;
	DBManager db = null;
	FileIO file = null;
	InvertedIndexer indexer = null;
	
	public WordDictionary(String schema, String dir, int numOfItems) {
		this.schema = schema;
		this.dir = dir;
		this.numOfItems = numOfItems;
		this.db = new DBManager(null, null, null);
		this.db.connectDB("root", "kyungsookim");
		this.file = new FileIO();
		this.indexer = new InvertedIndexer();
	}
	
	
	public boolean readAllWordsFromDB(int itemID) {
		try {
			int fIndex = (itemID/2000)+1;
			if(itemID%2000==0) fIndex--;
			
			// 0. DB로부터 메타데이터 문서 로드
			String sql = "SELECT words FROM movielens.items_metadata where itemID = "+itemID+";";
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
					String word = line2[j].replace(" ", "");
					if(word==null || word.length()<=1) continue;
					
					indexer.addWord(word, itemID, 1); // Index file에 해당 단어를 삽입 (word, 문서ID, 단어 빈도수)
				}
			}
			
			return true;
		}catch(Exception e) {
			System.err.println("getFeatureElementList Exception: "+e.getMessage());
			return false;
		}
	}
}
