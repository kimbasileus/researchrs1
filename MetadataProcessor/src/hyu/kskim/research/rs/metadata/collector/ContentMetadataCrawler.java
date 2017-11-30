package hyu.kskim.research.rs.metadata.collector;

import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import hyu.kskim.research.rs.utils.DBManager;
import hyu.kskim.research.rs.utils.FileIO;

public class ContentMetadataCrawler {
	String dbSchema = null;
	int numOfItems = 0;
	DBManager db = null;
	FileIO file = null;
	WebCrawler crawler = null;
	
	public ContentMetadataCrawler(String schema, int numOfItems) {
		this.dbSchema = schema;
		this.numOfItems = numOfItems;
		this.db = new DBManager(null, null, null);
		this.file = new FileIO();
		this.crawler = new WebCrawler();
		
		this.db.connectDB("root", "kyungsookim");
	}
	
	/*
	 * 실행 순서
	 * 1. 각각의 아이템과 관련된 웹 문서들의 링크들을 구글 검색엔진으로부터 추출하고 이를 DB에 저장
	 * 2. DB로부터 아이템들의 링크를 하나씩 읽어서 document 로딩 및 word 추출
	 * 3. 추출된 word는 DB에, document 원본은 Disk에 저장
	 */
	public void runExtractWebLinks() {
		try {
			String linkList =  null;
			for(int itemID = 1; itemID <= this.numOfItems; itemID++) {
				linkList = runExtractWebLinks(itemID, 1);
				if(linkList == null) continue;
				
				System.out.println(linkList);
				String sql = "INSERT INTO `"+this.dbSchema+"`.`items_metadata` (`itemID`, `reference`) "
						+ "VALUES ('"+itemID+"', '"+linkList+"');";
				
				this.db.getStmt().executeUpdate(sql);
			}
		}catch(Exception e) {
			System.err.println("runExtractWebLinks-1 Exception: "+e.getMessage());
		}
	}
	
	
	// 주어진 아이템과 관련된 웹 문서들의 링크들을 구글 검색엔진으로부터 추출하여 반환한다.
	public String runExtractWebLinks(int itemID, int maxLength) {
		try {
			// 1. 해당 아이템의 제목 구하기
			String sql = "SELECT `name` FROM "+this.dbSchema+".items where ID = "+itemID+";";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			String title = null;
			while(rs0.next()) {
				title = rs0.getString(1);
			}
			rs0.close();
			if(title==null || title.length()==0) return null;
			
			// 2. 해당 아이템의 제목을 이용하여 Google Query 생성 및 Google로부터 검색 결과 얻어오기
			//String key = "AIzaSyDXKjgITul-T7jGINpSfK3TZDYQWiFfLWo";
			String key = "AIzaSyDuorW5sQTbxJxASqyi-MIfb3fbx7rjVPY";

			if( title.contains(",  The (") || title.contains(", The (") ) {
				title = title.replace(",  The "," ");
				title = "The "+title;
			}
			title = title.replace(" ", "_").replace(",", "").replace("\"", "");
			//title = "The_American_President_(1995)";
			String query = "https://www.googleapis.com/customsearch/v1?key="+key+"&cx=014476702540917467452:nn7oosdp2ao&q="+title+"&alt=json&start=1";
			String webPage = this.crawler.getWebPage(query);
			
			
			// 3. Google 검색 결과로부터 순서대로 링크를 추출
			ArrayList<String> links = this.crawler.extractURL_from_GoogleResult(webPage, maxLength);
			if(links.size()==0) return null;
			
			// 4. 각각의 링크를 하나의 리스트로 합치기
			StringBuffer sb = new StringBuffer();
			for(int i=0; i < links.size(); i++) {
				if(i != links.size()-1) sb.append(links.get(i)).append("<<<>>>");
				else sb.append(links.get(i));
			}
			
			// 5. 해당 아이템의 Google 검색 결과 링크들을 반환한다.
			return sb.toString();
		}catch(Exception e) {
			System.err.println("runExtractWebLinks-2 Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	
	
	
	
	
}
