package hyu.kskim.research.recomsys.prepare.test;

import hyu.kskim.recomsys.prepare.dataset.MovieLensDataset;
import hyu.kskim.recomsys.prepare.utils.RandomNumbers;

public class Main {
	public static void main(String[] args) {
		MovieLensDataset mld = new MovieLensDataset(671, 9125, 100004);
		//mld.run();
		//mld.makeTestDataSet(0.2); // 0.2, 0.4, 0.6, 0.8
		//mld.makeCumulatedDataSet(0.6, 0.8);
		mld.verify(60, 80);
		
		
	}
}
