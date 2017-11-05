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
	HashSet<Integer> testItemList = null;
	
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
			this.testItemList = rn.getNRandomNumbers_Set(1, this.numOfRatings, numOfTestRatings);
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
					/*
					this.db.getStmt().executeUpdate("INSERT INTO `movielens`.`ratings_testset_"+percent+"` (`userID`, `itemID`, `rating`, `timestamp`, `No`) "
							+ "VALUES ('"+userNo+"', '"+itemNo+"', '"+rating+"', '"+timeStamp+"', '"+no+"');");
					*/
				}
			}
			
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
		}catch(Exception e){
			System.err.println("makeTestDataSet error: "+e.getMessage());
		}
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