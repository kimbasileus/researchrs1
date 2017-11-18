package hyu.kskim.research.rs.preexp.experiment;

import java.util.*;

import hyu.kskim.research.rs.utils.*;

import java.sql.*;

public class ExperimentInterface {
	Hashtable<Integer, Double> testSetList = null;
	Hashtable<Integer, Double> testResult = null;
	FileIO file = null;
	DBManager db = null;
	
	String dbUserName = null;
	String dbPassword = null;
	String dbSchema = null;
	String dir_experiment_result = null;
	
	
	int numOfUsers = 0;
	int numOfItems = 0;
	double ratioOfTestSets = 0;
	int numOfTestSets = 0;
	int numOfTrainingSets = 0;
	
	
	
	// 호출 1. 생성자
	public ExperimentInterface(int numOfUsers, int numOfItems, double ratioOfTestSets) {
		this.testSetList = new Hashtable<Integer, Double>();
		this.testResult = new Hashtable<Integer, Double>();
		this.file = new FileIO();
		
		this.numOfUsers = numOfUsers;
		this.numOfItems = numOfItems;
		this.ratioOfTestSets = ratioOfTestSets;
	}
	
	// 호출 2. DB정보 세팅
	public void initializeDB(String schema, String userName, String password, String dir_experiment_result) {
		try {
			this.db = new DBManager(null, null, null);
			this.db.connectDB(userName, password);
			this.dbSchema = schema;
			this.dir_experiment_result = dir_experiment_result;
		}catch(Exception e) {
			
		}
	}
	
	
	
	
	public double getPredictedRating(int userID, int itemID) {
		try {
			double rating = -1;
			
			// TODO: 여기에서 개발한 추천 알고리즘의 점수 예측 함수를 호출한다.
			
			return rating;
		}catch(Exception e) {
			
			return -1;
		}
	}
	
	
	public boolean isConverge(double preValue, double newValue, double threshold) {
		if( Math.abs(preValue-newValue) < threshold)
			return true;
		else return false;
	}
	
	
	
	//////////////////// Hashtable Interfaces /////////////////////
	public boolean loadTestSets() {
		try {
			
		//	this.numOfTestSets = 0;
		//	this.numOfTrainingSets = 0;
			return true;
		}catch(Exception e) {
			
			return false;
		}
	}
}
