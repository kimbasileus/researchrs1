/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.tools.ietool.nlp;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import hyu.kskim.research.tools.db.FileIO;

public class StopWordEliminator {
	FileIO fp = new FileIO();
	String dir = null;
	HashSet<String> stopWordList = new HashSet<String>();
	int numOfStopWords = 0;
	
	public StopWordEliminator(){
		this.dir = ".\\stopwords.txt";
		readStopWordList();
	}
	
	public StopWordEliminator(String path){
		if(path != null) this.dir = ".\\stopwords.txt";
		else this.dir = path;
		readStopWordList();
	}
	
	public boolean isStopWord(String word){
		try{
			if( stopWordList.contains(word) ) return true;
			else return false;
		}catch(Exception e){
			System.err.println("isStopWord error: "+e.getMessage());
			return false;
		}
	}
	
	
	public boolean readStopWordList(){
		
		try{
			 BufferedReader reader = new BufferedReader(new FileReader(dir));
			 String inputLine = null;
			 while ((inputLine = reader.readLine()) != null){
				 System.out.println("Read stop word: "+inputLine);
				 stopWordList.add(inputLine);				 
				 this.numOfStopWords++;
			 }
			 reader.close();
			 
			 System.out.println("Complete read stop word list: the total number of stop words = "+this.numOfStopWords);

			 return true;
		}catch(Exception e){
			System.err.println("readStopWordList error: "+e.getMessage());
			return false;
		}
	}
	
	
	public ArrayList<String> getStopWordEliminatedSentence(ArrayList<String> tokenList){
		try{
			 ArrayList<String> newList = new ArrayList<String>();
			 int n = tokenList.size();
			 
			 String word = "";
			 for(int i=0; i < n; i++){
				 word = tokenList.get(i);
				 if(stopWordList.contains(word)) continue;
				 else newList.add(word);
			 }
			 
			 // System.out.println(" = "+this.numOfStopWords);

			 return newList;
		}catch(Exception e){
			System.err.println("getStopWordEliminatedSentence error: "+e.getMessage());
			return null;
		}		
	}
}
