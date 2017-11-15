package hyu.kskim.research.rs.test;

import hyu.kskim.research.rs.preexp.ist.MakeInitialIST;

public class Main {
	public static void main(String args[]) {
		MakeInitialIST ist = new MakeInitialIST("movielens", 671, 9125, 0.3);
		ist.run();
	}
}
