package hyu.kskim.recomsys.prepare.utils;

import java.sql.*;

/**
 *
 * @author KyungSoo Kim
 */

public class DBManager {
	private String jdbcDriver = "com.mysql.jdbc.Driver"; // JDBC Driver Name
	private String dbURL = "jdbc:mysql://localhost:3306/?autoReconnect=true&useSSL=false"; // DB URL: For solving SSL problem: autoReconnect=true&useSSL=false
	
	private String USERNAME = "root"; // Default option
	private String PASSWORD = "kyungsookim";
	
	private Connection conn = null;
	private Statement stmt = null;
	
	//////////////////////////////////// Constructor ///////////////////////////////////////////////////
	//public DBManager(){}
		
	public DBManager(String jdbcDriverName, String dbURL, String connOptions){
		if(jdbcDriverName != null) this.jdbcDriver = jdbcDriverName;
		if(dbURL != null) this.dbURL = dbURL;
		if(connOptions != null) this.dbURL = this.dbURL + "?"+connOptions;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////


	////////////////////////////// Connection & Close DB ///////////////////////////////////////////////
	public boolean connectDB(String userID, String passWord) {
		try{
			if(userID !=null ) this.USERNAME = userID;
			if(passWord !=null) this.PASSWORD = passWord;
			
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(dbURL,USERNAME,PASSWORD);
			stmt = conn.createStatement();
			
			System.out.println("\n- MySQL Database is successfully connected.");
			return true;
		}catch(Exception e){
			System.err.println("openDBConnector Exception: "+e.getMessage());
			conn = null; stmt = null;
			return false;
		}
	}
	
	
	public boolean closeDB() {
		try{
			if (this.conn!=null) conn.close();
			if (this.stmt!=null) stmt.close();
			
			conn = null; stmt = null;
			
			System.out.println("\n- MySQL Database is successfully closed.");
			return true;
		}catch(Exception e){
			System.err.println("closeDBConnector Exception: "+e.getMessage());
			conn = null; stmt = null;
			return false;
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////// Getter methods /////////////////////////////////////////////
	public Statement getStmt() {
		return stmt;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
}