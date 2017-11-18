package hyu.kskim.research.rs.preexp.ds;

/*
 * 희소 행렬을 생성하는데 필요한 기본 인터페이스를 정의한다.
 */
public interface SparseMatrix {
	public void putRating(int i, int j, double value);
	public double getRating(int i, int j);
	public boolean removeRating(int i, int j);
	public boolean isEmpty();
	public int getNumOfRatings();
}
