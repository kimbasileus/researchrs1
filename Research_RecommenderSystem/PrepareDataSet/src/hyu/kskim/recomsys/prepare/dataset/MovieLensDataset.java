package hyu.kskim.recomsys.prepare.dataset;

import java.io.BufferedReader;
import java.io.FileReader;

import hyu.kskim.recomsys.prepare.utils.DBManager;
import hyu.kskim.recomsys.prepare.utils.FileIO;

public class MovieLensDataset {
	String path = "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small"; // Dataset directory path
	String dbId = "root";
	String dbPw = "kyungsookim";
	DBManager db = new DBManager(null, null, null);
	FileIO file = new FileIO();
	
	public MovieLensDataset() {
		db.connectDB("root", "kyungsookim");
		
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
				 
				 db.getStmt().executeUpdate("INSERT INTO `movielens`.`ratings` (`userID`, `itemID`, `rating`, `timestamp`) "
				 		+ "VALUES ('"+userID+"', '"+itemID+"', '"+rating+"', '"+timestamp+"');");
				 
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
}