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
	//Hashtable<Integer, Hashtable<Integer, Double> > R = null;
	String dbSchema = null;
	int numOfRatings = 0;
	
	int numOfUsers = 0;
	int numOfItems = 0;
	
	public UserRatingMatrix_hashTable(String dbSchema, int numOfUsers, int numOfItems) {
		this.dbSchema = dbSchema;
		this.R = new Hashtable<IndexPair, Double>();
		//this.R = new Hashtable<Integer, Hashtable<Integer, Double>>();
		this.db = new DBManager(null, null, null);
		this.db.connectDB("root", "kyungsookim");
		
		this.numOfUsers = numOfUsers;
		this.numOfItems = numOfItems;
	}
	
	public void putRating(int userID, int itemID, double rating) {
		R.put(new IndexPair(userID, itemID), rating);
	}
	
	public double getRating(int userID, int itemID) {
		IndexPair key = new IndexPair(userID, itemID);
		if(this.R.containsKey(key)) return this.R.get(key);
		else return -1;
	}
	
	public boolean removeRating(int userID, int itemID) {
		IndexPair key = new IndexPair(userID, itemID);
		if(this.R.containsKey(key)) {
			this.R.remove(new IndexPair(userID, itemID));
			return true;
		}
		else
			return false;
	}
	
	public boolean isEmpty() {
		if(this.numOfRatings == 0) return true;
		else return false;
	}
	
	public int getNumOfRatings() {
		return this.numOfRatings;
	}
		
	
	public void loadUserRatings(boolean isFromItems) {
		try {
			if(isFromItems) { // From items......
				for(int itemID=1; itemID<=this.numOfItems; itemID++) {
					this.loadUserRatings_fromItem(itemID);
				}
			}else { // From users......
				for(int userID=1; userID<=this.numOfUsers; userID++) {
					this.loadUserRatings_fromUser(userID);
				}
			}
			
			System.out.println("Complete loading all ratings : "+this.numOfRatings+"\t"+this.R.size());
			
		}catch(Exception e) {
			System.err.println("loadUserRatings Exception: "+e.getMessage());
		}
	}
	
		
	
	private void loadUserRatings_fromUser(int userID) {
		try {
			String sql = "select itemID, rating from `"+this.dbSchema+"`.`ratings_trainset` where userID = '"+userID+"';";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			
			int itemID; double rating;
			while(rs0.next()) {
				itemID = rs0.getInt(1);
				rating = rs0.getDouble(2);
				this.putRating(userID, itemID, rating);
				numOfRatings++;
			}			
			rs0.close();	
			
			System.out.println("\t\t- Complete loading user "+userID+"'s ratings into the hashtable.");
		}catch(SQLException e) {
			System.err.println("loadUserRatings_fromUser Exception: "+e.getMessage());
			System.err.println("\t\t- "+e.getSQLState());
		}
	}

	
	private void loadUserRatings_fromItem(int itemID) {
		try {
			String sql = "select userID, rating from `"+this.dbSchema+"`.`ratings_trainset` where itemID = '"+itemID+"';";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			
			int userID; double rating;
			while(rs0.next()) {
				userID = rs0.getInt(1);
				rating = rs0.getDouble(2);
				this.putRating(userID, itemID, rating);
				numOfRatings++;
			}			
			rs0.close();			
			
			System.out.println("\t\t- Complete loading item "+itemID+"'s ratings into the hashtable.");
		}catch(SQLException e) {
			System.err.println("loadUserRatings_fromItem Exception: "+e.getMessage());
			System.err.println("\t\t- "+e.getSQLState());
		}
	}
}
