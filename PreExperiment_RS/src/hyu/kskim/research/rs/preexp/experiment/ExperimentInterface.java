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
	
	// ȣ�� 1. ������
	public ExperimentInterface(int numOfUsers, int numOfItems, double ratioOfTestSets) {
		this.testSetList = new LinkedList<TestSetPair>();
		this.testSuccessList = new LinkedList<TestSetPair>();
		this.testFailList = new LinkedList<TestSetPair>();
		
		this.file = new FileIO();
		
		this.numOfUsers = numOfUsers;
		this.numOfItems = numOfItems;
		this.ratioOfTestSets = ratioOfTestSets;
	}
	
	// ȣ�� 2. DB���� ����
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
	
	// ȣ�� 3. DB�κ��� Test Dataset �ε�
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
	
	
	// ȣ�� 4. ���� ���� �Լ�
	public boolean run() {
		try {
			if(methodID == 101) { // Item-based CF
				this.ibc = new ItemBasedCF("movielens", 671, 9125, 30);
				ibc.initRecommenderEngine();
			}else {
				System.out.println("��õ���� ���� �Ұ�!!");
				return false;
			}
			
			
			int countTestItem = 0;
			TestSetPair testSet = null;
			
			// �ռ� "ȣ�� 3"���� �ε��� ��� Test Dataset�� ���Ͽ� ������ �����Ѵ�.
			int count = 0;
			int previousItemID = -1;
			ArrayList<SimPair> simList = null;
						
			while(countTestItem < this.numOfTestSets && !this.testSetList.isEmpty()) {
				testSet = this.testSetList.remove();
				double predicted_rating = -1;
				
				
				// test item�� ����Ǹ� ���� ���� ������ ����Ʈ�� �ε��Ѵ�.
				if(previousItemID != testSet.itemID) {
					simList = this.ibc.loadSimilarityList(testSet.itemID);
				}
				// test set �� ���� rating ���
				count++;
				predicted_rating = getPredictedRating(testSet.userID, testSet.itemID, simList);
								
				// ������ �׽�Ʈ ������ ID�� ����
				previousItemID = testSet.itemID;
				
				// rating ���� ������ ���л�ʷ� ������ �����Ѵ�.
				if(predicted_rating == -1) { // Rating ��� ����
					testSet.predicted_rating = -1;
					this.testFailList.add(testSet);
				}else { // Rating ��� ����
					testSet.predicted_rating = predicted_rating;
					testSet.prediction_error = testSet.actual_rating - predicted_rating;
					this.testSuccessList.add(testSet);
					System.out.println(count+"]\t User "+testSet.userID+", item "+testSet.itemID+": rating "+predicted_rating);
				}
				
				if(count==7000) break;
			}
			
			int numOfTotalTestSet = this.numOfTestSets; // ��ü �׽�Ʈ���̽��� ��
			int numOfPredictedTestSet = this.testSuccessList.size(); // ���� ������ ������ �׽�Ʈ ���̽��� ��
			int numOfFailTestSet = this.testFailList.size(); // ���� ������ ������ �׽�Ʈ ���̽��� ��
						
			
			System.out.println("��ü �׽�Ʈ ���̽� ��: "+numOfTotalTestSet);
			//System.out.println("���� ������ ������ �׽�Ʈ ���̽��� ��: "+numOfPredictedTestSet);
			//System.out.println("���� ������ ������ �׽�Ʈ ���̽��� ��: "+numOfFailTestSet);
			
			this.analysisResults(numOfTotalTestSet);
			return true;
		}catch(Exception e) {
			
			return false;
		}
	}
	
	
	// ȣ�� 5. ���� ��� �м� �Լ�
	public boolean analysisResults(int numOfTestSet) {
		try {
			String experimentTitle = "����1_���� ItembasedCF";
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
			
			// TODO: ���⿡�� ������ ��õ �˰����� ���� ���� �Լ��� ȣ���Ѵ�.
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