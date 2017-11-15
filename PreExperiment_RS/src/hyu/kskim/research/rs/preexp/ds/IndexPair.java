package hyu.kskim.research.rs.preexp.ds;

/*
 * ��ǥ, ����� ��/�� �ε��� ���� ǥ���� �� ����ϸ�, �ַ� Hashtable�� Key value�� Ȱ��ȴ�.
 */
public class IndexPair{
	public int row;
	public int col;
	
	public IndexPair(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}
	
	@Override
	public int hashCode() {
		return (Integer.toString(row)+","+Integer.toString(col)).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(  ( ((IndexPair)obj).col == this.col ) && ( ((IndexPair)obj).row == this.row ) ) return true;
		else return false;
	}

	/*
	@Override
	public int compareTo(Object o) {
		int r = ((IndexPair)o).row;
		int c = ((IndexPair)o).col;
		
		int result = 0;
		if(this.row > r ) result = 1;
		else if( (this.row == r) && (this.col > c) ) result =  1;
		else if( (this.row == r) && (this.col < c) ) result =  -1;
		else if(this.row < r) result =  0;

		return result;
	}
	*/
}