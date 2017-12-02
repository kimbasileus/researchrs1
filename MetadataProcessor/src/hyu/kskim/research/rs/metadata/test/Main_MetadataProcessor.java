package hyu.kskim.research.rs.metadata.test;
import java.io.*;

import hyu.kskim.research.rs.metadata.collector.*;
import hyu.kskim.research.rs.utils.FileIO;

public class Main_MetadataProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\contentmetadata";
		
		
		MetadataCollector_MovieLens crawlerM = new MetadataCollector_MovieLens("movielens", dir, 9125);
		//crawlerM.runExtractWikiDocs();
		//crawlerM.processUnCollectedItems(2); // trial 1~2 from Wiki
		crawlerM.runExtractIMDBDocs(); // trial 3 from IMDB
		//crawlerM.crawler.getWebPage_for_IMDB_CastingList("http://www.imdb.com/title/tt50105/fullcredits");
		
	}

}
