package hyu.kskim.research.recomsys.utility;

import java.util.Random;

import hyu.kskim.research.recomsys.utility.ds.ISTPair_CosineSim;

public class Main_Utility {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random r = new Random();
		int size = 10000000;
		double x[] = new double[size];
		double y[] = new double[size];
		
		for(int i=0; i < x.length; i++) {
			x[i] = r.nextInt(7)-1;
			y[i] = r.nextInt(7)-1;
		}
		
		double sim = 0; double sim2 = 0;
		SimMeasures measure = new SimMeasures();
		sim = measure.cosineSim(x, y);
		sim2 = measure.cosineSim(measure.transformToIndexPairArray(x), measure.transformToIndexPairArray(y));
		System.out.println("Success. sim using method1:"+sim+ "\t sim using method2: "+sim2);
	}

}
