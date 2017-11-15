package hyu.kskim.research.rs.preexp.ds;

import java.sql.*;
import java.util.*;

import hyu.kskim.research.rs.utils.DBManager;

/*
 * User-Item Rating Matrix를 희소 행렬의 형태로 메모리에 유지하고, 이들에 대한 접근 메소드들을 제공한다.
 */
public class UserRatingMatrix_hashTable implements SparseMatrix{
	DBManager db = null;
	Hashtable<IndexPair, Double> R  = null;
	String dbSchema = null;
	int numOfRatings = 0;
	
	public UserRatingMatrix_hashTable(String dbSchema) {
		this.dbSchema = dbSchema;
		this.R = new Hashtable<IndexPair, Double>();
		this.db = new DBManager(null, null, null);
	}
	
	public void putRating(int userID, int itemID, double rating) {
		R.put(new IndexPair(userID, itemID), rating);
	}
	
	public double getRating(int userID, int itemID) {
		return R.get(new IndexPair(userID, itemID) );
	}
	
	public double removeRating(int userID, int itemID) {
		return R.remove(new IndexPair(userID, itemID));
	}
	
	public boolean isEmpty() {
		if(this.numOfRatings == 0) return true;
		else return false;
	}
	
	public int getNumOfRatings() {
		return this.numOfRatings;
	}
	
	
	public void loadUserRatings(int userID) {
		try {
			String sql = "select itemID, rating from `"+this.dbSchema+"`.`ratings` where userID = '"+userID+"';";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			
			int itemID; double rating;
			while(rs0.next()) {
				itemID = rs0.getInt(1);
				rating = rs0.getDouble(2);
				R.put(new IndexPair(userID, itemID), rating);
				numOfRatings++;
			}			
			rs0.close();			
		}catch(SQLException e) {
			System.err.println("loadUserRatings_fromUser Exception: "+e.getMessage());
			System.err.println("\t\t- "+e.getSQLState());
		}
	}

	
	public void loadUserRatings_fromItem(int itemID) {
		// TODO Auto-generated method stub
		try {
			String sql = "select userID, rating from `"+this.dbSchema+"`.`ratings` where itemID = '"+itemID+"';";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			
			int userID; double rating;
			while(rs0.next()) {
				userID = rs0.getInt(1);
				rating = rs0.getDouble(2);
				R.put(new IndexPair(userID, itemID), rating);
				numOfRatings++;
			}			
			rs0.close();			
		}catch(SQLException e) {
			System.err.println("loadUserRatings_fromItem Exception: "+e.getMessage());
			System.err.println("\t\t- "+e.getSQLState());
		}
	}	
}
