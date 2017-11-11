package hyu.kskim.research.recomsys.math.similarity;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import hyu.kskim.research.recomsys.utility.ds.IndexPair;

public class AdjCosineSimilarity{
	DBManager db = new DBManager(null, null, null);
	public final int inf = -1;
	
	
	public AdjCosineSimilarity(double userAvg[], int numOfUsers) {
		this.db.connectDB("root", "kyungsookim");
	}
	
	
	public Hashtable<Integer, Double> loadUserSimListFromDB(String schemaName) { // schemaName = movielens
		try {
			Hashtable<Integer, Double> userAvgList = new Hashtable<Integer, Double>();
			String sql = "SELECT userID, average FROM "+schemaName+".user_avg;";
			
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			while(rs0.next()) {
				userAvgList.put(rs0.getInt(1), rs0.getDouble(2));
			}
			rs0.close();
			
			return userAvgList;
		}catch(Exception e) {
			System.err.println("loadUserSimListFromDB exception: "+e.getMessage());
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
