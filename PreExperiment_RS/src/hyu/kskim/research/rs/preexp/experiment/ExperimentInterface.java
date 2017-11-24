package hyu.kskim.research.rs.preexp.experiment;

import java.util.*;

import hyu.kskim.research.rs.preexp.alg.trad.ItemBasedCF;
import hyu.kskim.research.rs.preexp.ds.SimPair;
import hyu.kskim.research.rs.utils.*;
import java.sql.*;


public class ExperimentInterface {
	Queue<TestSetPair> testSetList = null;
	Queue<TestSetPair> testSuccessList = null;
	Queue<TestSetPair> testFailList = null;
	
	FileIO file = null;
	DBManager db = null;
	ExperimentAnalyzer expAnalyzer = null;
	
	String dbUserName = null;
	String dbPassword = null;
	String dbSchema = null;
	String dir_experiment_result = null;
	
	
	int numOfUsers = 0;
	int numOfItems = 0;
	double ratioOfTestSets = 0;
	int numOfTestSets = 0;
	int numOfTrainingSets = 0;
	
	int methodID = 0;
	
	ItemBasedCF ibc = null;
	
	// 호출 1. 생성자
	public ExperimentInterface(int numOfUsers, int numOfItems, double ratioOfTestSets) {
		this.testSetList = new LinkedList<TestSetPair>();
		this.testSuccessList = new LinkedList<TestSetPair>();
		this.testFailList = new LinkedList<TestSetPair>();
		
		this.file = new FileIO();
		
		this.numOfUsers = numOfUsers;
		this.numOfItems = numOfItems;
		this.ratioOfTestSets = ratioOfTestSets;
	}
	
	// 호출 2. DB정보 세팅
	public void initializeDB(String schema, String userName, String password, String dir_experiment_result,
			int methodID) {
		try {
			this.db = new DBManager(null, null, null);
			this.db.connectDB(userName, password);
			this.dbSchema = schema;
			this.dir_experiment_result = dir_experiment_result;
			this.methodID = methodID;
		}catch(Exception e) {
			
		}
	}
	
	// 호출 3. DB로부터 Test Dataset 로딩
	public boolean loadTestSets() {
		try {
			String sql = "SELECT userID, itemID, actual_rating FROM "+this.dbSchema+".ratings_testset order by itemID asc;";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			
			
			while(rs0.next()) {
				this.testSetList.add(new TestSetPair(rs0.getInt(1), rs0.getInt(2),  rs0.getDouble(3)));
			}
			this.numOfTestSets = this.testSetList.size();
			
			///////////////////
			
			//System.out.println(this.numOfTestSets);
			
			rs0.close();
			return true;
		}catch(SQLException e) {
			
			return false;
		}
	}
	
	
	// 호출 4. 실험 진행 함수
	public boolean run() {
		try {
			if(methodID == 101) { // Item-based CF
				this.ibc = new ItemBasedCF("movielens", 671, 9125, 30);
				ibc.initRecommenderEngine();
			}else {
				System.out.println("추천엔진 선택 불가!!");
				return false;
			}
			
			
			int countTestItem = 0;
			TestSetPair testSet = null;
			
			// 앞서 "호출 3"에서 로딩된 모든 Test Dataset에 대하여 실험을 진행한다.
			int count = 0;
			int previousItemID = -1;
			ArrayList<SimPair> simList = null;
						
			while(countTestItem < this.numOfTestSets && !this.testSetList.isEmpty()) {
				testSet = this.testSetList.remove();
				double predicted_rating = -1;
				
				
				// test item이 변경되면 새로 유사 아이템 리스트를 로딩한다.
				if(previousItemID != testSet.itemID) {
					simList = this.ibc.loadSimilarityList(testSet.itemID);
				}
				// test set 에 대한 rating 계산
				count++;
				predicted_rating = getPredictedRating(testSet.userID, testSet.itemID, simList);
								
				// 현재의 테스트 아이템 ID를 저장
				previousItemID = testSet.itemID;
				
				// rating 예측 성공과 실패사례로 나누어 저장한다.
				if(predicted_rating == -1) { // Rating 계산 실패
					testSet.predicted_rating = -1;
					this.testFailList.add(testSet);
				}else { // Rating 계산 성공
					testSet.predicted_rating = predicted_rating;
					testSet.prediction_error = testSet.actual_rating - predicted_rating;
					this.testSuccessList.add(testSet);
					System.out.println(count+"]\t User "+testSet.userID+", item "+testSet.itemID+": rating "+predicted_rating);
				}
				
				if(count==7000) break;
			}
			
			int numOfTotalTestSet = this.numOfTestSets; // 전체 테스트케이스의 수
			int numOfPredictedTestSet = this.testSuccessList.size(); // 평점 예측에 성공한 테스트 케이스의 수
			int numOfFailTestSet = this.testFailList.size(); // 평점 예측에 실패한 테스트 케이스의 수
						
			
			System.out.println("전체 테스트 케이스 수: "+numOfTotalTestSet);
			//System.out.println("평점 예측에 성공한 테스트 케이스의 수: "+numOfPredictedTestSet);
			//System.out.println("평점 예측에 실패한 테스트 케이스의 수: "+numOfFailTestSet);
			
			this.analysisResults(numOfTotalTestSet);
			return true;
		}catch(Exception e) {
			
			return false;
		}
	}
	
	
	// 호출 5. 실험 결과 분석 함수
	public boolean analysisResults(int numOfTestSet) {
		try {
			String experimentTitle = "실험1_고전 ItembasedCF";
			int experimentID = 1658;
			
			// 1. Setting
			this.expAnalyzer = new ExperimentAnalyzer(experimentTitle, numOfUsers, numOfItems, ratioOfTestSets, experimentID);
			this.expAnalyzer.initDB(dbSchema, "root", "kyungsookim", "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\experiments");
			this.expAnalyzer.settingExperimentInfo(numOfTestSet, testSuccessList, testFailList);
			
			
			// 2. Analysis
			this.expAnalyzer.computeBasicPerformance();
			
			
			// 3. Print result
			this.expAnalyzer.exportAnalysisResults();
			
			return true;
		}catch(Exception e) {
			
			return false;
		}
	}
	
	
	public double getPredictedRating(int userID, int itemID, ArrayList<SimPair> simList) {
		try {
			double rating = -1;
			
			// TODO: 여기에서 개발한 추천 알고리즘의 점수 예측 함수를 호출한다.
			rating = ibc.getRating(userID, itemID, simList);
			
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
	
}