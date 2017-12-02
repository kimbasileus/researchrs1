package hyu.kskim.research.rs.metadata.test;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.security.auth.login.CredentialException;
import javax.security.auth.login.FailedLoginException;

import java.io.*;

public class SearchAPI {
	
	public void apitest() {
		// Key: AIzaSyDXKjgITul-T7jGINpSfK3TZDYQWiFfLWo
		System.out.println("Start");
		
		try{
			String key = "AIzaSyDXKjgITul-T7jGINpSfK3TZDYQWiFfLWo";
			String query = "Trigger_Effect_The_(1996)";
			// StackOverflow Exaxmple
			//URL url = new URL("https://www.googleapis.com/customsearch/v1?key="+key+"&cx=013036536707430787589:_pqjad5hr1a&q="+query+"&alt=json");
			
			// Example Search Engine ID
			// URL url = new URL("https://www.googleapis.com/customsearch/v1?key="+key+"&cx=017576662512468239146:omuauf_lfve&q="+query+"&alt=json&start=41");
			
			// Personal Search Engine ID
			//URL url = new URL("https://www.googleapis.com/customsearch/v1?key="+key+"&cx=014476702540917467452:nn7oosdp2ao&q="+query+"&alt=json&start=1");
			
			/*
			 * 
			 */
			URL url = new URL("https://api.themoviedb.org/3/movie/15602?api_key=b09f9b1f96f996830178b77e5686289b");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Accept", "application/json");
	        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	
	        StringBuffer sb = new StringBuffer();
	        String output;
	        System.out.println("Output from Server .... \n");
	        while ((output = br.readLine()) != null) {
	        	output = output.replaceAll("<(.*?)>", "").replace("\t", "");
	        	
	        	if(output.length()<=2) continue;
	        	if(output.length()>=1 && output.charAt(0) == '^' ) continue;
	        	
	        	sb.append(output).append("\n");
	        }
	        
	        String doc = sb.toString();
	        System.out.println(doc);
	        conn.disconnect();
		}catch(Exception e){
			System.err.println("Exception: "+e.getMessage());
		}
	}
}