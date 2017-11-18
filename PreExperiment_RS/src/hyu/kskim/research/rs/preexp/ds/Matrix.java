package hyu.kskim.research.rs.preexp.ds;

import java.util.Hashtable;

/*
 * User-Item Rating Matrix, Matrix Factorization 시 반환되는 P, Q 행렬들에 대한 자료구조이다.
 */

public class Matrix implements SparseMatrix{
	public Hashtable<IndexPair, Double> list  = null;
	int numOfRows=0;
	int numOfColumns=0;
	int numOfElements = 0;
	
	public Matrix(int numOfRows, int numOfColumns) {
		this.list = new Hashtable<IndexPair, Double>();
		this.numOfRows = numOfRows;
		this.numOfColumns = numOfColumns;
	}	
	
	@Override
	public void putRating(int i, int j, double value) {
		// TODO Auto-generated method stub
		this.list.put(new IndexPair(i, j), value);
	}

	@Override
	public double getRating(int i, int j) {
		// TODO Auto-generated method stub
		IndexPair key = new IndexPair(i, j);
		if(this.list.containsKey(key)) return this.list.get(key);
		else return -1;
	}

	@Override
	public boolean removeRating(int i, int j) {
		// TODO Auto-generated method stub
		IndexPair key = new IndexPair(i, j);
		if(this.list.containsKey(key)) {
			this.list.remove(key);
			return true;
		}else return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		if(this.numOfElements==0) return true;
		else return false;
	}

	@Override
	public int getNumOfRatings() {
		// TODO Auto-generated method stub
		return this.numOfElements;
	}
	
	public void init(double v) {
		int r = numOfRows;
		int c = numOfColumns;
		
		for(int i=0; i < r; i++) {
			for(int j=0; j < c; j++) {
				this.putRating(i, j, v);
			}
		}
	}
}
