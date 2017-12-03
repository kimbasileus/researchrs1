package hyu.kskim.research.tools.ietool.nlp;

import hyu.kskim.research.tools.db.FileIO;
import hyu.kskim.research.tools.ietool.indexer.InvertedIndexer;

public class Test {
	static Parser parser = new Parser();
	
	public static void main(String args[]){
		//parser.tokenization("Incheon is very beautiful city in Korea.");
		//InvertedIndexer index  = new InvertedIndexer();
		DocumentAnalysis docAnalysis = new DocumentAnalysis();
		FileIO file = new FileIO();
		String doc = file.reader_as_String(".\\doc.txt");
		String doc2 = file.reader_as_String(".\\doc2.txt");
		docAnalysis.getPreprocessedWordSet(1, doc);
		System.out.println( docAnalysis.index.getNumOfTotalDocs("approxim") );
		System.out.println( docAnalysis.index.getNumOfTotalFreq("field"));
		System.out.println( docAnalysis.index.getNumOfWordFreqInDoc(1, "plan"));
		
		docAnalysis.getPreprocessedWordSet(2, doc2);
		System.out.println( docAnalysis.index.getNumOfTotalDocs("republ") );
		System.out.println( docAnalysis.index.getNumOfTotalFreq("republ"));
		System.out.println( docAnalysis.index.getNumOfWordFreqInDoc(2, "republ"));
	}
}
