package hyu.kskim.research.rs.metadata.test;
import java.io.*;

import hyu.kskim.research.rs.metadata.collector.*;
import hyu.kskim.research.rs.utils.FileIO;

public class Main_MetadataProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\contentmetadata";
		
		
		/*
		// ������ ��Ÿ������ ���� ���� �ڵ�
		MetadataCollector_MovieLens crawlerM = new MetadataCollector_MovieLens("movielens", dir, 9125);
		//crawlerM.runExtractWikiDocs(); //�ʱ� ����
		//crawlerM.processUnCollectedItems(2); // �ʱ� ���� �� ����� (1-->2: trial 1~2 from Wiki)
		crawlerM.runExtractIMDBDocs(); // ����� ������ �����ۿ� ���� imdb ������ ���� (trial 3 from IMDB)
		//crawlerM.crawler.getWebPage_for_IMDB_CastingList("http://www.imdb.com/title/tt50105/fullcredits"); // For debugging
		*/
		
		// ������ ������ ��Ÿ������ �����鿡 ���� ���� �� DB ĳ�� �ڵ�
		//MetadataPreprocessor pp = new MetadataPreprocessor("movielens", dir, 9125); // 1. Wiki-based, 2: IMDB-based
		//pp.getFeatureElementList(); // for debug
		
		// ������ ������κ��� �ܾ �����Ͽ� ������ �����ϰ�, �̸� DB�� ĳ���ϴ� �ڵ�
		WordDictionary dic = new WordDictionary("movielens", dir, 9125);
	}

}
