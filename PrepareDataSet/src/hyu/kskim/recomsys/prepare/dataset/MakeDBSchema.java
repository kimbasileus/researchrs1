package hyu.kskim.recomsys.prepare.dataset;

import java.sql.SQLException;

import hyu.kskim.recomsys.prepare.utils.DBManager;

public class MakeDBSchema {
	String schema = null;
	String dbId = "root";
	String dbPw = "kyungsookim";
	DBManager db = new DBManager(null, null, null);
	
	String new_Schema = null; // Generate new database schema
	String new_ist = null; // item to item similarity table
	String new_items = null; // item and its metadata
	String new_ratings = null; // user-item rating matrix
	String new_ratings_testset = null; // user-item rating matrix (test set)
	String new_ratings_trainset = null; // user-item rating matrix (training set)
	String new_user_avg = null; // user's rating average
	String new_users = null; // users
	String new_ust = null; // user to user similarity table
	
	public MakeDBSchema() { // 持失切
		this.db.connectDB("root", "kyungsookim");
	}
	
	public void endMakeDBSchema() { // 社瑚切
		this.db.closeDB();
	}
	
	public void makeSchema(String schema) {
		this.new_Schema = "CREATE DATABASE `"+schema+"`";
		this.new_ist = "CREATE TABLE `ist` (\r\n" + 
				"  `ID` int(11) NOT NULL,\r\n" + 
				"  `item1ID` int(11) NOT NULL,\r\n" + 
				"  `item2ID` int(11) NOT NULL,\r\n" + 
				"  `similarity` double DEFAULT NULL,\r\n" + 
				"  `A` double DEFAULT NULL,\r\n" + 
				"  `B` double DEFAULT NULL,\r\n" + 
				"  `C` double DEFAULT NULL,\r\n" + 
				"  `D` double DEFAULT NULL,\r\n" + 
				"  `E` double DEFAULT NULL,\r\n" + 
				"  `F` double DEFAULT NULL,\r\n" + 
				"  `G` double DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`item1ID`,`item2ID`),\r\n" + 
				"  KEY `ID` (`ID`) USING BTREE,\r\n" + 
				"  KEY `sim` (`similarity`) USING BTREE\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;\r\n";
		
		this.new_items = "CREATE TABLE `items` (\r\n" + 
				"  `ID` int(11) NOT NULL,\r\n" + 
				"  `movieID` int(11) DEFAULT NULL,\r\n" + 
				"  `name` mediumtext,\r\n" + 
				"  `genres` varchar(100) DEFAULT NULL,\r\n" + 
				"  `imdbID` int(11) DEFAULT NULL,\r\n" + 
				"  `tmdbID` int(11) DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`ID`),\r\n" + 
				"  KEY `movieID` (`movieID`) USING BTREE\r\n" + 
				") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		
		this.new_ratings = "CREATE TABLE `ratings` (\r\n" + 
				"  `ID` int(11) NOT NULL,\r\n" + 
				"  `userID` int(11) NOT NULL,\r\n" + 
				"  `itemID` int(11) NOT NULL,\r\n" + 
				"  `rating` double DEFAULT NULL,\r\n" + 
				"  `timestamp` int(11) DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`userID`,`itemID`),\r\n" + 
				"  KEY `ID` (`ID`) USING BTREE\r\n" + 
				") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		
		this.new_ratings_testset = "CREATE TABLE `ratings_testset` (\r\n" + 
				"  `ID` int(11) NOT NULL,\r\n" + 
				"  `userID` int(11) NOT NULL,\r\n" + 
				"  `itemID` int(11) NOT NULL,\r\n" + 
				"  `actual_rating` double DEFAULT NULL,\r\n" + 
				"  `predicted_rating` double DEFAULT NULL,\r\n" + 
				"  `timestamp` int(11) DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`userID`,`itemID`),\r\n" + 
				"  KEY `ID` (`ID`) USING BTREE\r\n" + 
				") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		
		this.new_ratings_trainset = "CREATE TABLE `ratings_trainset` (\r\n" + 
				"  `ID` int(11) NOT NULL,\r\n" + 
				"  `userID` int(11) NOT NULL,\r\n" + 
				"  `itemID` int(11) NOT NULL,\r\n" + 
				"  `rating` double DEFAULT NULL,\r\n" + 
				"  `timestamp` int(11) DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`userID`,`itemID`),\r\n" + 
				"  KEY `ID` (`ID`) USING BTREE\r\n" + 
				") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		
		this.new_user_avg = "CREATE TABLE `user_avg` (\r\n" + 
				"  `userID` int(11) NOT NULL,\r\n" + 
				"  `average` double DEFAULT NULL,\r\n" + 
				"  `numOfRating` int(11) DEFAULT NULL,\r\n" + 
				"  `sumOfRating` double DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`userID`),\r\n" + 
				"  KEY `avg` (`average`) USING BTREE\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		this.new_users = "CREATE TABLE `users` (\r\n" + 
				"  `ID` int(11) NOT NULL,\r\n" + 
				"  `name` varchar(45) DEFAULT NULL,\r\n" + 
				"  `age` int(11) DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`ID`)\r\n" + 
				") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		
		this.new_ust = "CREATE TABLE `ust` (\r\n" + 
				"  `ID` int(11) NOT NULL,\r\n" + 
				"  `user1ID` int(11) NOT NULL,\r\n" + 
				"  `user2ID` int(11) NOT NULL,\r\n" + 
				"  `similarity` double DEFAULT NULL,\r\n" + 
				"  `A` double DEFAULT NULL,\r\n" + 
				"  `B` double DEFAULT NULL,\r\n" + 
				"  `C` double DEFAULT NULL,\r\n" + 
				"  `D` double DEFAULT NULL,\r\n" + 
				"  `E` double DEFAULT NULL,\r\n" + 
				"  `F` double DEFAULT NULL,\r\n" + 
				"  `G` double DEFAULT NULL,\r\n" + 
				"  PRIMARY KEY (`user1ID`,`user2ID`),\r\n" + 
				"  KEY `ID` (`ID`) USING BTREE,\r\n" + 
				"  KEY `sim` (`similarity`) USING BTREE\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		
		try {
			System.out.println("Start making new schema: "+schema);
			this.db.getStmt().execute(this.new_Schema);
			this.db.getStmt().execute("use "+schema+";");
			
			this.db.getStmt().execute(this.new_ist);
			this.db.getStmt().execute(this.new_items);
			this.db.getStmt().execute(this.new_ratings);
			this.db.getStmt().execute(this.new_ratings_testset);
			this.db.getStmt().execute(this.new_ratings_trainset);
			this.db.getStmt().execute(this.new_user_avg);
			this.db.getStmt().execute(this.new_users);
			this.db.getStmt().execute(this.new_ust);
			
			System.out.println("End making new schema: "+schema);
		} catch (SQLException e) {
			System.err.println("makeSchema error: "+e.getMessage());
		}
	}
	
	
	public void deleteSchema(String schema) {
		try {
			System.out.println("Start deleting new schema: "+schema);
			
			//this.db.getStmt().execute("use "+schema+";");
			this.db.getStmt().execute("DROP DATABASE `"+schema+"`;");
			
			System.out.println("End deleting new schema: "+schema);
		} catch (SQLException e) {
			System.err.println("deleteSchema error: "+e.getMessage());
		}
		
	}

}
