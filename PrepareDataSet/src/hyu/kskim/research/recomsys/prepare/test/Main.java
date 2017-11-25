package hyu.kskim.research.recomsys.prepare.test;

import hyu.kskim.recomsys.prepare.dataset.EpinionsDataset;
import hyu.kskim.recomsys.prepare.dataset.MakeDBSchema;
import hyu.kskim.recomsys.prepare.dataset.MovieLensDataset;
import hyu.kskim.recomsys.prepare.utils.RandomNumbers;

public class Main {
	public static void main(String[] args) {
		/*
		MakeDBSchema makeDBSchema = new MakeDBSchema();
		makeDBSchema.makeSchema("Anyang");
		//makeDBSchema.deleteSchema("Anyang");
		makeDBSchema.endMakeDBSchema();
		*/
		
		
		// 생성자
		/*
		MovieLensDataset mld = new MovieLensDataset(
				"movielens", 
				"D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small",
				671, 9125, 100004);
		*/
		// mld.loadInitialDataset_into_DB(); // 무비렌즈 초기 text 데이터로부터 DB로 읽어들이는 함수
		
		/*
		mld.makeTestDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", "movielens", 0.2); // 0.2, 0.4, 0.6, 0.8
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.2, 0.3);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.3, 0.4);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.4, 0.5);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.5, 0.6);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.6, 0.7);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.7, 0.8);
		*/
		
		/*
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 20, 30);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 30, 40);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 40, 50);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 50, 60);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 60, 70);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 70, 80);
		*/
		
		//mld.loadTrain_Test_Dataset_Into_DB("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", "movielens", 0.2);
		
		
		//mld.load_userAverage_Into_DB("movielens");
		
		boolean isBasedItem = false;
	//	mld.verify_itemIDs("movielens", 0, isBasedItem);
	//	mld.verify_itemIDs("movielens", 1, isBasedItem);
	//	mld.verify_itemIDs("movielens", 2, isBasedItem);
		
	//	mld.endMovieLensDataset(); // 소멸자
		
		EpinionsDataset ed = new EpinionsDataset("epinionscom", 91735, 26527, 170452);
		//ed.verify_itemIDs("epinionsserver", 2, true);
		//ed.make_users_table();
		//ed.make_usertrust_table();
		//ed.make_items_table();
		//ed.make_userRating_table();
		//ed.make_itemnetwork_table();
		
		
		// 2. 누적 TestSet 만들기
		//"D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", "epinionscom", 0.2);
		//ed.makeTestDataSet("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", "epinionscom", 0.2);
		//ed.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 0.2, 0.3);
		//ed.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 0.3, 0.4);
		//ed.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 0.4, 0.5);
		//ed.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 0.5, 0.6);
		//ed.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 0.6, 0.7);
		//ed.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 0.7, 0.8);
		
		//ed.verify("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 20, 30);
		//ed.verify("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 30, 40);
		//ed.verify("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 40, 50);
		//ed.verify("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 50, 60);
		//ed.verify("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 60, 70);
		//ed.verify("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", 70, 80);
		
		//ed.loadTrain_Test_Dataset_Into_DB("D:\\Research_LibraryDataSet\\Dataset\\Epinions\\testsets\\", "epinionscom", 0.2);
	
		ed.load_userAverage_Into_DB("epinionscom");
	}
}
