package hyu.kskim.research.rs.test;

import java.util.Scanner;

import hyu.kskim.research.rs.preexp.alg.trad.ItemBasedCF;
import hyu.kskim.research.rs.preexp.ds.UserRatingMatrix_hashTable;
import hyu.kskim.research.rs.preexp.experiment.ExperimentInterface;
import hyu.kskim.research.rs.preexp.ist.MakeInitialIST;

public class Main {
	public static void main(String args[]) {
		/*
		MakeInitialIST ist = new MakeInitialIST("movielens", 671, 9125, -1);
		ist.run(20);
		*/
		
		/*
		UserRatingMatrix_hashTable urm = new UserRatingMatrix_hashTable("movielens", 671, 9125);
		urm.loadUserRatings(true);
		
		Scanner scan = new Scanner(System.in);
		int userID; int itemID;
		while(true) {
			userID = Integer.parseInt(scan.nextLine());
			itemID = Integer.parseInt(scan.nextLine());
			
			if(userID == -1 || itemID == -1) break;
			
			System.out.println("\t- Rating is: "+urm.getRating(userID, itemID));
		}
		*/
		/*
		ItemBasedCF ibc = new ItemBasedCF("movielens", 671, 9125, 30);
		ibc.initRecommenderEngine();
		System.out.println( ibc.getRating(584, 2063) );
		ibc.closeItemBasedCF();
		*/
		
		ExperimentInterface ei = new ExperimentInterface(671, 9125, 0.2);
		ei.initializeDB("movielens", "root", "kyungsookim", "", 101);
		ei.loadTestSets();
		ei.run();
		
	}
}
