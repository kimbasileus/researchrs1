package hyu.kskim.research.rs.preexp.alg.mf;

import hyu.kskim.research.rs.preexp.ds.IndexPair;
import hyu.kskim.research.rs.preexp.ds.Matrix;
import hyu.kskim.research.rs.preexp.ds.UserRatingMatrix_hashTable;

public class MatrixFactorization {
	UserRatingMatrix_hashTable R = null;
	Matrix P = null;
	Matrix Q = null;
	double alpha = 0.1;

	int numOfUsers = 10;
	int numOfItems = 8; 
	int rank = 2; // Rank of R
	
	public MatrixFactorization(String dbSchema, int numOfUsers, int numOfItems, int rank) {
		R = new UserRatingMatrix_hashTable(dbSchema, numOfUsers, numOfItems);
		this.numOfUsers = numOfUsers;
		this.numOfItems = numOfItems;
		this.rank = rank;
		
		this.P = new Matrix(numOfUsers, rank);
		this.Q = new Matrix(numOfItems, rank);
	}
	
	public void init() {
		this.P.init(0.1);
		this.Q.init(0.1);
	}
	
	public void runMF(int maxIteration) {
		
	}
	
	public void SGD() { // 실제 Matrix Factorization 구현 부분
		
		
	}
	
	public boolean isConverge(double oldValue, double newValue, double epsilon) {
		if  (  Math.abs(newValue-oldValue) < epsilon ) return true;
		else return false;
	}
	
	public void updateValue(Matrix M, int row, int col, double value) {
		M.list.put(new IndexPair(row, col), value);
	}
}
