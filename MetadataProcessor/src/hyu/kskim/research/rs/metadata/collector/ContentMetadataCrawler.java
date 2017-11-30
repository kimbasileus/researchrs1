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
	 * ���� ����
	 * 1. ������ �����۰� ���õ� �� �������� ��ũ���� ���� �˻��������κ��� �����ϰ� �̸� DB�� ����
	 * 2. DB�κ��� �����۵��� ��ũ�� �ϳ��� �о document �ε� �� word ����
	 * 3. ����� word�� DB��, document ������ Disk�� ����
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
	
	
	// �־��� �����۰� ���õ� �� �������� ��ũ���� ���� �˻��������κ��� �����Ͽ� ��ȯ�Ѵ�.
	public String runExtractWebLinks(int itemID, int maxLength) {
		try {
			// 1. �ش� �������� ���� ���ϱ�
			String sql = "SELECT `name` FROM "+this.dbSchema+".items where ID = "+itemID+";";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			String title = null;
			while(rs0.next()) {
				title = rs0.getString(1);
			}
			rs0.close();
			if(title==null || title.length()==0) return null;
			
			// 2. �ش� �������� ������ �̿��Ͽ� Google Query ���� �� Google�κ��� �˻� ��� ������
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
			
			
			// 3. Google �˻� ����κ��� ������� ��ũ�� ����
			ArrayList<String> links = this.crawler.extractURL_from_GoogleResult(webPage, maxLength);
			if(links.size()==0) return null;
			
			// 4. ������ ��ũ�� �ϳ��� ����Ʈ�� ��ġ��
			StringBuffer sb = new StringBuffer();
			for(int i=0; i < links.size(); i++) {
				if(i != links.size()-1) sb.append(links.get(i)).append("<<<>>>");
				else sb.append(links.get(i));
			}
			
			// 5. �ش� �������� Google �˻� ��� ��ũ���� ��ȯ�Ѵ�.
			return sb.toString();
		}catch(Exception e) {
			System.err.println("runExtractWebLinks-2 Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	
	
	
	
	
}
