package hyu.kskim.research.rs.preexp.alg.trad;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.*;
import hyu.kskim.research.rs.preexp.ds.*;
import hyu.kskim.research.rs.preexp.ds.UserRatingMatrix_hashTable;
import hyu.kskim.research.rs.utils.DBManager;
import hyu.kskim.research.rs.utils.FileIO;

public class ItemBasedCF{
	String dbSchema;
	DBManager db = null;
	FileIO file = null;
	
	int numOfUsers;
	int numOfItems;
	
	int threshold = 30;
	
	UserRatingMatrix_hashTable ratings = null;
	
	// 0. 생성자
	public ItemBasedCF(String dbSchema, int numOfUsers, int numOfItems, int threshold) {
		System.out.println( System.getProperty("os.name") );
		db = new DBManager(null, null, null);			
		db.connectDB("root", "kyungsookim");
		this.dbSchema = dbSchema;
		file = new FileIO();
		
		this.numOfUsers = numOfUsers;
		this.numOfItems = numOfItems;
		this.threshold = threshold;
		
		this.ratings = new UserRatingMatrix_hashTable(dbSchema, numOfUsers, numOfItems);
	}
	
	// 소멸자
	public void closeItemBasedCF() {
		this.db.closeDB();
	}
	
	// 1. 추천 시스템 초기화
	//@Override
	public void initRecommenderEngine() {
		this.ratings.loadUserRatings(false);
	}
	
	
	// 2. 주어진 사용자의 아이템에 대한 평점을 예측하는 함수
	
	public double getRating(int userID, int itemID, ArrayList<SimPair> simList) {
		try {
			//ArrayList<SimPair> simList = loadSimilarityList(itemID);
			
			if(simList == null) {
				//System.out.println("유사 아이템 추출 불가");
				return -1;
			}
			
			//System.out.println("유사 아이템 수: "+simList.size());
			int length = simList.size();
			double H = 0; double L = 0; int id; double sim; double rating; int count = 0;
			for(int i=0 ; i < length; i++) {
				if(count > this.threshold) break;
				
				id = simList.get(i).itemID;
				sim = simList.get(i).similarity;
				rating = this.ratings.getRating(userID, id);
				if(rating == -1) continue;
				
				H += sim*rating;
				L += Math.abs(sim);
				//System.out.println("similar item "+id+"\t rating: "+rating+"\t sim:"+sim);
				count++;
			}
			
			if(L==0 || H<0) {
				//System.out.println("유사도 계산 불가: H is "+H+"\t L is "+L);
				return -1;
			}
			
			double finalRating = H/L;
			if(finalRating >0 && finalRating <1) finalRating = 1;
			return finalRating;
		}catch(Exception e) {
			System.out.println("getRating Exception: "+e.getMessage());
			return -1;
		}
	}
	
	// 주어진 아이템과 다른 아이템들간의 유사도값을 DB로부터 로딩하는 함수. 이 때 DB는 반드시 각각의 ItemID에 대하여 indexing이 사전에 완료되어야 한다.
	public ArrayList<SimPair> loadSimilarityList(int targetItemID){
		try {
			ArrayList<SimPair> list = new ArrayList<SimPair>();
			
			String sql = "SELECT item1ID, item2ID, similarity FROM "+this.dbSchema+".ist "
					+ "where item1ID = "+targetItemID+" or item2ID = "+targetItemID+" order by similarity desc;";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int item1ID; int item2ID; double sim;
			while(rs0.next()) {
				item1ID = rs0.getInt(1);
				item2ID = rs0.getInt(2);
				sim = rs0.getDouble(3);
				
				if(item1ID==targetItemID) {
					list.add(new SimPair(item2ID, sim));
				}else {
					list.add(new SimPair(item1ID, sim));
				}
			}
			
			rs0.next();
			if(list.size()==0) return null;
			
			return list;
		}catch(SQLException e) {
			System.out.println("loadSimilarityList Exception: "+e.getMessage()+" // "+e.getSQLState());
			return null;
		}
	}
}



