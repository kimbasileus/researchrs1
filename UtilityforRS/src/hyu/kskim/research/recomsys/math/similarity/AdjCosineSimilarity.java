package hyu.kskim.research.recomsys.math.similarity;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import hyu.kskim.research.recomsys.utility.ds.IndexPair;

public class AdjCosineSimilarity{
	DBManager db = new DBManager(null, null, null);
	public final int inf = -1;
	int numOfUsers;
	double userAvg[] = null; // Index is 1~N... (Size = the number of users +1)
	
	public AdjCosineSimilarity(double userAvg[], int numOfUsers) {
		this.numOfUsers = numOfUsers;
		
		if(userAvg!=null) this.userAvg = userAvg;
		else this.userAvg = new double[this.numOfUsers+1];
		
		this.userAvg[0] = -1;
		
		this.db.connectDB("root", "kyungsookim");
	}
	
	
	public void loadUserSimListFromDB(String schemaName) { // schemaName = movielens
		try {
			String sql = "SELECT ID, average FROM "+schemaName+".users;";
			
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			while(rs0.next()) {
				this.userAvg[rs0.getInt(1)] = rs0.getDouble(2);
			}
			
			rs0.close();
			
		}catch(Exception e) {
			System.err.println("loadUserSimListFromDB exception: "+e.getMessage());
		}
	}

	
	public double getSimilarity(double[] x, double[] y, Hashtable<Pair, Double> trainSets) {
		int len = x.length;
		for(int i=0; i < len; i++) {
			
		}
		
		return 0;
	}

	
	public double getSimilarity(ArrayList<IndexPair> x, ArrayList<IndexPair> y, Hashtable<Pair, Double> trainSets) {

		
		return 0;
	}
	
	
	
}
