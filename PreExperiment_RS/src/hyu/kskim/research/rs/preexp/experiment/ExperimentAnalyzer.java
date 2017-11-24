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
	public double precision_MAE; // precision using MAE
	public double precision_RMSE; // precision using RMSE
	public double f1_MAE; // F1 measure using MAE
	public double f1_RMSE; // F1 measure using RMSE
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
		// 1. Compute performance
		double sumMAE = 0; double sumRMSE = 0; 
		for(int i=0; i < this.numOfSuccessTest; i++) {
			sumMAE += Math.abs( this.successTestist[i].prediction_error );
			sumRMSE += Math.pow(this.successTestist[i].prediction_error, 2);
		}
		
		this.MAE = sumMAE / (double)this.numOfSuccessTest;
		this.RMSE = Math.sqrt( sumRMSE / (double)this.numOfSuccessTest );
		this.coverage = ((double)this.numOfSuccessTest / (double)this.numOfTestSet);
		this.precision_MAE = 1.0-(this.MAE/(double)4);
		this.precision_RMSE = 1.0-(this.RMSE/(double)4);
		this.f1_MAE = (2.0*this.precision_MAE*this.coverage)/(this.precision_MAE+this.coverage);
		this.f1_RMSE = (2.0*this.precision_RMSE*this.coverage)/(this.precision_RMSE+this.coverage);
		
		System.out.println("\t MAE: "+this.MAE);
		System.out.println("\t RMSE: "+this.RMSE);
		
		System.out.println("\t Precision using MAE: "+this.precision_MAE);
		System.out.println("\t Precision using RMSE: "+this.precision_RMSE);
		System.out.println("\t Coverage: "+this.coverage);
		
		System.out.println("\t F1 score using MAE: "+this.f1_MAE);
		System.out.println("\t F1 score using RMSE: "+this.f1_RMSE);
		
		return true;
	}
	

	
	
	public void exportAnalysisResults() {
		try {
			Date dt = new Date();
			
			StringBuffer sb = new StringBuffer();
			String fileName = title+"_"+(this.ratioOfTestSet*100)+"_"+this.experimentID;
			
			String stringTitle = "Title\t"+title+"\t \n";
			String ratioOfTestSet = "Ratio of TestSet\t"+this.ratioOfTestSet*100+"%\t \n";
			String experimentID = "Experiment ID\t"+this.experimentID+"\t \n";
			String date = "Date\t"+dt.toString()+"\t \n";
			
			String stringMAE = "MAE\t"+this.MAE+"\t \n";
			String stringRMSE = "RMSE\t"+this.RMSE+"\t \n";
			String stringCoverage = "Coverage\t"+this.coverage+"\t \n";
			String stringPrecision_MAE = "Precision_MAE\t"+this.precision_MAE+"\t \n";
			String stringPrecision_RMSE = "Precision_RMSE\t"+this.precision_RMSE+"\t \n";
			String stringF1_MAE = "F1_MAE\t"+this.f1_MAE+"\t \n";
			String stringF1_RMSE = "F1_RMSE\t"+this.f1_RMSE+"\t \n";
			
			sb.append(stringTitle).append(ratioOfTestSet).append(experimentID).append(date).
				append(stringMAE).append(stringRMSE).append(stringCoverage).
				append(stringPrecision_MAE).append(stringPrecision_RMSE).append(stringF1_MAE).
				append(stringF1_RMSE);
			
			this.file.writer(this.dir+"\\"+fileName+".analysis", sb.toString());
		}catch(Exception e) {
			
		}
	}
}

