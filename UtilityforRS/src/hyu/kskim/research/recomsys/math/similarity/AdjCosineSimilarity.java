package hyu.kskim.research.recomsys.math.similarity;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import hyu.kskim.research.recomsys.utility.ds.IndexPair;

public class AdjCosineSimilarity{
	DBManager db = new DBManager(null, null, null);
	public final int inf = -1;
	
	public AdjCosineSimilarity() {
		this.db.connectDB("root", "kyungsookim");
	}
	
	
	public double[] loadUserSimListFromDB(int numOfUsers, String schemaName) { // schemaName = movielens
		try {
			double userAvgList[] = new double[numOfUsers+1];
			String sql = "SELECT userID, average FROM "+schemaName+".user_avg;";
			
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			userAvgList[0] = -1;
			while(rs0.next()) {
				userAvgList[rs0.getInt(1)] = rs0.getInt(2);
			}
			rs0.close();
			
			return userAvgList;
		}catch(Exception e) {
			System.err.println("loadUserSimListFromDB exception: "+e.getMessage());
			return null;
		}
	}

	
	public ArrayList<IndexPair> getItemVector_as_List(int itemID){
		try {
			
			
			
			
			
			ArrayList<IndexPair> list = new ArrayList<IndexPair>();
						
			return list;
		}catch(Exception e) {
			System.err.println("getItemVector_as_List exception: "+e.getMessage());
			return null;
		}
	}
	
	public double getSimilarity(double[] x, double[] y) {
		int len = x.length;
		for(int i=0; i < len; i++) {
			
		}
		
		return 0;
	}

	
	public double getSimilarity(ArrayList<IndexPair> x, ArrayList<IndexPair> y) {
		
		
		return 0;
	}
	
	
	
}


class IndexPair{
	public int index;
	public double value;
	
	public IndexPair(int index, double value) {
		this.index = index;
		this.value = value;
	}
}