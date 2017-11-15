package hyu.kskim.research.rs.preexp.ds;

/*
 * 좌표, 행렬의 행/열 인덱스 등을 표시할 때 사용하며, 주로 Hashtable의 Key value로 활용된다.
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