/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.rs.metadata.tools.indexer;

import java.util.*;

class HashNode{ // hashtable �ڷ� ����
	public int numOfFreq;
	public int numOfDocs;
	public TreeMap<Integer, Integer> post = new TreeMap<Integer, Integer>(); // posting list�� red-black tree ������ ����
	
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
	
	// word: �ܾ�, docID: �ܾ �����ϴ� ������ ID, n: �ش� ���� �� �ܾ��� �� ��
	public boolean addWord(String word, int docID, int n){
		try{

			// Hash table�� ���Ͽ� �ش� �ܾ ���� �ߺ��� üũ
			if(table.containsKey(word)){ // Hashtable�� �ش� word�� �����ϴ� ���
				// Hash table�κ��� �ش� �ܾ ���� ���� ������ �� ��谪 ����
				HashNode node = table.get(word);
				node.numOfFreq += n;
				
				// �ش� posting file ������Ʈ�� ���Ͽ� ���Ͽ� �ߺ��� üũ
				if(node.post.containsKey(docID)){ // posting list�� �ش� �ܾ��� ������ �����ϴ� ���
					int num = node.post.get(docID) + n; // ���� �� �ش� �ܾ��� �󵵼��� ����
					node.post.replace(docID, num); // �ش� post ����
				}else{ // posting list�� �ش� �ܾ��� ������ �������� �ʴ� ���
					node.numOfDocs++;
					node.post.put(docID, n); // ����
				}
				
				// Hash table ����
				table.replace(word, node);
				
			}else{ // Hashtable�� �ش� word�� �������� �ʴ� ��� ��� ���� ���� ���� Hash table�� ����
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
			
			// �ش� ������ �ܾ� ���� �� ��� ���� ����
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
	
	// �־��� �ܾ��� �� �󵵼� (= Total Term frequency)
	public int getNumOfTotalFreq(String word){
		if(!this.table.containsKey(word)) return 0;

		HashNode node = this.table.get(word);
		
		if(node==null) return 0;
		
		return node.getTotalNumOfFreq();
	}

	
	// �־��� �ܾ �����ϴ� ������ ���� (=Document frequency)
	public int getNumOfTotalDocs(String word){
		if(!this.table.containsKey(word)) return 0;

		HashNode node = this.table.get(word);
		
		if(node==null) return 0;
		
		return node.getTotalNumOfDocs();
	}
	
	// �־��� �ܾ �����ϴ� �������� ID�� ����Ʈ Ÿ������ ��ȯ�Ѵ�.
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
	
	
	// �־��� �ܾ �����ϴ� �������� ID�� �ش� ���� �������� �󵵼��� ����Ʈ Ÿ������ ��ȯ�Ѵ�.
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
	
	
	// Ư�� ���� ������ �־��� �ܾ��� �󵵼� (=Term frequency in the document)
	public int getNumOfWordFreqInDoc(int docID, String word) {
		if(!this.table.containsKey(word)) return 0;
		if(!this.table.get(word).post.containsKey(docID) ) return 0;
		
		int freq = this.table.get(word).post.get(docID);
		
		return freq;
	}
}
