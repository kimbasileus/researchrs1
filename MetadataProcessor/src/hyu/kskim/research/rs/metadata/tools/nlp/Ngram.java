/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.rs.metadata.tools.nlp;

import java.util.*;

public class Ngram {
	int N; // Basic setting is Bigram
	
	public Ngram(int N){
		this.N = N;
	}
	
	public Ngram(){
		this.N = 2; // Basic setting is Bigram
	};

	//0. Main interface
	public ArrayList<String> getNgram(String s){
		return this.getN_gram(this.preprocessing(s));
	}
	
	//1. Preprocessing
	public String preprocessing(String s){
		if(s==null) return null;
		StringBuffer sb = new StringBuffer();
		
		sb.append(s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
		
		System.out.println("Preprocessed sentence: "+sb.toString());
		return sb.toString();
	}
	
	//2. Make n-gram
	public ArrayList<String> getN_gram(String s){
		if(s==null) return null;
		ArrayList<String> ngramList = new ArrayList<String>();
		
		int high = s.length()-N+1;
		for(int i=0; i < high; i++){
			//System.out.println(s.substring(i, i+N));
			ngramList.add(s.substring(i, i+N));
		}
		
		for(int i=0; i < ngramList.size(); i++) System.out.print(ngramList.get(i)+" --> ");
		return ngramList;
	}
	

	
	//3. N-gram similarity using Jaccard Method
	public double jaccard_sim(String word1, String word2){
		// 주어진 N-gram 리스트로부터 유사도 계산을 위한 집합을 생성한다. (사유: Jaccard similarity는 집합 기반 연산으로, 집합은 중복 허용 X)
		HashSet<String> ht1 = new HashSet<String>(); // Set of Word1's n-grams
		HashSet<String> ht2 = new HashSet<String>(); // Set of Word2's n-grams
		HashSet<String> union = new HashSet<String>();  // Set of Union of the n-grams
		HashSet<String> inter = new HashSet<String>(); // Set of Intersection of the n-grams
		
		ht1.addAll(this.getNgram(word1)); // Set of word1's n-grams --> A
		ht2.addAll(this.getNgram(word2)); // Set of word2's n-grams --> B
		
		// A U B
		union.addAll(ht1);
		union.addAll(ht2);
		
		// A intersection B
		Iterator<String> val1 = ht1.iterator();
		while(val1.hasNext()){ // A's value
			String v1 = val1.next();
			if(ht2.contains(v1)) inter.add(v1);
		}
		
		
		// Compute Jaccard similarity
		double sim = 0;
		
	   // double n1 = ht1.size();
	   // double n2 = ht2.size();
		double un = union.size();
		double it = inter.size();
		
		if(un==0) return -1;
		if(it==0) return 0;
		
		sim = it / un;
		
		return sim;
	}
}
