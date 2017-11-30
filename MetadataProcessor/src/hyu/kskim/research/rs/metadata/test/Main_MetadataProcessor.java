package hyu.kskim.research.rs.metadata.test;
import java.io.IOException;

import hyu.kskim.research.rs.metadata.collector.*;

public class Main_MetadataProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\contentmetadata";
		
		SearchAPI api = new SearchAPI();
		try {
			api.apitest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ContentMetadataCrawler crawler = new ContentMetadataCrawler("movielens", 9175);
		//crawler.runExtractWebLinks();
		
		// Gets the text of the main page and prints it.
		
	}

}
