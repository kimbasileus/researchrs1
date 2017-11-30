package hyu.kskim.research.rs.metadata.collector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

public class WebCrawler {
	public String getWebPage(String url_address) {
		try {
			URL url = new URL(url_address);
			
			HttpURLConnection conn =(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept", "application/json");
	        
			StringBuffer sb = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			int count = 1;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine).append("\n");
				//System.out.println((count)+"] \t"+inputLine);
				count++;
				//if (count == 198) break;
			}
			in.close();
			
			return sb.toString();
		}catch(Exception e) {
			System.err.println("getWebPage Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	public ArrayList<String> extractURL_from_GoogleResult(String webPage, int maxLength) { // 최대 10개 page의 Link를 반환
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
				if(count == maxLength) break;
			}
						
			if(linkList.size() == 0) return null;
			else return linkList;
		}catch(Exception e) {
			System.err.println("extractURL Exception: "+e.getMessage());
			return null;
		}
	}
	
	
	
}
