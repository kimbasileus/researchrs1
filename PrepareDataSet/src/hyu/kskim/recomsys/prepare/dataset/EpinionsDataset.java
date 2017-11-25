package hyu.kskim.recomsys.prepare.dataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import hyu.kskim.recomsys.prepare.utils.DBManager;
import hyu.kskim.recomsys.prepare.utils.FileIO;
import hyu.kskim.recomsys.prepare.utils.RandomNumbers;


public class EpinionsDataset {
	String schema = "epinionscom";
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
	
	// 0. ������
	public EpinionsDataset(String schema, int numOfUsers, int numOfItems, int numOfRatings) {
		this.db.connectDB("root", "kyungsookim");
		
		if(schema!=null) this.schema = schema;
		this.numOfUsers = numOfUsers; // 91735
		this.numOfItems = numOfItems; // 26527
		this.numOfRatings = numOfRatings; // 170452
	}
	
	
	// 0. �Ҹ���
	public void endMovieLensDataset() {
		this.db.closeDB();
	}
		
	
	/////////////////////////////////////// ���̺� ���� �� ���� �ڵ� ////////////////////////////////////////////////
	// �Լ� 1. ����� ���� ���̺� ���� �� ���� (users) (�Ϸ�)
	public void make_users_table() {
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
	
	// �Լ� 2. ����� �Ҽ� ��Ʈ��ũ ���� ���̺� ���� �� ���� (usertrust) (�Ϸ�)
	public void make_usertrust_table() {  
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
	
	
		
	// �Լ� 3. ������ ����&��Ÿ������ ���̺� ���� �� ���� (items) (�Ϸ�)
	public void make_items_table() {
		try {
			ItemInfo2[] itemList = new ItemInfo2[this.numOfItems+1];
			String sql = "SELECT newcontentNumID, contentName, contentURL FROM epinionsserver.content order by newcontentNumID asc;";
			//1. �⺻ content ���� �б� (ID, Name, URL)
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			while(rs0.next()) {
				int itemID = rs0.getInt(1);
				String title = rs0.getString(2).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				String URL = rs0.getString(3).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				
				itemList[itemID] = new ItemInfo2(itemID, title, URL);
				System.out.println("make_items_table\t item: "+itemID+" 1�� �ε� �Ϸ�. ");
			}
			rs0.close();
			
			
			//2. ��Ÿ������ ���� �б�                 1           2     3         4       5        6         7       8       9        10 
			String sql2 = "SELECT newcontentNumID, Genre, Subgenre, Date, Language, Director, Actors, Stars, Summary, keywords FROM epinionsserver.contentmeta;";
			ResultSet rs1 = this.db.getStmt().executeQuery(sql2);
			while(rs1.next()) {
				int itemID = rs1.getInt(1);
				itemList[itemID].genre = rs1.getString(2).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`").replace("/", ",");
				itemList[itemID].subgenre = rs1.getString(3).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`").replace("/", ",");
				itemList[itemID].date = rs1.getString(4).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				itemList[itemID].language = rs1.getString(5).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				itemList[itemID].director = rs1.getString(6).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				itemList[itemID].actors = rs1.getString(7).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				itemList[itemID].stars = rs1.getString(8).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				itemList[itemID].summary = rs1.getString(9).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`");
				itemList[itemID].keywords = rs1.getString(10).replace("'", "\\'").replace("\"", "\\\"").replace("`", "\\`").replace("::", ",");
			
				System.out.println("make_items_table\t item: "+itemID+" 2�� �ε� �Ϸ�. ");
			}
			rs1.close();
			
			//System.exit(0);
			// 3. DB�� �ϳ��� ����ϱ�.
			for(int i = 1; i < itemList.length; i++) {
				String sql3 = "INSERT INTO `epinionscom`.`items` (`ID`, `title`, `genres`, `subgenre`, `date`, `language`, `director`, `actors`, `stars`, `summary`, `URL`, `keywords`) "
						+ "VALUES ('"+itemList[i].itemID+"', '"+itemList[i].title+"', '"+itemList[i].genre+"', '"+itemList[i].subgenre+"', '"+itemList[i].date+"', '"+itemList[i].language+"', "
						+ "'"+itemList[i].director+"', '"+itemList[i].actors+"', '"+itemList[i].stars+"', '"+itemList[i].summary+"', '"+itemList[i].URL+"', '"+itemList[i].keywords+"');";
				this.db.getStmt().executeUpdate(sql3);
				System.out.println(i+"] make_items_table\t item: "+itemList[i].itemID+" ��Ÿ������ ��� �Ϸ�. ");
			}
		} catch (Exception e) {
			System.err.println("make_items_table Exception: "+e.getMessage());
		}
	}
	
	
	// �Լ� 4. �����-������ ���� ���̺� ���� �� ���� (ratings) �Ϸ�
	public void make_userRating_table() { 
		try {
			// 1. Load existing data
			String sql = "SELECT userID, newcontentNumID, rating, ratingDate FROM epinionsserver.ratings order by userID asc, newcontentNumID asc, ratingDate desc;";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			Queue<ItemRatingInfo2> userList = new LinkedList<ItemRatingInfo2>();
			
			int previousUserID = -100; int previousItemID = -100;
			while(rs0.next()) {
				int userID = rs0.getInt(1);
				int itemID = rs0.getInt(2);
				double rating = rs0.getDouble(3);
				String date = rs0.getString(4).replace("'", "").replace("/", "-");
				
				if(previousUserID == userID && previousItemID == itemID) continue;
				
				userList.add(new ItemRatingInfo2(userID, itemID, rating, date));
				
				previousUserID = userID;
				previousItemID = itemID;
			}
			rs0.close();
			
			// 2. write them onto the new table
			ItemRatingInfo2 ratingNode = null;
			int count = 0;
			while(!userList.isEmpty()) {
				ratingNode = userList.remove();
				count++;
				String sql2 = "INSERT INTO `epinionscom`.`ratings` (`ID`, `userID`, `itemID`, `rating`, `timestamp`) "
						+ "VALUES ('"+count+"', '"+ratingNode.userID+"', '"+ratingNode.itemID+"', '"+ratingNode.rating+"', '"+ratingNode.timeStamp+"');";
				
				db.getStmt().executeUpdate(sql2);
				System.out.println((count)+"] make_userRating_table: "+ratingNode.userID+"\t "+ratingNode.itemID+"\t "+ratingNode.rating);
			}

		}catch(Exception e) {
			System.err.println("make_userrating_table error: "+e.getMessage());
		}
	}
	
	
	// �Լ� 5. ������ ��Ʈ��ũ ���� �� ���� (itemnetwork) �Ϸ�
	public void make_itemnetwork_table() {
		try {
			// 1. Load existing data
			String sql = "SELECT from_movie, to_movie FROM epinionsserver.movie_linkdata order by from_movie asc, to_movie asc;";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			Queue<ItemLink2> userList = new LinkedList<ItemLink2>();
			
			int previousFromID = -100; int previousToID = -100;
			while(rs0.next()) {
				int fromID = rs0.getInt(1);
				int toID = rs0.getInt(2);
				
				if(previousFromID == fromID && previousToID == toID) continue;
				
				userList.add(new ItemLink2(fromID, toID));
				
				previousFromID = fromID;
				previousToID = toID;
			}
			rs0.close();
			
			// 2. write them onto the new table
			ItemLink2 itemLinkNode = null;
			int count = 0;
			while(!userList.isEmpty()) {
				itemLinkNode = userList.remove();
				count++;
				String sql2 = "INSERT INTO `epinionscom`.`itemnetwork` (`ID`, `fromItemID`, `toItemID`, `weight`) "
						+ "VALUES ('"+count+"', '"+itemLinkNode.fromItemID+"', '"+itemLinkNode.toItemID+"', '1.0');";
				
				db.getStmt().executeUpdate(sql2);
				System.out.println((count)+"] make_itemnetwork_table: "+itemLinkNode.fromItemID+"\t "+itemLinkNode.toItemID);
			}						
		}catch(Exception e) {
			System.err.println("make_itemnetwork_table error: "+e.getMessage());
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	////////////////////////////// trating & test ���� ���� �ڵ� //////////////////////////////////////////
	// �Լ� 1. ���� test dataset �����
	public void makeTestDataSet(String fileDir, String schema, double ratioOfTestRatings) {
		try {
			int percent = (int) (ratioOfTestRatings*100.0);
			
			int numOfTestRatings = (int) (this.numOfRatings*ratioOfTestRatings);
			this.numOfTestRatings = numOfTestRatings;
			this.numOfTrainingRatings = this.numOfRatings - this.numOfTestRatings;
			
			RandomNumbers rn = new RandomNumbers();
			//int testItemList[] = rn.getNRandomNumbers(1, this.numOfRatings, numOfTestRatings);
			HashSet<Integer> testItemList = rn.getNRandomNumbers_Set(1, this.numOfRatings, numOfTestRatings);
			Iterator<Integer> itr = testItemList.iterator();
			
			int id = 0; int userNo; int itemNo; double rating; String timeStamp;
			
			StringBuffer sb = new StringBuffer();
			ResultSet rs = null;
			int count = 0;
			while(itr.hasNext()) {
				id = itr.next();
				
				rs = this.db.getStmt().executeQuery("SELECT userID, itemID, rating, timestamp "
						+ "FROM `"+schema+"`.`ratings` where ID = "+id+";");
				
				while(rs.next()) {
					userNo = rs.getInt(1);
					itemNo = rs.getInt(2);
					rating = rs.getDouble(3);
					timeStamp = rs.getString(4);
					
					count++;
					System.out.println(count+"]\t"+id+","+userNo+","+itemNo+","+rating+","+timeStamp);
					
					sb.append(id+","+userNo+","+itemNo+","+rating+","+timeStamp).append("\n");
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
	
	
	// �Լ� 2. ���� test dataset �����
	public void makeCumulatedDataSet(String fileDir, double previous_ratio, double now_ratio) {
		try {
			double ratioOfTestRatings = now_ratio - previous_ratio;
			int percent = (int) (now_ratio*100.0);
			
			int numOfTestRatings = (int) (this.numOfRatings*ratioOfTestRatings);
			this.numOfTestRatings = numOfTestRatings;
			this.numOfTrainingRatings = this.numOfRatings - this.numOfTestRatings;
			
			
			// ������ �׽�Ʈ �����͵��� ID�� DB�κ��� �о���δ�.
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
			
			
			
			// ���� ����
			RandomNumbers rn = new RandomNumbers();
			
			testItemList = rn.getNCummulatedRandomNumbers_Set(1, this.numOfRatings, numOfTestRatings, testItemList);
			Iterator<Integer> itr = testItemList.iterator();
			
			int id = 0; int userNo; int itemNo; double rating; String timeStamp;
			//ArrayList<testSetPair> extractedList = new ArrayList<testSetPair>();
			
			StringBuffer sb = new StringBuffer();
			ResultSet rs = null;
			while(itr.hasNext()) {
				id = itr.next();
				
				rs = this.db.getStmt().executeQuery("SELECT userID, itemID, rating, timestamp "
						+ "FROM `"+schema+"`.`ratings` where ID = "+id+";");
				
				while(rs.next()) {
					userNo = rs.getInt(1);
					itemNo = rs.getInt(2);
					rating = rs.getDouble(3);
					timeStamp = rs.getString(4);
					
					System.out.println(id+","+userNo+","+itemNo+","+rating+","+timeStamp);
					
					sb.append(id+","+userNo+","+itemNo+","+rating+","+timeStamp).append("\n");
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
	
	
	
	// �Լ� 3. Test Dataset ���� �Լ�
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
	
	
	
	// �Լ� 4. Test DataSet DB�� ����ϱ�
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
			int id; int userID; int itemID; double rating; String timeStamp;
			while ((inputLine = reader.readLine()) != null){
				pair = inputLine.split(",");
				if(pair==null) continue;
				
				id = Integer.parseInt(pair[0]);
				userID = Integer.parseInt(pair[1]);
				itemID = Integer.parseInt(pair[2]);
				rating = Double.parseDouble(pair[3]);
				timeStamp = pair[4];
				
			// 3. Delete testset from training set and Write testset onto the testset DB
				this.db.getStmt().executeUpdate("DELETE FROM `"+schema+"`.`ratings_trainset` "
						+ "WHERE `ID`='"+id+"';");
				
				this.db.getStmt().executeUpdate("INSERT INTO `"+schema+"`.`ratings_testset` (`ID`, `userID`, `itemID`, `actual_rating`, `timestamp`) "
						+ "VALUES ('"+id+"', '"+userID+"', '"+itemID+"', '"+rating+"', '"+timeStamp+"');");
			}
			reader.close();
			
			
		}catch(Exception e){
			System.err.println("loadTrain_Test_Dataset_Into_DB error: "+e.getMessage());
		}
	}
	
	
	private void loadAllRating_For_makeTratingSet(String schema) {
		try {
			for(int itemID=1; itemID<=this.numOfItems; itemID++) {
				this.loadAllRating_For_makeTratingSet_From_Item(schema, itemID);
			}
			
			System.out.println("Original Rating Data is loaded: size is "+this.itemList.size());
			
			
			int n = this.itemList.size();
			RatingInfo node = null;
			for(int i=0; i < n ; i++) {
				node = this.itemList.get(i);
				String sql = "INSERT INTO `"+schema+"`.`ratings_trainset` (`ID`, `userID`, `itemID`, `rating`, `timestamp`) "
						+ "VALUES ('"+node.id+"', '"+node.userID+"', '"+node.itemID+"', '"+node.rating+"', '"+node.timestamp_string+"');";
				this.db.getStmt().executeUpdate(sql);
				
				System.out.println((i+1)+"��° rating�� trainset�� ���� �Ϸ�.");
			}
			
		}catch(Exception e){
			System.err.println("loadAllRating_For_makeTratingSet error: "+e.getMessage());
		}
	}
	
	
	private void loadAllRating_For_makeTratingSet_From_Item(String schema, int itemID) {
		try {
			String sql = "SELECT * FROM "+schema+".ratings where itemID = '"+itemID+"';";
						
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int id; int userID; int itemId; double rating; String timestamp;
			while(rs0.next()) {
				id = rs0.getInt(1);
				userID = rs0.getInt(2);
				itemId = rs0.getInt(3);
				rating = rs0.getDouble(4);
				timestamp = rs0.getString(5);
				
				this.itemList.add(new RatingInfo(id, userID, itemId, rating, timestamp));
 			}
			System.out.println("  ������ "+itemID+" �� ���� rating ���� �޸� �ε� �Ϸ�.");
			rs0.close();
		}catch(Exception e){
			System.err.println("loadAllRating_For_makeTratingSet_From_Item error: "+e.getMessage());
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////// Test Set�� ����޴� ���̺� ���� //////////////////////////////////////////
	// �Լ� 1. ����ں��� ���� ����� ���Ͽ� DB�� ĳ���ϴ� �Լ� (user_avg) �Ϸ�
	public void load_userAverage_Into_DB(String schema) {
		try {
			ArrayList<Integer> users = new ArrayList<Integer>();
			ArrayList<UserAvg> avgList = new ArrayList<UserAvg>();
			
			// 0. DB�κ��� ��� ����ڵ��� ID�� �ҷ��´�.
			ResultSet rs0 = this.db.getStmt().executeQuery("SELECT ID FROM "+schema+".users;");
			while(rs0.next()) {
				users.add(rs0.getInt(1));
			}
			rs0.close();
			
			
			// 1. training set DB�κ��� �� ����ں��� Rating ������ ������ �̵��� ����� ����Ͽ� �޸𸮿� �����Ѵ�.
			int n = users.size();
			int userID;
			for(int i=0; i < n; i++) { // User ID
				userID = users.get(i);
				
				ResultSet rs1 = this.db.getStmt().executeQuery("SELECT rating FROM "+schema+".ratings_trainset where userID = "+userID+";");
				double sum = 0; int num = 0; double avg=0;
				if(rs1!=null) {
					while(rs1.next()) {
						sum += rs1.getDouble(1);
						num++;
					}
				}
				if(num==0) avg = -1;
				else avg = sum/(double)num;
						
				avgList.add(new UserAvg(userID, avg, num, sum));
				rs1.close();
				System.out.println("\t-- User "+userID+"'s average: "+avg);
			}
			
			
			// 2. �� ����ں��� ���� ��հ����� DB�� ĳ���Ѵ�.
			n = avgList.size();
			UserAvg avgNode = null;
			
			this.db.getStmt().executeUpdate("delete FROM "+schema+".user_avg;"); // ������ ĳ�̵Ǿ� �ִ� ���� ��� �����.
			
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
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	////////////////////////////// Item ID ���� ���� �ڵ� ///////////////////////////////////////////////

	// �Լ� 1. ������ ��ID �� ��ID ���� ��Ȯ�� ���� �Լ�
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
			
			System.out.println("1.  ID and new ID �ε� �Ϸ�: the number of keys is "+itemIDs.size());
			System.out.println("------------");
			
			if(!isBasedItem) { // User-based
				for(int uID=1; uID<=this.numOfUsers; uID++) {
					//this.loadAllRating_For_makeTratingSet_From_User(schema, uID, method);
				}
			}else { // Item-based
				for(int iID=1; iID<=this.numOfItems; iID++) {
					this.loadAllRating_For_makeTratingSet_From_Item222(schema, iID, method);
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
	
	// �־��� �������� ��� �������� �ε��Ͽ� itemList �迭����Ʈ�� �����ϴ� �Լ�
	private void loadAllRating_For_makeTratingSet_From_Item222(String schema, int itemID, int method) {
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
			
			System.out.println("  ������ "+itemID+" �� ���� rating ���� �޸� �ε� �Ϸ�.");
			rs0.close();
		}catch(Exception e){
			System.err.println("loadAllRating_For_makeTratingSet_From_Item error: "+e.getMessage());
		}
	}
	
} // ���� Ŭ���� ����



// �����-����� ���� ���� �� �����-����� �̸� ���� ���� Ŭ����
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



// ������ ID �� ������ ��Ÿ������ ���� ���� Ŭ����
class ItemInfo2{
	public int itemID;
	public String title;
	public String genre;
	public String subgenre;
	public String date;
	public String language;
	public String director;
	public String actors;
	public String stars;
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
		this.stars = starts;
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
		this.stars = starts;
		this.summary = summary;
		this.URL = URL;
		this.keywords = keywords;
	}
}


// �����-������ ���� ������ �����ϴ� Ŭ����
class ItemRatingInfo2{
	public int userID;
	public int itemID;
	public double rating;
	public String timeStamp;
	
	public ItemRatingInfo2(int userID, int itemID, double rating, String timeStamp) {
		this.userID = userID;
		this.itemID = itemID;
		this.rating = rating;
		this.timeStamp = timeStamp;
	}
}


class ItemLink2{
	public int fromItemID;
	public int toItemID;
	
	public ItemLink2(int fromItemID, int toItemID) {
		this.fromItemID = fromItemID;
		this.toItemID = toItemID;
	}
}

