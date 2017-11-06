package hyu.kskim.recomsys.prepare.dataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import hyu.kskim.recomsys.prepare.utils.DBManager;
import hyu.kskim.recomsys.prepare.utils.FileIO;
import hyu.kskim.recomsys.prepare.utils.RandomNumbers;

public class MovieLensDataset {
	String path = "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small"; // Dataset directory path
	String dbId = "root";
	String dbPw = "kyungsookim";
	DBManager db = new DBManager(null, null, null);
	FileIO file = new FileIO();
//	HashSet<Integer> testItemList = null;
	
	int numOfUsers;
	int numOfItems;
	int numOfRatings;
	
	int numOfTestRatings;
	int numOfTrainingRatings;
	
	public MovieLensDataset(int numOfUsers, int numOfItems, int numOfRatings) {
		db.connectDB("root", "kyungsookim");
		this.numOfUsers = numOfUsers; // 671
		this.numOfItems = numOfItems; // 9125
		this.numOfRatings = numOfRatings; // 100004
	}
	
	public void run() {
		this.loadRatingDataSetIntoDB();
		this.loadMovieContentDataSetIntoDB();
		this.loadMovieLinkDataSetIntoDB();
		
		db.closeDB();
	}
	
	public void loadRatingDataSetIntoDB() {
		String dir = path+"\\ratings.csv";
		try{
			
			 // Update Information Table
			 db.getStmt().executeUpdate("INSERT INTO `movielens`.`information` (`ID`, `dbName`, `numOfUsers`, `numOfItems`, `numOfRatings`) "
					+ "VALUES ('1', 'movielens_small', '"+this.numOfUsers+"', '"+this.numOfItems+"', '"+this.numOfRatings+"');");
			
			 BufferedReader reader = new BufferedReader(new FileReader(dir));
			 String inputLine = null;
			 
			 String temp[] = null; int userID; int itemID; double rating; int timestamp;
			 int count = -1;
			 while ((inputLine = reader.readLine()) != null){
				 if(count==-1) {
					 count++;
					 continue;
				 }
				
				 temp = inputLine.split(",");
				 if(temp.length != 4) continue;
				 
				 userID = Integer.parseInt(temp[0]);
				 itemID = Integer.parseInt(temp[1]);
				 rating = Double.parseDouble(temp[2]);
				 timestamp = Integer.parseInt(temp[3]);
				 
				 count++;
				 System.out.println("["+count+"] \t"+userID+"\t"+itemID+"\t"+rating+"\t"+timestamp);
				 
				 db.getStmt().executeUpdate("INSERT INTO `movielens`.`ratings` (`ID`, `userID`, `itemID`, `rating`, `timestamp`) "
				 		+ "VALUES ('"+count+"', '"+userID+"', '"+itemID+"', '"+rating+"', '"+timestamp+"');");
				 
			 }
			 
			 reader.close();
		}catch(Exception e){
			System.err.println("loadRatingDataSetIntoDB error: "+e.getMessage());
		}
	}
	
	// Current working code..........
	/*
	public void loadUserInformationIntoDB() {
		try {
			HashSet<Integer> userList = new HashSet<Integer>();
			ResultSet rs0 = this.db.getStmt().executeQuery("SELECT distinct userID FROM movielens.ratings;");
			while(rs0.next()) {
				userList.add(rs0.getInt(1));
			}
			rs0.close();
			
			// 
			
			ResultSet rs1 = null;
			Iterator<Integer> itr = userList.iterator();
			
			ArrayList<UserInfo> users = new ArrayList<UserInfo>();
			
			while(itr.hasNext()) { // For each user
				rs1 = this.db.getStmt().executeQuery("SELECT rating FROM movielens.ratings "
						+ "where userID = "+itr.next()+";");
				
				double sumOfRating = 0;	int numOfRatings = 0; double avg = 0;
				while(rs1.next()) { // For each rating
					numOfRatings++;
					sumOfRating += rs1.getDouble(1);
				}
				
				if(numOfRatings==0) avg = -1;
				else avg = sumOfRating/(double) numOfRatings;
				
				
			}
			
		}catch(Exception e){
			System.err.println("loadUserInformationIntoDB error: "+e.getMessage());
		}
	}
	*/
	
	
	
	public void loadMovieContentDataSetIntoDB() {
		String dir = path+"\\movies.csv";
		try{
			 BufferedReader reader = new BufferedReader(new FileReader(dir));
			 String inputLine = null;
			 
			 String temp[] = null; int movieID; String title; String genres;
			 int count = -1;
			 while ((inputLine = reader.readLine()) != null){
				 if(count==-1) {
					 count++;
					 continue;
				 }
				 
				 temp = inputLine.split(",");
				 if(temp.length < 3) {
					 continue;
				 }else if(temp.length >3) {
					 int length = temp.length;
   					 movieID = Integer.parseInt(temp[0]);
   					 genres = temp[length-1].replace("'", "\\'").replace("|", ",");
   					 
   					 title = "";
   					 for(int i=1; i <= length-2; i++)
   						 if(i != length-2) title = title + temp[i]+", ";
   						 else title = title + temp[i];
   					 
   					 title = title.replace("'", "\\'").replace("|", ",");
				 }else {
				 
					 movieID = Integer.parseInt(temp[0]);
					 title = temp[1].replace("'", "\\'");
					 genres = temp[2].replace("'", "\\'").replace("|", ",");
				 
				 }
				 count++;
				 System.out.println("["+count+"] \t"+movieID+"\t"+title+"\t"+genres);
				 
				 db.getStmt().executeUpdate("INSERT INTO `movielens`.`movies` (`itemID`, `title`, `genres`) "
				 		+ "VALUES ('"+movieID+"', '"+title+"', '"+genres+"');");
				
			 }
			 reader.close();
			 
		}catch(Exception e){
			System.err.println("loadMovieContentDataSetIntoDB error: "+e.getMessage());
		}
	}
	
	
	
	public void loadMovieLinkDataSetIntoDB() {
		String dir = path+"\\links.csv";
		try{
			 BufferedReader reader = new BufferedReader(new FileReader(dir));
			 String inputLine = null;
			 
			 String temp[] = null; int movieID; int imdbID; int tmdbID;
			 int count = -1;
			 while ((inputLine = reader.readLine()) != null){
				 if(count==-1) {
					 count++;
					 continue;
				 }
				
				 temp = inputLine.split(",");
				 if(temp.length ==1 ) {
					 movieID = Integer.parseInt(temp[0]);
					 imdbID = -1;
					 tmdbID = -1;
				 }else if (temp.length == 2) {
					 movieID = Integer.parseInt(temp[0]);
					 imdbID = Integer.parseInt(temp[1]);
					 tmdbID = -1;
				 }else {
					 movieID = Integer.parseInt(temp[0]);
					 imdbID = Integer.parseInt(temp[1]);
					 tmdbID = Integer.parseInt(temp[2]);
				 }
				 count++;
				 System.out.println("["+count+"] \t"+movieID+"\t"+imdbID+"\t"+tmdbID);
				 
				 db.getStmt().executeUpdate("UPDATE `movielens`.`movies` "
						+ "SET `imdbID`='"+imdbID+"', `tmdbID`='"+tmdbID+"' WHERE `itemID`='"+movieID+"';");
				
			 }
			 reader.close();
			 
		}catch(Exception e){
			System.err.println("loadMovieLinkDataSetIntoDB error: "+e.getMessage());
		}
	}
	
	
	
	public void makeTestDataSet(double ratioOfTestRatings) {
		try {
			int percent = (int) (ratioOfTestRatings*100.0);
			
			int numOfTestRatings = (int) (this.numOfRatings*ratioOfTestRatings);
			this.numOfTestRatings = numOfTestRatings;
			this.numOfTrainingRatings = this.numOfRatings - this.numOfTestRatings;
			
			RandomNumbers rn = new RandomNumbers();
			//int testItemList[] = rn.getNRandomNumbers(1, this.numOfRatings, numOfTestRatings);
			HashSet<Integer> testItemList = rn.getNRandomNumbers_Set(1, this.numOfRatings, numOfTestRatings);
			Iterator<Integer> itr = testItemList.iterator();
			
			int n = numOfTestRatings;
			int id = 0; int userNo; int itemNo; double rating; int timeStamp;
			
			ArrayList<testSetPair> extractedList = new ArrayList<testSetPair>();
			
			ResultSet rs = null;
			while(itr.hasNext()) {
				id = itr.next();
				
				rs = this.db.getStmt().executeQuery("SELECT userID, itemID, rating, timestamp, ID "
						+ "FROM movielens.ratings where ID = "+id+";");
				
				while(rs.next()) {
					userNo = rs.getInt(1);
					itemNo = rs.getInt(2);
					rating = rs.getDouble(3);
					timeStamp = rs.getInt(4);
					
					System.out.println(userNo+"\t"+itemNo+"\t"+rating+"\t"+timeStamp+"\t"+id);
					
					extractedList.add(new testSetPair(userNo, itemNo, rating, timeStamp, id));
				}
			}
			
			testItemList.clear();
			rs.close();
			
		//	testItemList = null;
		//	System.gc();
			System.out.println("----------------");
			
			testSetPair node = null;
			for(int i=0; i < n; i++) {
				node = extractedList.get(i);
				
				System.out.println(node.userNo+"\t"+node.itemNo+"\t"+node.rating+"\t"+node.timeStamp+"\t"+(i+1));
				
				this.db.getStmt().executeUpdate("INSERT INTO `movielens`.`ratings_testset_"+percent+"` (`ID`, `userID`, `itemID`, `rating`, `timestamp`) "
						+ "VALUES ('"+node.id+"', '"+node.userNo+"', '"+node.itemNo+"', '"+node.rating+"', '"+node.timeStamp+"');");
				
			}
			
			extractedList.clear();
		}catch(Exception e){
			System.err.println("makeTestDataSet error: "+e.getMessage());
		}
	}

	

	public void makeCumulatedDataSet(double previous_ratio, double now_ratio) {
		try {
			double ratioOfTestRatings = now_ratio - previous_ratio;
			int percent = (int) (now_ratio*100.0);
			
			int numOfTestRatings = (int) (this.numOfRatings*ratioOfTestRatings);
			this.numOfTestRatings = numOfTestRatings;
			this.numOfTrainingRatings = this.numOfRatings - this.numOfTestRatings;
			
			
			// 이전의 테스트 데이터들의 ID를 DB로부터 읽어들인다.
			HashSet<Integer> testItemList = new HashSet<Integer>();
			//this.testItemList.clear();
			ResultSet rs0 = null;
			rs0 = this.db.getStmt().executeQuery("SELECT ID "
					+ "FROM movielens.ratings_testset_"+(int) (previous_ratio*100.0)+";");
			int previousID = 0;
			while(rs0.next()) {
				previousID = rs0.getInt(1);
				testItemList.add(previousID);
			}
			rs0.close();
			
			RandomNumbers rn = new RandomNumbers();
			
			testItemList = rn.getNCummulatedRandomNumbers_Set(1, this.numOfRatings, numOfTestRatings, testItemList);
			Iterator<Integer> itr = testItemList.iterator();
			
			int id = 0; int userNo; int itemNo; double rating; int timeStamp;
			ArrayList<testSetPair> extractedList = new ArrayList<testSetPair>();
			
			ResultSet rs = null;
			while(itr.hasNext()) {
				id = itr.next();
				
				rs = this.db.getStmt().executeQuery("SELECT userID, itemID, rating, timestamp, ID "
						+ "FROM movielens.ratings where ID = "+id+";");
				
				while(rs.next()) {
					userNo = rs.getInt(1);
					itemNo = rs.getInt(2);
					rating = rs.getDouble(3);
					timeStamp = rs.getInt(4);
					
					System.out.println(userNo+"\t"+itemNo+"\t"+rating+"\t"+timeStamp+"\t"+id);
					
					extractedList.add(new testSetPair(userNo, itemNo, rating, timeStamp, id));
				}
			}
			
			testItemList.clear();
			rs.close();
			
		//	testItemList = null;
		//	System.gc();
			System.out.println("----------------");
			
			int len = extractedList.size();
			testSetPair node = null;
			for(int i=0; i < len; i++) {
				node = extractedList.get(i);
				
				System.out.println(node.userNo+"\t"+node.itemNo+"\t"+node.rating+"\t"+node.timeStamp+"\t"+(i+1));
				
				this.db.getStmt().executeUpdate("INSERT INTO `movielens`.`ratings_testset_"+percent+"` (`ID`, `userID`, `itemID`, `rating`, `timestamp`) "
						+ "VALUES ('"+node.id+"', '"+node.userNo+"', '"+node.itemNo+"', '"+node.rating+"', '"+node.timeStamp+"');");
				
			}
			
			extractedList.clear();
		}catch(Exception e){
			System.err.println("makeCumulatedDataSet error: "+e.getMessage());
		}
	}

	
	public void verify(int previous, int now) {
		try {
			ResultSet rs0 = this.db.getStmt().executeQuery("SELECT ID "
					+ "FROM movielens.ratings_testset_"+previous+";");
			
			HashSet<Integer> set0 = new HashSet<Integer>();
			int id0 = 0;
			while(rs0.next()) {
				id0 = rs0.getInt(1);
				set0.add(id0);
				System.out.println("read previous set: "+id0);
			}
			rs0.close();
			
			ResultSet rs1 = this.db.getStmt().executeQuery("SELECT ID "
					+ "FROM movielens.ratings_testset_"+now+";");
			
			HashSet<Integer> set1 = new HashSet<Integer>();
			int id1 = 0;
			while(rs1.next()) {
				id1 = rs1.getInt(1);
				set1.add(id1);
				System.out.println("read now set: "+id1);
			}
			rs1.close();
			
			boolean isCorrect = true;
			Iterator<Integer> itr = set0.iterator();
			while(itr.hasNext()) {
				if(!set1.contains(itr.next())) {
					System.out.println("!!!!!");
					isCorrect = false;
				}
			}
			
			if(isCorrect) System.out.println("Correct set.");
			else System.out.println("Incorrect set.");
			
		}catch(Exception e){
			System.err.println("verify error: "+e.getMessage());
		}
	}
	
	
	private void startMovieLensDataset() {
		//1. Loading movie lens dataset
		run();
		
		//2. make test data set (0.2)
		makeTestDataSet(0.2);
		
		//3. make cummulated dataSet (0.4, 0.6, 0.8)
		makeCumulatedDataSet(0.2, 0.4);
		makeCumulatedDataSet(0.4, 0.6);
		makeCumulatedDataSet(0.6, 0.8);
		
		//4. Verify
		verify(20, 40);
		verify(40, 60);
		verify(60, 80);
		
	}
}



class testSetPair{
	public int userNo;
	public int itemNo;
	public double rating;
	public int timeStamp;
	public int id;
	
	public testSetPair(int userNo, int itemNo, double rating, int timeStamp, int no) {
		super();
		this.userNo = userNo;
		this.itemNo = itemNo;
		this.rating = rating;
		this.timeStamp = timeStamp;
		this.id = no;
	}
}


class UserInfo{
	public int userID;
	public double sumOFRatings;
	public int numOfRatings;
	public double avg;
	
	public UserInfo(int userID, double sumOFRatings, int numOfRatings, double avg) {
		super();
		this.userID = userID;
		this.sumOFRatings = sumOFRatings;
		this.numOfRatings = numOfRatings;
		this.avg = avg;
	}	
}