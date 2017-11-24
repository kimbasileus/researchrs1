package hyu.kskim.recomsys.prepare.dataset;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import hyu.kskim.recomsys.prepare.utils.DBManager;
import hyu.kskim.recomsys.prepare.utils.FileIO;


public class EpinionsDataset {
	String schema = "movielens";
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
	
	public EpinionsDataset(String schema, int numOfUsers, int numOfItems, int numOfRatings) {
		this.db.connectDB("root", "kyungsookim");
		
		if(schema!=null) this.schema = schema;
		this.numOfUsers = numOfUsers; // 91735
		this.numOfItems = numOfItems; // 26527
		this.numOfRatings = numOfRatings; // 170797
	}
	
	
	
	public void endMovieLensDataset() { // 소멸자
		this.db.closeDB();
	}
		
	///////////////////////////// 테이블 생성 코드 /////////////////////////////////////////////////
	public void make_users_table() { // 완료
		try {
			// 1. Load existing data
			String sql = "SELECT userNumID, userID FROM epinionsserver.user_large order by userNumID asc;";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			Queue<UserInfo2> userList = new LinkedList<UserInfo2>();
			
			while(rs0.next()) {
				String userName = rs0.getString(2).replace("'", "\\'");
				userList.add(new UserInfo2(rs0.getInt(1), userName));
			}
			rs0.close();
			
			// 2. write them onto the new table
			DBManager db2 = new DBManager(null, null, null);
			db2.connectDB("root", "kyungsookim");
			
			UserInfo2 user = null;
			while(!userList.isEmpty()) {
				user = userList.remove();
				
				String sql2 = "INSERT INTO `epinionscom`.`users` (`ID`, `name`) VALUES ('"+user.userID+"', '"+user.userName+"');";
				db2.getStmt().addBatch(sql2);
				System.out.println("make_users_table: "+user.userID+"\t"+user.userName);
			}
			
			db2.getStmt().executeBatch();
			db2.getStmt().close();
			db2.closeDB();

		}catch(Exception e) {
			System.err.println("make_users_table error: "+e.getMessage());
		}
	}
	
	
	public void make_usertrust_table() {  // 완료
		try {
			// 1. Load existing data
			String sql = "SELECT fromNumID, toNumID FROM epinionsserver.usertrust_large;";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			Queue<UserInfo2> userList = new LinkedList<UserInfo2>();
			
			while(rs0.next()) {
				userList.add(new UserInfo2(rs0.getInt(1), rs0.getInt(2)));
			}
			rs0.close();
			
			// 2. write them onto the new table
			DBManager db2 = new DBManager(null, null, null);
			db2.connectDB("root", "kyungsookim");
			
			UserInfo2 user = null;
			int count = 0;
			while(!userList.isEmpty()) {
				user = userList.remove();
				
				String sql2 = "INSERT INTO `epinionscom`.`usertrust` (`fromUserID`, `toUserID`) "
						+ "VALUES ('"+user.userID+"', '"+user.toUserID+"');";
				db2.getStmt().addBatch(sql2);
				count++;
				System.out.println((count)+"] make_usertrust_table: "+user.userID+"\t --> "+user.toUserID);
			}
			
			db2.getStmt().executeBatch();
			db2.getStmt().close();
			db2.closeDB();

		}catch(Exception e) {
			System.err.println("make_users_table error: "+e.getMessage());
		}
	}
	
	
		
	
	public void make_items_table() {
		try {
			ItemInfo2[] itemList = new ItemInfo2[this.numOfItems+1];
			String sql = "SELECT newcontentNumID, contentName, contentURL FROM epinionsserver.content order by newcontentNumID asc;";
			//1. 기본 content 정보 읽기 (ID, Name, URL)
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			while(rs0.next()) {
				int itemID = rs0.getInt(1);
				String title = rs0.getString(2).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				String URL = rs0.getString(3).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				
				itemList[itemID] = new ItemInfo2(itemID, title, URL);
				System.out.println("make_items_table\t item: "+itemID+" 1차 로딩 완료. ");
			}
			rs0.close();
			
			
			//2. 메타데이터 정보 읽기                 1           2     3         4       5        6         7       8       9        10 
			String sql2 = "SELECT newcontentNumID, Genre, Subgenre, Date, Language, Director, Actors, Stars, Summary, keywords FROM epinionsserver.contentmeta;";
			ResultSet rs1 = this.db.getStmt().executeQuery(sql2);
			while(rs1.next()) {
				int itemID = rs1.getInt(1);
				itemList[itemID].genre = rs1.getString(2).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
				itemList[itemID].subgenre = rs1.getString(3).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
				itemList[itemID].date = rs1.getString(4).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
				itemList[itemID].language = rs1.getString(5).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
				itemList[itemID].director = rs1.getString(6).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
				itemList[itemID].actors = rs1.getString(7).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
				itemList[itemID].starts = rs1.getString(8).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
				itemList[itemID].summary = rs1.getString(9).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
				itemList[itemID].keywords = rs1.getString(10).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");;
			
				System.out.println("make_items_table\t item: "+itemID+" 2차 로딩 완료. ");
			}
			rs1.close();
			
			//System.exit(0);
			// 3. DB에 하나씩 기록하기.
			for(int i = 1; i < itemList.length; i++) {
				String sql3 = "INSERT INTO `epinionscom`.`items` (`ID`, `title`, `genres`, `subgenre`, `date`, `language`, `director`, `actors`, `stars`, `summary`, `URL`, `keywords`) "
						+ "VALUES ('"+itemList[i].itemID+"', '"+itemList[i].title+"', '"+itemList[i].genre+"', '"+itemList[i].subgenre+"', '"+itemList[i].date+"', '"+itemList[i].language+"', "
						+ "'"+itemList[i].director+"', '"+itemList[i].actors+"', '"+itemList[i].starts+"', '"+itemList[i].summary+"', '"+itemList[i].URL+"', '"+itemList[i].keywords+"');";
				//this.db.getStmt().executeUpdate(sql3);
				System.out.println(i+"] make_items_table\t item: "+itemList[i].itemID+" 메타데이터 기록 완료. ");
			}
		} catch (Exception e) {
			System.err.println("make_items_table Exception: "+e.getMessage());
		}
	}
	
	
	
	////////////////////////////// Item ID 검증 관련 코드 ///////////////////////////////////////////////
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
	
	public void verify_itemIDs(String schema, int method, boolean isBasedItem) {
        // movielensID, newID
		Hashtable<Integer, Integer> itemIDs = new Hashtable<Integer, Integer>();
		try {
			this.itemList.clear();
			
			String sql = "SELECT contentNumID, newcontentNumID FROM "+schema+".content;";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int newID; int oldID;
			while(rs0.next()) {
				oldID = rs0.getInt(1);
				newID = rs0.getInt(2);
				
				itemIDs.put(oldID, newID);
			}
			rs0.close();
			
			System.out.println("1.  ID and new ID 로딩 완료: the number of keys is "+itemIDs.size());
			System.out.println("------------");
			
			if(!isBasedItem) { // User-based
				for(int uID=1; uID<=this.numOfUsers; uID++) {
					//this.loadAllRating_For_makeTratingSet_From_User(schema, uID, method);
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
				oldID = node.oldItemID;
				newID = node.itemID;
				
				if(itemIDs.containsKey(oldID) && itemIDs.get(oldID) == newID) {
					numOfCorrect++;
					System.out.println("["+(i+1)+"] OldID "+oldID+" --> newID "+newID+" : Correct.");
				}else {
					numOfIncorrect++;
					System.out.println("["+(i+1)+"] OldID "+oldID+" --> newID "+newID+" : InCorrect.------- InCorrect.");
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
	
	private void loadAllRating_For_makeTratingSet_From_Item(String schema, int itemID, int method) {
		try {
			String sql = null;
			ResultSet rs0 = null;
			
			if(method==1) { // ratings
				sql = "SELECT ID , userID, newcontentNumID, contentID, rating  FROM "+schema+".ratings"
						+ " where newcontentNumID = "+itemID+";";
				rs0 = this.db.getStmt().executeQuery(sql);
				int id; int userID; int itemId; int movielensID; double rating; int timestamp;
				while(rs0.next()) {
					id = rs0.getInt(1);
					userID = rs0.getInt(2);
					itemId = rs0.getInt(3);
					movielensID = rs0.getInt(4);
					rating = rs0.getDouble(5);
					timestamp = 1;
					
					this.itemList.add(new RatingInfo(id, userID, itemId, movielensID, rating, timestamp));
	 			}
			}else if(method==2) { // contentmetadata
				sql = "SELECT newcontentNumID, contentNumID FROM "+schema+".contentmeta"
						+ " where newcontentNumID = "+itemID+";";
				rs0 = this.db.getStmt().executeQuery(sql);
				int id; int userID; int itemId; int movielensID; double rating; int timestamp;
				while(rs0.next()) {
					id = 1;
					userID = 1;
					itemId = rs0.getInt(1);
					movielensID = rs0.getInt(2);
					rating = 1;
					timestamp = 1;
					
					this.itemList.add(new RatingInfo(id, userID, itemId, movielensID, rating, timestamp));
	 			}
			}
			
			System.out.println("  아이템 "+itemID+" 에 대한 rating 정보 메모리 로딩 완료.");
			rs0.close();
		}catch(Exception e){
			System.err.println("loadAllRating_For_makeTratingSet_From_Item error: "+e.getMessage());
		}
	}
	
	
	
}



class UserInfo2{
	public int userID;
	public String userName;
	public int toUserID;
	
	public UserInfo2(int userID, String userName) {
		
		this.userID = userID;
		this.userName = userName;
	}
	
	public UserInfo2(int userID, int toUserID) {
		this.userID = userID;
		this.toUserID = toUserID;
	}
}



class ItemInfo2{
	public int itemID;
	public String title;
	public String genre;
	public String subgenre;
	public String date;
	public String language;
	public String director;
	public String actors;
	public String starts;
	public String summary;
	public String URL;
	public String keywords;
	
	public ItemInfo2(int itemID, String title, String URL) {
		this.itemID = itemID;
		this.title = title;
		this.URL = URL;
	}
	
	public ItemInfo2(int itemID, String genre, String subgenre, String date, String language,
			String director, String actors, String starts, String summary, String keywords) {
		
		this.itemID = itemID;
		//this.title = title;
		this.genre = genre;
		this.subgenre = subgenre;
		this.date = date;
		this.language = language;
		this.director = director;
		this.actors = actors;
		this.starts = starts;
		this.summary = summary;
		//this.URL = URL;
		this.keywords = keywords;
	}
	
	public ItemInfo2(int itemID, String title, String genre, String subgenre, String date, String language,
			String director, String actors, String starts, String summary, String URL, String keywords) {
		
		this.itemID = itemID;
		this.title = title;
		this.genre = genre;
		this.subgenre = subgenre;
		this.date = date;
		this.language = language;
		this.director = director;
		this.actors = actors;
		this.starts = starts;
		this.summary = summary;
		this.URL = URL;
		this.keywords = keywords;
	}
}


class ItemRatingInfo2{
	public int userID;
	public int itemID;
	public double rating;
	
	public ItemRatingInfo2(int userID, int itemID, double rating) {
		
		this.userID = userID;
		this.itemID = itemID;
		this.rating = rating;
	}
}