package hyu.kskim.research.rs.metadata.test;
import java.io.*;

import hyu.kskim.research.rs.metadata.collector.*;
import hyu.kskim.research.rs.utils.FileIO;

public class Main_MetadataProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\contentmetadata";
		
		
		/*
		// 콘텐츠 메타데이터 문서 수집 코드
		MetadataCollector_MovieLens crawlerM = new MetadataCollector_MovieLens("movielens", dir, 9125);
		//crawlerM.runExtractWikiDocs(); //초기 수집
		//crawlerM.processUnCollectedItems(2); // 초기 수집 후 재시행 (1-->2: trial 1~2 from Wiki)
		crawlerM.runExtractIMDBDocs(); // 재시행 실패한 아이템에 대한 imdb 데이터 수집 (trial 3 from IMDB)
		//crawlerM.crawler.getWebPage_for_IMDB_CastingList("http://www.imdb.com/title/tt50105/fullcredits"); // For debugging
		*/
		
		// 수집된 콘텐츠 메타데이터 문서들에 대한 정제 및 DB 캐싱 코드
		//MetadataPreprocessor pp = new MetadataPreprocessor("movielens", dir, 9125); // 1. Wiki-based, 2: IMDB-based
		//pp.getFeatureElementList(); // for debug
		
		// 수집된 문서들로부터 단어를 추출하여 사전을 구성하고, 이를 DB에 캐싱하는 코드
		WordDictionary dic = new WordDictionary("movielens", dir, 9125);
	}

}
