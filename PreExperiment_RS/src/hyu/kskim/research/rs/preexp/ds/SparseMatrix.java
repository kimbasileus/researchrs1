package hyu.kskim.research.rs.preexp.ds;

/*
 * ��� ����� �����ϴµ� �ʿ��� �⺻ �������̽��� �����Ѵ�.
 */
public interface SparseMatrix {
	public void putRating(int i, int j, double value);
	public double getRating(int i, int j);
	public boolean removeRating(int i, int j);
	public boolean isEmpty();
	public int getNumOfRatings();
}
