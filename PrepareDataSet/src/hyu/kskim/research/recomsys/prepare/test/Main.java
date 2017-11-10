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
		
		mld.loadInitialDataset_into_DB(); // ������ �ʱ� text �����ͷκ��� DB�� �о���̴� �Լ�
		
		//mld.makeTestDataSet(0.2); // 0.2, 0.4, 0.6, 0.8
		//mld.makeCumulatedDataSet(0.6, 0.8);
		//mld.verify(60, 80);
		
		mld.endMovieLensDataset(); // �Ҹ���
		
	}
}
