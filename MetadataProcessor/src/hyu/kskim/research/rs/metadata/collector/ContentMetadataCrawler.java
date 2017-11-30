package hyu.kskim.research.rs.metadata.collector;

import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import hyu.kskim.research.rs.utils.DBManager;
import hyu.kskim.research.rs.utils.FileIO;

public class ContentMetadataCrawler {
	String dir = null;
	String dbSchema = null;
	int numOfItems = 0;
	DBManager db = null;
	FileIO file = null;
	WebCrawler crawler = null;
	
	public ContentMetadataCrawler(String schema, String dir, int numOfItems) {
		this.dbSchema = schema;
		this.dir = dir;
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
	////////////////////////////////// I. By Wikipedia ////////////////////////////////////////////////////
	public void runExtractWikiDocs() {
		try {
			String page =  null;
			StringBuffer sb = new StringBuffer();
			int ratio = 1;
			for(int itemID = 4001; itemID <= 6000; itemID++) {
				page = runExtractWikiDocs(itemID, true);
				//ratio = (itemID/1000)+1;
				if(page == null) {
					sb.append(itemID).append("\n");
					continue;
				}
				if(page.length() < 10) continue;
				
				this.file.writer(dir+"\\item_"+itemID+".doc", page);
				System.out.println("Item "+itemID+" Disk�� ���� �Ϸ�");
			}
			
			this.file.writer(dir+"\\item_no_collected.txt", sb.toString());
		}catch(Exception e) {
			System.err.println("runExtractWebLinks-1 Exception: "+e.getMessage());
		}
	}
	
	
	// �־��� �����۰� ���õ� �� �������� ��ũ���� ���� �˻��������κ��� �����Ͽ� ��ȯ�Ѵ�.
	public String runExtractWikiDocs(int itemID, boolean isFirst) {
		try {
			// 1. �ش� �������� ���� ���ϱ�
			String sql = "SELECT `name`, `year` FROM "+this.dbSchema+".items where ID = "+itemID+";";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			String title = null;
			int year = -1;
			while(rs0.next()) {
				title = rs0.getString(1);
				year = rs0.getInt(2);
			}
			rs0.close();
			if(title==null || title.length()==0) return null;
			
			// 2. �ش� �������� ������ �̿��Ͽ� Google Query ���� �� Google�κ��� �˻� ��� ������
			//String key = "AIzaSyDuorW5sQTbxJxASqyi-MIfb3fbx7rjVPY";

			title = this.getCanonicalTitleForm(title);
			
			// ��õ��� ��� title�� ���� �� ��ȭ�Ѵ�.
			if(!isFirst) {
				title  = title.replace("?", "");
			}
			String query = "https://en.wikipedia.org/w/index.php?search="+title;
			String webPage = new String("".getBytes("UTF-8"),"UTF-8");
			webPage = this.crawler.getWebPage_for_Wiki(query + "_(film)");
		
			System.out.println(title);
			
			// ���� ����/
			boolean isCast = false; boolean isPlot = false;
			if(!webPage.contains("Cast") && !webPage.contains("Plot") && !webPage.contains("Directed by")) {
				webPage = this.crawler.getWebPage_for_Wiki(query + "_("+year+"_film)");
				System.out.println("��� 3");
			}
			if(!webPage.contains("Cast") && !webPage.contains("Plot") && !webPage.contains("Directed by")) {
				webPage = this.crawler.getWebPage_for_Wiki(query);
				System.out.println("��� 4");
			}
			
			if(!webPage.contains("Plot") && !webPage.contains("Cast") && !webPage.contains("Directed by")) return null;
			
			// webPage = webPage + "END_File";
			webPage = webPage.replaceAll("<.*?>", "").replaceAll("\\^", "").replaceAll("\\(.*?\\)", "").
					replaceAll("\\[(.*?)\\]", "").replaceAll("\\{.*?\\}", "").replaceAll("[&][#](.*?)[;]", " ");
			
			
			System.out.println(webPage);
			return webPage;
		}catch(Exception e) {
			System.err.println("runExtractWebLinks-2 Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	
	private String getCanonicalTitleForm(String rawTitle) {
		try {
			String title = rawTitle;
			title = title.replaceAll(" \\([0-9]{4}\\)", "");
			if( title.contains(",  Then") || title.contains(",  then") || title.contains(",  They") || title.contains(",  they") || title.contains(",  These") || title.contains(",  these")) {
				
			}else if( title.contains(",  The") ) {
				title = title.replace(",  The","");
				title = "The "+title;
			}else if(title.contains(",  A")) {
				title = title.replace(",  A","");
				title = "A "+title;
			}else if(title.contains(",  An")) {
				title = title.replace(",  An","");
				title = "A "+title;
			}
			
			title = title.replaceAll(" \\(.*?\\) ", "").replaceAll(" \\(.*?\\)", "").replaceAll("\\(.*?\\) ", "").replaceAll("\\(.*?\\)", "");
			title = title.replace(" ", "_").replace("\"", "").replace("'", "%27").replaceAll("&", "%26");
			
			return title;
		}catch(Exception e) {
			System.err.println("getCanonicalTitle-2 Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	
	public void processUnCollectedItems() {
		try {
			// 1. 
			ArrayList<Integer> list = new ArrayList<Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(dir+"\\item_no_collected.txt"));
			String inputLine = null;
			
			while ((inputLine = reader.readLine()) != null){
				System.out.println(inputLine);
				inputLine = inputLine.replace("\n", "");
				list.add(Integer.parseInt(inputLine));
			}
			reader.close();
			if(list.size() == 0) return;
			
			// 2. 
			String page = null;
			StringBuffer sb = new StringBuffer();
			for(int i=0; i < list.size(); i++) {
				int itemID = list.get(i);
				
				page = runExtractWikiDocs(itemID, false);
				//ratio = (itemID/1000)+1;
				if(page == null) {
					sb.append(itemID).append("\n");
					continue;
				}
				if(page.length() < 10) continue;
				
				this.file.writer(dir+"\\item_"+itemID+".doc", page);
				System.out.println("Item "+itemID+" Disk�� ���� �Ϸ�");
			}
			
			
			this.file.writer(dir+"\\item_no_collected_2.txt", sb.toString());
		}catch(Exception e) {
			System.err.println("processUnCollectedItems Exception: "+e.getMessage());
			return;
		}
	}
	
	//////////////////////////////////II. By Google API ////////////////////////////////////////////////////
	public ArrayList<String> runExtractRelatedLinks_from_GoogleAPI(int itemID, int limit) {
		try {
			// 1. �ش� �������� ���� ���ϱ�
			String sql = "SELECT `name`, `year` FROM "+this.dbSchema+".items where ID = "+itemID+";";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			String title = null;
			int year = -1;
			while(rs0.next()) {
				title = rs0.getString(1);
				year = rs0.getInt(2);
			}
			rs0.close();
			if(title==null || title.length()==0) return null;
			
			// 2. �ش� �������� ������ �̿��Ͽ� Google Query ���� �� Google�κ��� �˻� ��� ������
			String key = "AIzaSyDuorW5sQTbxJxASqyi-MIfb3fbx7rjVPY";
			
			title = title.replace("\"", "").replace(" ", "");
			
			String query = "https://www.googleapis.com/customsearch/v1?key="+key+"&cx=014476702540917467452:nn7oosdp2ao&q="+title+"&alt=json&start=1";
			String webPage = null;
			webPage = this.crawler.getWebPage(query);
		
			System.out.println(title);
			
			if(webPage==null) return null;
			
			
			// 3. 
			ArrayList<String> linkList = this.crawler.extractURL_from_GoogleResult(webPage, limit);
			
			if(linkList==null || linkList.size()==0) return null;
			
			return linkList;
		}catch(Exception e) {
			System.err.println("runExtractWebLinks-2 Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	
	
	//////////////////////////////////III. By IMDB ////////////////////////////////////////////////////
}
