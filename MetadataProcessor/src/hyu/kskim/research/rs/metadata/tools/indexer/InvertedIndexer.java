/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.rs.metadata.tools.indexer;

import java.util.*;

class HashNode{ // hashtable 자료 구조
	public int numOfFreq;
	public int numOfDocs;
	public TreeMap<Integer, Integer> post = new TreeMap<Integer, Integer>(); // posting list는 red-black tree 구조로 저장
	
	public HashNode(){
		this.numOfFreq = 0;
		this.numOfDocs = 0;
	}
	
	public int getTotalNumOfFreq(){
		return numOfFreq;
	}
	
	public int getTotalNumOfDocs(){
		return numOfDocs;
	}
}


public class InvertedIndexer {
	public int numOfTotalWords = 0;
	
	
	TreeMap<String, HashNode> table = new TreeMap<String, HashNode>();
	//HashMap<Integer, Integer> docIndex = new HashMap<Integer, Integer>();
	
	// word: 단어, docID: 단어를 포함하는 문서의 ID, n: 해당 문서 내 단어의 빈도 수
	public boolean addWord(String word, int docID, int n){
		try{

			// Hash table에 대하여 해당 단어에 대한 중복성 체크
			if(table.containsKey(word)){ // Hashtable에 해당 word가 존재하는 경우
				// Hash table로부터 해당 단어에 대한 정보 얻어오기 및 통계값 갱신
				HashNode node = table.get(word);
				node.numOfFreq += n;
				
				// 해당 posting file 업데이트를 위하여 대하여 중복성 체크
				if(node.post.containsKey(docID)){ // posting list에 해당 단어의 문서가 존재하는 경우
					int num = node.post.get(docID) + n; // 문서 내 해당 단어의 빈도수를 갱신
					node.post.replace(docID, num); // 해당 post 갱신
				}else{ // posting list에 해당 단어의 문서가 존재하지 않는 경우
					node.numOfDocs++;
					node.post.put(docID, n); // 삽입
				}
				
				// Hash table 갱신
				table.replace(word, node);
				
			}else{ // Hashtable에 해당 word가 존재하지 않는 경우 모두 새로 만든 다음 Hash table에 삽입
				HashNode node = new HashNode();
				node.numOfDocs++;
				node.numOfFreq += n;
				node.post.put(docID, n);
				
				table.put(word, node);
				
				this.numOfTotalWords++;
			}
			
			//System.out.println("Success adding the word \""+word+"\"\tin the inverted file: "+table.get(word).numOfDocs+" "+table.get(word).numOfFreq);
			
			return true;
		}catch(Exception e){
			System.err.println("addWord() Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	public boolean deleteWord(String word){
		try{
			if(this.table.isEmpty()) return false;
			
			if(!this.table.containsKey(word)) return false;
			
			this.table.remove(word);
			this.numOfTotalWords--;
			
			System.out.println("Success delete the word "+word+" in the list.");
			return true;
		}catch(Exception e){
			System.err.println("deleteWord() Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	public boolean deleteWordWithDoc(String word, int docID){
		try{
			if(this.table.isEmpty()) return false;
			if(!this.table.containsKey(word)) return false;
			
			HashNode node = this.table.get(word);
			if(node.post.isEmpty()) return false;
			if(!node.post.containsKey(docID)) return false;
			
			int n = 0;
			n = node.post.remove(docID); // the number of words in the document (docID)
			
			// 해당 문서의 단어 삭제 후 통계 정보 갱신
			node.numOfDocs--;
			node.numOfFreq -= n;
			
			this.numOfTotalWords--;
			
			System.out.println("Success delete the word "+word+" with its document "+docID+" in the list: frequency is "+n);
			
			return true;
		}catch(Exception e){
			System.err.println("deleteWordWithDoc() Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	public ArrayList<String> allWordList(){
		if(this.getNumOfTotalWords()==0) return null;
		
		ArrayList<String> wordList = new ArrayList<String>();
		Set<String> list = this.table.keySet();
		
		for(Iterator<String> it = list.iterator(); it.hasNext(); ) {
			wordList.add(it.next());
		}
		
		if(wordList.size()==0) return null;
		else return wordList;
	}
	
	
	public int getNumOfTotalWords() {
		return this.numOfTotalWords;
	}
	
	// 주어진 단어의 총 빈도수 (= Total Term frequency)
	public int getNumOfTotalFreq(String word){
		if(!this.table.containsKey(word)) return 0;

		HashNode node = this.table.get(word);
		
		if(node==null) return 0;
		
		return node.getTotalNumOfFreq();
	}

	
	// 주어진 단어를 포함하는 문서의 개수 (=Document frequency)
	public int getNumOfTotalDocs(String word){
		if(!this.table.containsKey(word)) return 0;

		HashNode node = this.table.get(word);
		
		if(node==null) return 0;
		
		return node.getTotalNumOfDocs();
	}
	
	// 주어진 단어를 포함하는 문서들의 ID를 리스트 타입으로 반환한다.
	public ArrayList<Integer> getNAlllDocs_having_word(String word){
		if(!this.table.containsKey(word)) return null;

		HashNode node = this.table.get(word);
		
		if(node==null) return null;
		if(node.post == null || node.post.size()==0) return null;
		
		ArrayList<Integer> docIDs = new ArrayList<Integer>();
		Set<Integer> keys = node.post.keySet();
		for(Iterator<Integer> it = keys.iterator(); it.hasNext(); ) {
			docIDs.add(it.next());
		}
		
		if(docIDs.size()==0) return null;
		return docIDs;
	}
	
	
	// 주어진 단어를 포함하는 문서들의 ID와 해당 문서 내에서의 빈도수를 리스트 타입으로 반환한다.
	public ArrayList<WordFreq> getNAlllDocs_and_Freq_having_word(String word){
		if(!this.table.containsKey(word)) return null;

		HashNode node = this.table.get(word);
		
		if(node==null) return null;
		if(node.post == null || node.post.size()==0) return null;
		
		ArrayList<WordFreq> docObjs = new ArrayList<WordFreq>();
		Set<Integer> keys = node.post.keySet();
		int id=0; int freq=0;
		for(Iterator<Integer> it = keys.iterator(); it.hasNext(); ) {
			id = it.next();
			freq = node.post.get(id);
			
			docObjs.add(new WordFreq(id, freq));
		}
		
		if(docObjs.size()==0) return null;
		return docObjs;
	}		
	
	
	// 특정 문서 내에서 주어진 단어의 빈도수 (=Term frequency in the document)
	public int getNumOfWordFreqInDoc(int docID, String word) {
		if(!this.table.containsKey(word)) return 0;
		if(!this.table.get(word).post.containsKey(docID) ) return 0;
		
		int freq = this.table.get(word).post.get(docID);
		
		return freq;
	}
}
