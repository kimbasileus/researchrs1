package hyu.kskim.research.rs.preexp.ds;

/*
 * IST 분산 캐싱 시 Adjusted Cosine 기법을 활용할 때의 자료 구조이다.
 */
public class ISTPair_ADC {
	public int item1ID;
	public int item2ID;
	public double A;
	public double B;
	public double C;
	public double D;
	public double E;
	public double F;
	public int G;
	public double sim;
	
	public ISTPair_ADC(int item1ID, int item2ID, double a, double b, double c, double d, double e, double f, int g, double sim) {
		super();
		this.item1ID = item1ID;
		this.item2ID = item2ID;
		A = a;
		B = b;
		C = c;
		D = d;
		E = e;
		F = f;
		G = g;
		this.sim = sim;
	}
}
