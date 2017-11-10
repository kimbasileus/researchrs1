package hyu.kskim.research.recomsys.math.similarity;

public class Pair implements Comparable<Pair>{
	public int x;
	public int y;
	
	public Pair(int x, int y){
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Pair arg0) {
		int isEqual = 0;
		
		if( (this.x == arg0.x) && (this.y == arg0.y) ) isEqual = 1;
		
		return isEqual;
	}
}
