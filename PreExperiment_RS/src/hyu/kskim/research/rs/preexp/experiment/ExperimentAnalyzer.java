package hyu.kskim.research.rs.preexp.experiment;

import hyu.kskim.research.rs.utils.DBManager;
import hyu.kskim.research.rs.utils.FileIO;
import java.util.*;
public class ExperimentAnalyzer {
	FileIO file = new FileIO();
	DBManager db = null;
	
	String title = "";
	String schema = "";
	String dir = "";
	int experimentID;
	
	public double ratioOfTestSet;
	public int numOfUsers;
	public int numOfItems;
	
	public int numOfTestSet;
	public int numOfSuccessTest;
	public int numOfFailTest;
	
	public double MAE; // mean absolute error
	public double RMSE; // root mean squared error
	public double coverage; // coverage
	public double precision; // precision
	public double recall; // recall
	public double f1; // F1 measure
	public double averageRunningTime; // running time
	
	TestSetPair[] successTestist = null;
	TestSetPair[] failTestList = null;
	
	// 생성자
	public ExperimentAnalyzer(String title, int numOfUsers, int numOfItems, double ratioOfTestSet, 
								int experimentID) {
		this.title = title;
		this.numOfUsers = numOfUsers;
		this.numOfItems = numOfItems;
		this.ratioOfTestSet = ratioOfTestSet;
		this.experimentID = experimentID;
	}
	
	// 호출 1.
	public void initDB(String dbSchema, String userName, String password, String dir) {
		this.schema = dbSchema;
		this.db = new DBManager(null, null, null);
		this.db.connectDB(userName, password);
		this.dir = dir;
	}
	
	// 호출 2.
	public void settingExperimentInfo(int numOfTestSet, Queue<TestSetPair> testSuccessList, Queue<TestSetPair> testFailList) {
		this.successTestist = testSuccessList.toArray(new TestSetPair[testSuccessList.size()]);
		this.failTestList = testFailList.toArray(new TestSetPair[testFailList.size()]);
		
		this.numOfTestSet = numOfTestSet;
		this.numOfSuccessTest = this.successTestist.length;
		this.numOfFailTest = this.failTestList.length;
	}
	
	
	// 호출 3. 
	public boolean computeBasicPerformance() {
		// 1. Compute MAE & RMSE
		double sumMAE = 0; double sumRMSE = 0; 
		for(int i=0; i < this.numOfSuccessTest; i++) {
			sumMAE += Math.abs( this.successTestist[i].prediction_error );
			sumRMSE += Math.pow(this.successTestist[i].prediction_error, 2);
		}
		
		this.MAE = sumMAE / (double)this.numOfSuccessTest;
		this.RMSE = Math.sqrt( sumRMSE / (double)this.numOfSuccessTest );
		this.coverage = (double)(this.numOfSuccessTest / this.numOfTestSet);
		
		System.out.println("\t MAE: "+this.MAE);
		System.out.println("\t RMSE: "+this.RMSE);
		System.out.println("\t Coverage: "+this.coverage);
		return true;
	}
	
	
	// 호출 4. 
	public boolean computeF1Performance() {
		double sum1 = 0;
		for(int i=0; i < this.numOfSuccessTest; i++) {
			
		}
		
		
		
		System.out.println("\t Precision: "+this.precision);
		System.out.println("\t Recall: "+this.recall);
		System.out.println("\t F1-Measure: "+this.f1);
		return true;
	}
	
	
	public void exportAnalysisResults() {
		
	}
}

