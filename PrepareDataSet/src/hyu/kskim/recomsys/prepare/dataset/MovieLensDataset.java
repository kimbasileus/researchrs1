package hyu.kskim.recomsys.prepare.dataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import hyu.kskim.recomsys.prepare.utils.DBManager;
import hyu.kskim.recomsys.prepare.utils.FileIO;
import hyu.kskim.recomsys.prepare.utils.RandomNumbers;

public class MovieLensDataset {
	String schema = "movielens";
	String path = "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small"; // Dataset directory path
	String dbId = "root";
	String dbPw = "kyungsookim";
	DBManager db = new DBManager(null, null, null);
	FileIO file = new FileIO();
	
	ArrayList<RatingInfo> itemList = new ArrayList<RatingInfo>();
	
	int numOfUsers;
	int numOfItems;
	int numOfRatings;
	
	int numOfTestRatings;
	int numOfTrainingRatings;
	
	public MovieLensDataset(String schema, String dataset_Path, int numOfUsers, int numOfItems, int numOfRatings) {
		this.db.connectDB("root", "kyungsookim");
		
		if(schema!=null) this.schema = schema;
		if(dataset_Path!=null) this.path = dataset_Path;
		this.numOfUsers = numOfUsers; // 671
		this.numOfItems = numOfItems; // 9125
		this.numOfRatings = numOfRatings; // 100004
	}
	
	public void endMovieLensDataset() { // 소멸자
		this.db.closeDB();
	}
	
	public void loadInitialDataset_into_DB() {		
		this.loadRatings_into_DB(this.schema);
		this.loadUsers_into_DB(this.schema);
		this.loadItems_into_DB(this.schema);
		this.loadMovieURLData_into_ItemDB(this.schema);
		this.changeItemID(this.schema);
	}
	
	public void loadRatings_into_DB(String schema) {  // If the table is trainset, tableName=ratings_trainset
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
				 
				 String sql = null;
			     sql = "INSERT INTO `"+schema+"`.`ratings` (`ID`, `userID`, `movielensID`, `rating`, `timestamp`) "
							+ "VALUES ('"+count+"', '"+userID+"', '"+itemID+"', '"+rating+"', '"+timestamp+"');";
			   
				 db.getStmt().executeUpdate(sql);
				 
			 }
			 
			 reader.close();
		}catch(Exception e){
			System.err.println("loadRatings_into_DB error: "+e.getMessage());
		}
	}
	
	
	
	public void loadUsers_into_DB(String schema) {
		try {
			ArrayList<Integer> userList = new ArrayList<Integer>();
			String sql = "SELECT distinct userID FROM "+schema+".ratings;";
			
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int userID = 0;
			while(rs0.next()) {
				userID = rs0.getInt(1);
				userList.add(userID);
				System.out.println(userID);
			}
			
			rs0.close();
			
			int len = userList.size();
			
			for(int i=0; i < len; i++)
			{
				userID = userList.get(i);
				this.db.getStmt().execute("INSERT INTO `"+schema+"`.`users` (`ID`) VALUES ('"+userID+"');");
			}
			
			
		}catch(Exception e){
			System.err.println("loadUsers_into_DB error: "+e.getMessage());
		}
	}
	
	
	
	
	public void loadItems_into_DB(String schema) {
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
				 
				 String sql = "INSERT INTO `"+schema+"`.`items` (`ID`, `movielensID`, `name`, `genres`) "
				 				+ "VALUES ('"+count+"', '"+movieID+"', '"+title+"', '"+genres+"');";
				 
				 db.getStmt().executeUpdate(sql);
				
			 }
			 reader.close();
			 
		}catch(Exception e){
			System.err.println("loadItems_into_DB error: "+e.getMessage());
		}
	}
	
	
	
	public void loadMovieURLData_into_ItemDB(String schema) {
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
				 
				 String sql = "UPDATE `"+schema+"`.`items` "
							+ "SET `imdbID`='"+imdbID+"', `tmdbID`='"+tmdbID+"' WHERE `movielensID`='"+movieID+"';";
				 db.getStmt().executeUpdate(sql);
				
			 }
			 reader.close();
			 
		}catch(Exception e){
			System.err.println("loadMovieURLData_into_ItemDB error: "+e.getMessage());
		}
	}
	
	
	public void changeItemID(String schema) {
		try {
			          //MovieLensID, New ID
			Hashtable<Integer, Integer> map = new Hashtable<Integer, Integer>();
			ResultSet rs0 = this.db.getStmt().executeQuery("SELECT ID, movielensID  FROM "+schema+".items;");
			while(rs0.next()) {
				map.put(rs0.getInt(2), rs0.getInt(1));
			}
			rs0.close();
			
			Enumeration<Integer> mId = map.keys();
			
			int count = 0;
			while(mId.hasMoreElements()) {
				int movielensID = mId.nextElement();
				int newID = map.get(movielensID);
				
				String sql = "UPDATE `"+schema+"`.`ratings` SET `itemID`='"+newID+"' WHERE `movielensID`='"+movielensID+"';";
				this.db.getStmt().executeUpdate(sql);
				
				count++;
				System.out.println("["+count+"] "+movielensID+" --> "+newID);
			}
		}catch(Exception e){
			System.err.println("changeItemID error: "+e.getMessage());
		}
	}
	
	
	public void deleteAll(String schema) {
		try {
			
		}catch(Exception e){
			System.err.println("deleteAll error: "+e.getMessage());
		}
	}
	
	
	public void makeTestDataSet(String fileDir, String schema, double ratioOfTestRatings) {
		try {
			FileIO file = new FileIO();
			int percent = (int) (ratioOfTestRatings*100.0);
			
			int numOfTestRatings = (int) (this.numOfRatings*ratioOfTestRatings);
			this.numOfTestRatings = numOfTestRatings;
			this.numOfTrainingRatings = this.numOfRatings - this.numOfTestRatings;
			
			RandomNumbers rn = new RandomNumbers();
			//int testItemList[] = rn.getNRandomNumbers(1, this.numOfRatings, numOfTestRatings);
			HashSet<Integer> testItemList = rn.getNRandomNumbers_Set(1, this.numOfRatings, numOfTestRatings);
			Iterator<Integer> itr = testItemList.iterator();
			
			int n = numOfTestRatings;
			int id = 0; int userNo; int itemNo; int mNo; double rating; int timeStamp;
			
			StringBuffer sb = new StringBuffer();
			ResultSet rs = null;
			while(itr.hasNext()) {
				id = itr.next();
				
				rs = this.db.getStmt().executeQuery("SELECT userID, itemID, movielensID, rating, timestamp "
						+ "FROM `"+schema+"`.`ratings` where ID = "+id+";");
				
				while(rs.next()) {
					userNo = rs.getInt(1);
					itemNo = rs.getInt(2);
					mNo = rs.getInt(3);
					rating = rs.getDouble(4);
					timeStamp = rs.getInt(5);
					
					System.out.println(id+","+userNo+","+itemNo+","+mNo+","+rating+","+timeStamp);
					
					sb.append(id+","+userNo+","+itemNo+","+mNo+","+rating+","+timeStamp).append("\n");
				}
			}
			
			testItemList.clear();
			rs.close();
			
			String dir = fileDir + "ratings_testset_"+percent+".testset";
			file.writer(dir, sb.toString());
			
		}catch(Exception e){
			System.err.println("makeTestDataSet error: "+e.getMessage());
		}
	}

	

	public void makeCumulatedDataSet(String fileDir, double previous_ratio, double now_ratio) {
		try {
			double ratioOfTestRatings = now_ratio - previous_ratio;
			int percent = (int) (now_ratio*100.0);
			
			int numOfTestRatings = (int) (this.numOfRatings*ratioOfTestRatings);
			this.numOfTestRatings = numOfTestRatings;
			this.numOfTrainingRatings = this.numOfRatings - this.numOfTestRatings;
			
			
			
			
			// 이전의 테스트 데이터들의 ID를 DB로부터 읽어들인다.
			String dir = fileDir + "ratings_testset_"+((int)(previous_ratio*100))+".testset";
			HashSet<Integer> testItemList = new HashSet<Integer>();
			
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(dir));
			String inputLine = null; String pair[] = null;
			while ((inputLine = reader.readLine()) != null){
				pair = inputLine.split(",");
				if(pair==null) continue;
				
				testItemList.add(Integer.parseInt(pair[0]));
			}
			reader.close();
			System.out.println(buffer.toString());
			
			
			
			
			// 랜덤 추출
			RandomNumbers rn = new RandomNumbers();
			
			testItemList = rn.getNCummulatedRandomNumbers_Set(1, this.numOfRatings, numOfTestRatings, testItemList);
			Iterator<Integer> itr = testItemList.iterator();
			
			int id = 0; int userNo; int itemNo; int mNo; double rating; int timeStamp;
			ArrayList<testSetPair> extractedList = new ArrayList<testSetPair>();
			
			StringBuffer sb = new StringBuffer();
			ResultSet rs = null;
			while(itr.hasNext()) {
				id = itr.next();
				
				rs = this.db.getStmt().executeQuery("SELECT userID, itemID, movielensID, rating, timestamp "
						+ "FROM `"+schema+"`.`ratings` where ID = "+id+";");
				
				while(rs.next()) {
					userNo = rs.getInt(1);
					itemNo = rs.getInt(2);
					mNo = rs.getInt(3);
					rating = rs.getDouble(4);
					timeStamp = rs.getInt(5);
					
					System.out.println(id+","+userNo+","+itemNo+","+mNo+","+rating+","+timeStamp);
					
					sb.append(id+","+userNo+","+itemNo+","+mNo+","+rating+","+timeStamp).append("\n");
				}
			}
			
			testItemList.clear();
			rs.close();
			
			dir = fileDir + "ratings_testset_"+percent+".testset";
			file.writer(dir, sb.toString());
			
		}catch(Exception e){
			System.err.println("makeCumulatedDataSet error: "+e.getMessage());
		}
	}

	
	public void verify(String fileDir, int previous, int now) {
		try {
			HashSet<Integer> set0 = new HashSet<Integer>();
			String dir = fileDir + "ratings_testset_"+previous+".testset";
			
			BufferedReader reader = new BufferedReader(new FileReader(dir));
			String inputLine = null; String pair[] = null;
			while ((inputLine = reader.readLine()) != null){
				pair = inputLine.split(",");
				if(pair==null) continue;
				
				set0.add(Integer.parseInt(pair[0]));
			}
			reader.close();
			
			
					
			HashSet<Integer> set1 = new HashSet<Integer>();
			String dir1 = fileDir + "ratings_testset_"+now+".testset";
			
			BufferedReader reader1 = new BufferedReader(new FileReader(dir1));
			String inputLine1 = null; String pair1[] = null;
			while ((inputLine1 = reader1.readLine()) != null){
				pair1 = inputLine1.split(",");
				if(pair1==null) continue;
				
				set1.add(Integer.parseInt(pair1[0]));
			}
			reader1.close();
					
			
			boolean isCorrect = true;
			Iterator<Integer> itr = set0.iterator();
			while(itr.hasNext()) {
				if(!set1.contains(itr.next())) {
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
		loadInitialDataset_into_DB();
		
		//2. make test data set (0.2)
		//makeTestDataSet("movielens", 0.2);
		
		//3. make cummulated dataSet (0.4, 0.6, 0.8)
		
		//4. Verify
	//	verify(20, 40);
		//verify(40, 60);
	//	verify(60, 80);
		
	}
	
	
		
	public void loadTrain_Test_Dataset_Into_DB(String fileDir, String schema, double testsetRatio) {
		try {
			
			int ratio = (int)(testsetRatio * 100);
			String dir = fileDir + "ratings_testset_"+ratio+".testset";
			
			// 0. Delete existing data from DB
			this.db.getStmt().executeUpdate("DELETE FROM `"+schema+"`.`ratings_trainset`;");
			this.db.getStmt().executeUpdate("DELETE FROM `"+schema+"`.`ratings_testset`;");
			
			
			// 1. Read all ratings into training datasetDB (ArrayList<RatingInfo> itemList>
			loadAllRating_For_makeTratingSet(schema);
			
			//System.exit(0);
			
			// 2. Read test set
			BufferedReader reader = new BufferedReader(new FileReader(dir));
			String inputLine = null; String pair[] = null;
			int id; int userID; int itemID; int mID; double rating; int timeStamp;
			while ((inputLine = reader.readLine()) != null){
				pair = inputLine.split(",");
				if(pair==null) continue;
				
				id = Integer.parseInt(pair[0]);
				userID = Integer.parseInt(pair[1]);
				itemID = Integer.parseInt(pair[2]);
				mID = Integer.parseInt(pair[3]);
				rating = Double.parseDouble(pair[4]);
				timeStamp = Integer.parseInt(pair[5]);
				
			// 3. Delete testset from training set and Write testset onto the testset DB
				this.db.getStmt().executeUpdate("DELETE FROM `"+schema+"`.`ratings_trainset` "
						+ "WHERE `ID`='"+id+"';");
				
				this.db.getStmt().executeUpdate("INSERT INTO `movielens`.`ratings_testset` (`ID`, `userID`, `itemID`, `movielensID`, `actual_rating`, `timestamp`) "
						+ "VALUES ('"+id+"', '"+userID+"', '"+itemID+"', '"+mID+"', '"+rating+"', '"+timeStamp+"');");
			}
			reader.close();
			
			
		}catch(Exception e){
			System.err.println("loadTrain_Test_Dataset_Into_DB error: "+e.getMessage());
		}
	}
	
	
	public void loadAllRating_For_makeTratingSet(String schema) {
		try {
			for(int itemID=1; itemID<=this.numOfItems; itemID++) {
				this.loadAllRating_For_makeTratingSet_From_Item(schema, itemID, 0);
			}
			
			System.out.println("Original Rating Data is loaded: size is "+this.itemList.size());
			
			
			int n = this.itemList.size();
			RatingInfo node = null;
			for(int i=0; i < n ; i++) {
				node = this.itemList.get(i);
				String sql = "INSERT INTO `"+schema+"`.`ratings_trainset` (`ID`, `userID`, `itemID`, `movielensID`, `rating`, `timestamp`) "
						+ "VALUES ('"+node.id+"', '"+node.userID+"', '"+node.itemID+"', '"+node.movielensID+"', '"+node.rating+"', '"+node.timestamp+"');";
				this.db.getStmt().executeUpdate(sql);
				
				System.out.println((i+1)+"번째 rating을 trainset에 삽입 완료.");
			}
			
		}catch(Exception e){
			System.err.println("loadAllRating_For_makeTratingSet error: "+e.getMessage());
		}
	}
	
	private void loadAllRating_For_makeTratingSet_From_Item(String schema, int itemID, int method) {
		try {
			String sql = null;
			if(method==0)
				sql = "SELECT * FROM "+schema+".ratings where itemID = '"+itemID+"';";
			else if(method==1)
				sql = "SELECT * FROM "+schema+".ratings_trainset where itemID = '"+itemID+"';";
			else if(method==2)
				sql = "SELECT * FROM "+schema+".ratings_testset where itemID = '"+itemID+"';";
			else
				sql = "SELECT * FROM "+schema+".ratings where itemID = '"+itemID+"';";
			
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int id; int userID; int itemId; int movielensID; double rating; int timestamp;
			while(rs0.next()) {
				id = rs0.getInt(1);
				userID = rs0.getInt(2);
				itemId = rs0.getInt(3);
				movielensID = rs0.getInt(4);
				rating = rs0.getDouble(5);
				timestamp = rs0.getInt(6);
				
				this.itemList.add(new RatingInfo(id, userID, itemId, movielensID, rating, timestamp));
 			}
			System.out.println("  아이템 "+itemID+" 에 대한 rating 정보 메모리 로딩 완료.");
			rs0.close();
		}catch(Exception e){
			System.err.println("loadAllRating_For_makeTratingSet_From_Item error: "+e.getMessage());
		}
	}
	
	
	

	public void load_userAverage_Into_DB(String schema) {
		try {
			ArrayList<Integer> users = new ArrayList<Integer>();
			ArrayList<UserAvg> avgList = new ArrayList<UserAvg>();
			
			// 0. DB로부터 모든 사용자들의 ID를 불러온다.
			ResultSet rs0 = this.db.getStmt().executeQuery("SELECT ID FROM "+schema+".users;");
			while(rs0.next()) {
				users.add(rs0.getInt(1));
			}
			rs0.close();
			
			
			// 1. training set DB로부터 각 사용자별로 Rating 값들을 구한후 이들의 평균을 계산하여 메모리에 저장한다.
			int n = users.size();
			int userID;
			for(int i=0; i < n; i++) { // User ID
				userID = users.get(i);
				
				ResultSet rs1 = this.db.getStmt().executeQuery("SELECT rating FROM "+schema+".ratings_trainset where userID = "+userID+";");
				double sum = 0; int num = 0; double avg=0;
				while(rs1.next()) {
					sum += rs1.getDouble(1);
					num++;
				}
				if(num==0) avg = -1;
				else avg = sum/(double)num;
						
				avgList.add(new UserAvg(userID, avg, num, sum));
				rs1.close();
				System.out.println("\t-- User "+userID+"'s average: "+avg);
			}
			
			
			// 2. 각 사용자별로 구한 평균값들을 DB에 캐싱한다.
			n = avgList.size();
			UserAvg avgNode = null;
			
			this.db.getStmt().executeUpdate("delete FROM "+schema+".user_avg;"); // 기존에 캐싱되어 있는 값은 모두 지운다.
			
			for(int i=0; i < n; i++) {
				avgNode = avgList.get(i);
				
				this.db.getStmt().executeUpdate("INSERT INTO `"+schema+"`.`user_avg` (`userID`, `average`, `numOfRating`, `sumOfRating`) "
						+ "VALUES ('"+avgNode.userID+"', '"+avgNode.average+"', '"+avgNode.numOfRating+"', '"+avgNode.sumOfRating+"');");
			}
			System.out.println("\t-- User average caching is completed.");
			
		}catch(Exception e){
			System.err.println("load_userAverage_Into_DB error: "+e.getMessage());
		}
	}
	
	
	
	
	
	
	public void verify_itemIDs(String schema, int method, boolean isBasedItem) {
		          // movielensID, newID
		Hashtable<Integer, Integer> itemIDs = new Hashtable<Integer, Integer>();
		try {
			this.itemList.clear();
			
			String sql = "SELECT ID, movielensID FROM "+schema+".items;";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int newID; int movielensID;
			while(rs0.next()) {
				movielensID = rs0.getInt(2);
				newID = rs0.getInt(1);
				
				itemIDs.put(movielensID, newID);
			}
			rs0.close();
			
			System.out.println("1. Movielens ID and new ID 로딩 완료: the number of keys is "+itemIDs.size());
			System.out.println("------------");
			
			if(!isBasedItem) { // User-based
				for(int uID=1; uID<=this.numOfUsers; uID++) {
					this.loadAllRating_For_makeTratingSet_From_User(schema, uID, method);
				}
			}else { // Item-based
				for(int iID=1; iID<=this.numOfItems; iID++) {
					this.loadAllRating_For_makeTratingSet_From_Item(schema, iID, method);
				}
			}
			
			System.out.println("2. Complete loading ratings from method "+method+": size is "+this.itemList.size());
			System.out.println("------------");
			
			int n = this.itemList.size();
			RatingInfo node = null;
			int numOfCorrect=0; int numOfIncorrect=0;
			
			for(int i=0; i < n ; i++) {
				node = this.itemList.get(i);
				
				//int ID = node.id;
				movielensID = node.movielensID;
				newID = node.itemID;
				
				if(itemIDs.containsKey(movielensID) && itemIDs.get(movielensID) == newID) {
					numOfCorrect++;
					System.out.println("["+(i+1)+"] MovielensID "+movielensID+" --> newID "+newID+" : Correct.");
				}else {
					numOfIncorrect++;
					System.out.println("["+(i+1)+"] MovielensID "+movielensID+" --> newID "+newID+" : InCorrect.------- InCorrect.");
				}
			}
			
			System.out.println("3. Complete loading ratings from method "+method+": size is "+this.itemList.size());
			System.out.println("\t - The number of correct ratings is "+numOfCorrect);
			System.out.println("\t - The number of incorrect ratings is "+numOfIncorrect);
			System.out.println("\t - The total number of ratings is "+(numOfCorrect+numOfIncorrect) );
			System.out.println("------------");
		}catch(Exception e){
			System.err.println("verity_itemIDs error: "+e.getMessage());
		}
	}
	
	
	private void loadAllRating_For_makeTratingSet_From_User(String schema, int userID, int method) {
		try {
			String sql = null;
			if(method==0)
				sql = "SELECT * FROM "+schema+".ratings where userID = '"+userID+"';";
			else if(method==1)
				sql = "SELECT * FROM "+schema+".ratings_trainset where userID = '"+userID+"';";
			else if(method==2)
				sql = "SELECT * FROM "+schema+".ratings_testset where userID = '"+userID+"';";
			else
				sql = "SELECT * FROM "+schema+".ratings where userID = '"+userID+"';";
			
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int id; int uID=0; int itemId; int movielensID; double rating; int timestamp;
			while(rs0.next()) {
				id = rs0.getInt(1);
				uID = rs0.getInt(2);
				itemId = rs0.getInt(3);
				movielensID = rs0.getInt(4);
				rating = rs0.getDouble(5);
				timestamp = rs0.getInt(6);
				
				this.itemList.add(new RatingInfo(id, uID, itemId, movielensID, rating, timestamp));
 			}
			System.out.println("  사용자 "+userID+" 에 대한 rating 정보 메모리 로딩 완료.");
			rs0.close();
		}catch(Exception e){
			System.err.println("loadAllRating_For_makeTratingSet_From_User error: "+e.getMessage());
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


class UserAvg{
	public int userID;
	public double average;
	public int numOfRating;
	public double sumOfRating;
	
	public UserAvg(int userID, double average, int numOfRating, double sumOfRating) {
		super();
		this.userID = userID;
		this.average = average;
		this.numOfRating = numOfRating;
		this.sumOfRating = sumOfRating;
	}	
}

class RatingInfo{
	public int id;
	public int userID;
	public int itemID;
	public int movielensID;
	public double rating;
	public int timestamp;
	
	public RatingInfo(int no, int userID, int itemID, int movielensID, double rating, int timestamp) {
		super();
		id = no;
		this.userID = userID;
		this.itemID = itemID;
		this.movielensID = movielensID;
		this.rating = rating;
		this.timestamp = timestamp;
	}
}