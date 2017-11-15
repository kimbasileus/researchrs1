package hyu.kskim.research.recomsys.prepare.test;

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
		
		
		// ������
		MovieLensDataset mld = new MovieLensDataset(
				"movielens", 
				"D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small",
				671, 9125, 100004);
		
		//mld.loadInitialDataset_into_DB(); // ������ �ʱ� text �����ͷκ��� DB�� �о���̴� �Լ�
		
		/*
		mld.makeTestDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", "movielens", 0.3); // 0.2, 0.4, 0.6, 0.8
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.3, 0.4);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.4, 0.5);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.5, 0.6);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.6, 0.7);
		mld.makeCumulatedDataSet("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 0.7, 0.8);
		*/
		
		/*
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 30, 40);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 40, 50);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 50, 60);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 60, 70);
		mld.verify("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", 70, 80);
		*/
		
		//mld.loadTrain_Test_Dataset_Into_DB("D:\\Research_LibraryDataSet\\Dataset\\MovieLens_small\\testsets\\", "movielens", 0.3);
		
		
		//mld.load_userAverage_Into_DB("movielens");
		mld.endMovieLensDataset(); // �Ҹ���
		
	}
}
