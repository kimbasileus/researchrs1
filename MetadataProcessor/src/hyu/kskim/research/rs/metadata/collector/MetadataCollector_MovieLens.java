package hyu.kskim.research.rs.metadata.collector;

import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import hyu.kskim.research.rs.utils.DBManager;
import hyu.kskim.research.rs.utils.FileIO;

/*
 * Part 1. 웹으로부터 모든 아이템에 대한 문서를 수집하는 클래스 (Wiki, Imdb)
 */
public class MetadataCollector_MovieLens {
	String dir = null;
	String dbSchema = null;
	int numOfItems = 0;
	DBManager db = null;
	FileIO file = null;
	public WebCrawler crawler = null;
	
	public MetadataCollector_MovieLens(String schema, String dir, int numOfItems) {
		this.dbSchema = schema;
		this.dir = dir;
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
	////////////////////////////////// I. By Wikipedia ////////////////////////////////////////////////////
	public void runExtractWikiDocs() {
		try {
			String page =  null;
			StringBuffer sb = new StringBuffer();
			
			int fIndex = 0;
			for(int itemID = 1; itemID <= 9125; itemID++) {
				fIndex = (itemID/2000)+1;
				if(itemID%2000==0) fIndex--;
				
				page = runExtractWikiDocs(itemID, 0);
				//ratio = (itemID/1000)+1;
				if(page == null) {
					sb.append(itemID).append("\n");
					continue;
				}
				if(page.length() < 10) continue;
				
				this.file.writer(dir+"\\contentmetadata_"+fIndex+"\\item_"+itemID+".doc", page);
				this.db.getStmt().executeUpdate("INSERT INTO `"+this.dbSchema+"`.`items_metadata` (`itemID`, `source`) "
						+ "VALUES ('"+itemID+"', 'wiki');");
				System.out.println("Item "+itemID+" Disk에 쓰기 완료");
			}
			
			this.file.writer(dir+"\\contentmetadata_"+fIndex+"\\item_no_collected.txt", sb.toString());
		}catch(Exception e) {
			System.err.println("runExtractWebLinks-1 Exception: "+e.getMessage());
		}
	}
	
	
	// 주어진 아이템과 관련된 웹 문서들의 링크들을 구글 검색엔진으로부터 추출하여 반환한다.
	public String runExtractWikiDocs(int itemID, int trial) {
		try {
			
			
			// 1. 해당 아이템의 제목 구하기
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
			
			// 2. 해당 아이템의 제목을 이용하여 Google Query 생성 및 Google로부터 검색 결과 얻어오기
			//String key = "AIzaSyDuorW5sQTbxJxASqyi-MIfb3fbx7rjVPY";

			title = this.getCanonicalTitleForm(title);

			if(trial==1) {
				title  = title.replace("?", "");
				if(!title.contains("The_")) title = "The_"+title;
				else title.replace("The_", "");
			}else if(trial==2) {
				year = year -1;
			}
			
			String query = "https://en.wikipedia.org/w/index.php?search="+title;
			String webPage = new String("".getBytes("UTF-8"),"UTF-8");
			if(trial==0 || trial == 1) 
				webPage = this.crawler.getWebPage_for_Wiki(query + "_(film)");
			else //(trial == 2)
				webPage = this.crawler.getWebPage_for_Wiki(query + "_("+(year+2)+"_film)");
			
			System.out.println(title);
			
			// 추출 실패/
			boolean isCast = false; boolean isPlot = false;
			
			if(!webPage.contains("Cast") && !webPage.contains("Plot") && !webPage.contains("Directed by")) {
				webPage = this.crawler.getWebPage_for_Wiki(query + "_("+year+"_film)");
				System.out.println("경우 3");
			}
			if(!webPage.contains("Cast") && !webPage.contains("Plot") && !webPage.contains("Directed by")) {
				webPage = this.crawler.getWebPage_for_Wiki(query);
				System.out.println("경우 4");
			}

			if(!webPage.contains("Cast") && !webPage.contains("Plot") && !webPage.contains("Directed by")) return null;


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
	
	
	
	public void processUnCollectedItems(int trial) {
		try {
			// 1. 
			ArrayList<Integer> list = new ArrayList<Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(dir+"\\contentmetadata_temp\\item_no_collected.txt"));
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
				
				page = runExtractWikiDocs(itemID, trial);
				//ratio = (itemID/1000)+1;
				if(page == null) {
					sb.append(itemID).append("\n");
					continue;
				}
				if(page.length() < 10) continue;
				
				this.file.writer(dir+"\\contentmetadata_temp\\item_"+itemID+".doc", page);
				this.db.getStmt().executeUpdate("INSERT INTO `"+this.dbSchema+"`.`items_metadata` (`itemID`, `source`) "
						+ "VALUES ('"+itemID+"', 'wiki');");
				
				System.out.println("Item "+itemID+" Disk에 쓰기 완료");
			}
			
			
			this.file.writer(dir+"\\contentmetadata_temp\\item_no_collected_2.txt", sb.toString());
		}catch(Exception e) {
			System.err.println("processUnCollectedItems Exception: "+e.getMessage());
			return;
		}
	}
	
	//////////////////////////////////II. By Google API ////////////////////////////////////////////////////
	public ArrayList<String> runExtractRelatedLinks_from_GoogleAPI(int itemID, int limit) {
		try {
			// 1. 해당 아이템의 제목 구하기
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
			
			// 2. 해당 아이템의 제목을 이용하여 Google Query 생성 및 Google로부터 검색 결과 얻어오기
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
	public void runExtractIMDBDocs() {
		try {
			// 1. 
			ArrayList<Integer> list = new ArrayList<Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(dir+"\\contentmetadata_temp\\item_no_collected.txt"));
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
			int count = 0;
			for(int i=0; i < list.size(); i++) {
				int itemID = list.get(i);
				
				count++;
				page = getPageFromIMDB(itemID);
				
				if(page == null) {
					sb.append(itemID).append("\n");
					continue;
				}
				if(page.length() < 10) continue;
				
				this.file.writer(dir+"\\contentmetadata_temp\\item_"+itemID+".doc", page);
				/*this.db.getStmt().executeUpdate("INSERT INTO `"+this.dbSchema+"`.`items_metadata` (`itemID`, `source`) "
						+ "VALUES ('"+itemID+"', 'imdb');");
				*/
				System.out.println("Item "+itemID+" Disk에 쓰기 완료");
				
				// System.exit(0);
				if(count%15==0) {
					System.out.println("1분간 휴식");
					Thread.sleep(1000*60);
				}
			}
			
			
			this.file.writer(dir+"\\contentmetadata_temp\\item_no_collected_2.txt", sb.toString());
		}catch(Exception e) {
			System.err.println("processUnCollectedItems Exception: "+e.getMessage());
			return;
		}
	}
	
	public String getPageFromIMDB(int itemID) {
		try {
			int imdbID = -1;
			
			
			String imdbPage = null; String imdbPageCast = null;
			
			StringBuffer sb = new StringBuffer();
			StringBuffer imdbSb = new StringBuffer();
			
			String sql = "SELECT imdbID, tmdbID FROM movielens.items where ID = "+itemID+";";
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			while(rs0.next()) {
				imdbID = rs0.getInt(1);
			}
			rs0.close();
			
			if(imdbID!=-1) {
				String query2 = "http://www.imdb.com/title/tt"+imdbID+"/plotsummary";
				String query3 = "http://www.imdb.com/title/tt"+imdbID+"/fullcredits";
				imdbPage = this.crawler.getWebPage_for_IMDB(query2);
				imdbPageCast = this.crawler.getWebPage_for_IMDB_CastingList(query3);
			}
			
		/*	if(tmdbID !=-1) {
				String query3 = "https://api.themoviedb.org/3/movie/"+tmdbID+"?api_key=b09f9b1f96f996830178b77e5686289b";
				tmdbPage = this.crawler.getWebPage(query3);
			}
			*/
			
			if((imdbPage != null ) &&  (imdbPageCast != null ) ) {
				imdbSb.append(imdbPage).append("\n").append(imdbPageCast);
				return imdbSb.toString();
			}
			else if(imdbPage == null)
				return ("\n"+imdbPageCast);
			else
				return imdbPage;
		}catch(Exception e) {
			System.err.println("getPageFromIMDB Exception: "+e.getMessage());
			return null;
		}
	}
}
