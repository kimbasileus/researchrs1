/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.tools.ietool.indexer;

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
	HashMap<String, HashNode> table = new HashMap<String, HashNode>();
	HashMap<Integer, Integer> docIndex = new HashMap<Integer, Integer>();
	
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
			}
			
			System.out.println("Success adding the word \""+word+"\"\tin the inverted file: "+table.get(word).numOfDocs+" "+table.get(word).numOfFreq);
			
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
			
			System.out.println("Success delete the word "+word+" with its document "+docID+" in the list: frequency is "+n);
			
			return true;
		}catch(Exception e){
			System.err.println("deleteWordWithDoc() Exception: "+e.getMessage());
			return false;
		}
	}
	
	
	
	
	
	
	// �־��� �ܾ��� �� �󵵼� (= Term frequency)
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
	
	// Ư�� ���� ������ �־��� �ܾ��� �󵵼� (=Term frequency in the document)
	public int getNumOfWordFreqInDoc(int docID, String word) {
		if(!this.table.containsKey(word)) return 0;
		if(!this.table.get(word).post.containsKey(docID) ) return 0;
		
		int freq = this.table.get(word).post.get(docID);
		
		return freq;
	}
}
