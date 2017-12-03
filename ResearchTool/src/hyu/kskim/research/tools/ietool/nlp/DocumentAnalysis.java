package hyu.kskim.research.tools.ietool.nlp;

import java.util.*;

import hyu.kskim.research.tools.ietool.dic.WordNet_JWI;
import hyu.kskim.research.tools.ietool.indexer.InvertedIndexer;

public class DocumentAnalysis {
	WordNet_JWI wordNet = null;
	Parser parser = null;
	StopWordEliminator stopWord = null;
	InvertedIndexer index = null;
	PorterStemmer stemmer = null;
	Dictionary dic = null;
	
	// 통계 정보 변수
	double averageFreqOfWords = 0;
	Hashtable<Integer, Double>	averageFreqInDoc = null; // 각 문서별로 평균 단어 빈도수
	Hashtable<String, Double> varianceOfEachWord = null; // 각 단어별 빈도수에 대한 분산 값
	
	// 생성자
	public DocumentAnalysis() {
		this.wordNet = new WordNet_JWI("C:\\WordNet-3.0\\");
		this.parser = new Parser();
		this.stopWord = new StopWordEliminator(""); // 경로 수정 필요
		this.index = new InvertedIndexer();
		this.stemmer = new PorterStemmer();
		this.dic = new Dictionary();
		
		this.averageFreqInDoc = new Hashtable<Integer, Double>();
		this.varianceOfEachWord = new Hashtable<String, Double>();
	}
	
	
	// 주어진 문서로부터 모든 단어들을 추출하고, 이를 단어 집합 (단어, 빈도수) 타입으로 반환하는 함수
	public Hashtable<String, Integer> getPreprocessedWordSet(int docID, String doc) {
		try {
			Hashtable<String, Integer> set = new Hashtable<String, Integer>();
			
			String[] sentences = doc.split("\n"); // The document is splited into sentences...
			if(sentences == null) return null;
			if(sentences.length == 0) return null;
			
			ArrayList<String> tokens = null;
			for(int i=0 ; i < sentences.length; i++) { // For each sentence
				tokens = this.parser.tokenization(sentences[i]);
				
				if(tokens==null) continue;
				if(tokens.size()==0) continue;
				
				for(int j=0; j < tokens.size(); j++) { // For each word in the sentence
					
					if(this.stopWord.isStopWord(tokens.get(j))) continue;
					
					String stemmedWord = this.stemmer.stemming_word( tokens.get(j) );
					
					int freq = 0;
					if(set.containsKey(stemmedWord)) { // 해당 단어가 이미 집합에 존재하는 경우
						freq = set.get(stemmedWord);
						set.put(stemmedWord, ++freq);
					}else { // 해당 단어가 집합에 없는 경우
						freq = 1;
						set.put(stemmedWord, freq);
					}
					
					this.dic.addWord(stemmedWord); // Dictionary
					this.index.addWord(stemmedWord, docID, freq); // Inverted Index file
				}
			}
			
			System.out.println("\n\n추출된 총 단어수: "+set.size());
			if(set.size()==0) return null;
			else return set;
		}catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("getPreprocessedWordSet Exception: "+e.getMessage()+ " // "+e.getLocalizedMessage());
			return null;
		}
	}
	
	
	public boolean computeStatistic() {
		try {
			
			
			return true;
		}catch(Exception e) {
			System.err.println("computeStatistic Exception: "+e.getMessage()+ " // "+e.getLocalizedMessage());
			return false;
		}
	}
	
	
	public int getTermFrequency(String word) {
		return this.index.getNumOfTotalFreq(word);
	}
	
	
	public int getDocumentFrequency(String word) {
		return this.index.getNumOfTotalDocs(word);
	}
	
	public int getTemFreqInDoc(int docID, String word) {
		return this.index.getNumOfWordFreqInDoc(docID, word);
	}
	
	
	
}



class Dictionary{
	public int numOfWords;
	HashSet<String> dictionary = new HashSet<String>();
	
	public void addWord(String word) {
		if(!dictionary.contains(word)) {
			this.dictionary.add(word);
			numOfWords++;
		}
	}
	
	public boolean remove(String word) {
		if(dictionary.contains(word)) {
			numOfWords--;
			return this.dictionary.remove(word);
		}else
			return false;
	}
}