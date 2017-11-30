package hyu.kskim.research.rs.metadata.collector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

public class WebCrawler {
	// 주어진 URL로부터 HTML Page를 다운로드하여 String type으로 반환한다. (범용)
	public String getWebPage(String url_address) {
		try {
			URL url = new URL(url_address);
			
			HttpURLConnection conn =(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept", "application/json");
	        
			StringBuffer sb = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = null;			
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine).append("\n");
			}
			in.close();
			
			String page = null;
			page = sb.toString().replace("\t", "");
			return page;
		}catch(Exception e) {
			System.err.println("getWebPage Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	// 주어진 URL로부터 HTML Page를 다운로드하여 String type으로 반환한다. (범용)
	public String getWebPage_for_Wiki(String url_address) {
		try {
			URL url = new URL(url_address);
			
			HttpURLConnection conn =(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept", "application/json");
	        
			StringBuffer sb = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = new String("".getBytes("UTF-8"),"UTF-8");;
			boolean isImportant = false;
			while ((inputLine = in.readLine()) != null) {
				if(!isImportant) {
					if(!inputLine.contains("From Wikipedia, the free encyclopedia")) continue;
					else isImportant = true;
					continue;
				}
				
				if(inputLine.contains("Edit section: Notes") || inputLine.contains("Edit section: References")) break;
				
				sb.append(inputLine).append("\n");
			}
			in.close();
			
			String page = new String("".getBytes("UTF-8"),"UTF-8");
			page = sb.toString().replace("\t", "");
			return page;
		}catch(Exception e) {
			System.err.println("getWebPage_for_Wiki Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	public ArrayList<String> extractURL_from_GoogleResult(String webPage, int limit) { // 최대 10개 page의 Link를 반환
		try {
			ArrayList<String> linkList = new ArrayList<String>();
			Pattern p = Pattern.compile("\"link\": \"(.*?)\"", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(webPage);
			
			int count = 0;
			while(m.find()) {
				String link = m.group(1).trim();
				if(link.length() < 1) continue;
				
				linkList.add(link);
				count++;
				if(count == limit) break;
			}
						
			if(linkList.size() == 0) return null;
			else return linkList;
		}catch(Exception e) {
			System.err.println("extractURL_from_GoogleResult Exception: "+e.getMessage());
			return null;
		}
	}
}
