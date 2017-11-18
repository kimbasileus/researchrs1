package hyu.kskim.research.rs.test;

import java.util.Scanner;

import hyu.kskim.research.rs.preexp.ds.UserRatingMatrix_hashTable;
import hyu.kskim.research.rs.preexp.ist.MakeInitialIST;

public class Main {
	public static void main(String args[]) {
		/*
		MakeInitialIST ist = new MakeInitialIST("movielens", 671, 9125, 0.3);
		ist.run(20);
		*/
		
		
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
		
	}
}
