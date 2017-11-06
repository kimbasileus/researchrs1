package hyu.kskim.research.recomsys.utility;

import java.util.ArrayList;

import hyu.kskim.research.recomsys.ist.ISTPair_CosineSim;
import hyu.kskim.research.recomsys.utility.ds.IndexPair;

public class SimMeasures {
	public static final int inf = -1;

	///////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Cosine Similarity: Traditional Vector Similarity
	 * @param x vector x (double type)
	 * @param y vector y (double type)
	 * @return similarity (double type)
	 */
	public double cosineSim(double x[], double y[]) {
		try {
			if(x==null || y==null) return inf;
			
			int lenX = x.length;
			int lenY = y.length;
			
			if(lenX==0 || lenY ==0) return inf;
			
			//int n=0;
			double sumXY = 0; double sumXq = 0; double sumYq = 0;
			
			long start = System.currentTimeMillis();
			
			int count = 0;
			for(int i=0; i < lenX; i++)
			{
				
				if(x[i]==inf || y[i]==inf) continue;
				// count++;
				sumXY += x[i]*y[i];
				sumXq += x[i]*x[i];
				sumYq += y[i]*y[i];
				//n++;
			}
			
			long end = System.currentTimeMillis();
			
			double sim = 0;
			
			if(sumXq <=0 || sumYq <=0) return inf;
			sim = sumXY/(Math.sqrt(sumXq)*Math.sqrt(sumYq));
			
			System.out.println("cosineSim1 running time: "+ (end-start)/1000.0 +" count is: "+count );
			
			return sim;
		}catch(Exception e) {
			System.out.println("cosineSim1 Exception: "+e.getMessage());
			return inf;
		}
	}
		
	
	/**
	 * Cosine Similarity: Traditional Vector Similarity
	 * @param x vector x (double type)
	 * @param y vector y (double type)
	 * @return IST similarity object (ISTPair_CosineSim type)
	 */	
	public ISTPair_CosineSim cosineSim_ISTPair(double x[], double y[]) {
		try {
			if(x==null || y==null) return null;
			
			int lenX = x.length;
			int lenY = y.length;
			
			if(lenX==0 || lenY ==0) return null;
			
			//int n=0;
			double sumXY = 0; double sumXq = 0; double sumYq = 0;
			
			long start = System.currentTimeMillis();
			
			int count = 0;
			for(int i=0; i < lenX; i++)
			{
				//count++;
				if(x[i]==inf || y[i]==inf) continue;
				
				sumXY += x[i]*y[i];
				sumXq += x[i]*x[i];
				sumYq += y[i]*y[i];
				//n++;
			}
			
			long end = System.currentTimeMillis();
			
			double sim = 0;
			
			if(sumXq <=0 || sumYq <=0) return null;
			sim = sumXY/(Math.sqrt(sumXq)*Math.sqrt(sumYq));
			
			System.out.println("cosineSim_ISTPair running time: "+ (end-start)/1000.0 +" count is: "+count );
			
			ISTPair_CosineSim result = new ISTPair_CosineSim(sumXY, sumXq, sumYq, sim);
			
			return result;
		}catch(Exception e) {
			System.out.println("cosineSim_ISTPair Exception: "+e.getMessage());
			return null;
		}
	}
		
	///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Cosine Similarity: Improved Vector Similarity
	 * @param x vector x (Index-value Pair List type)
	 * @param y vector y (Index-value Pair List  type)
	 * @return similarity (double type)
	 */
	public double cosineSim(IndexPair[] x, IndexPair[] y) {
		try {
			if(x==null || y==null) return inf;
			
			int lenX = x.length;
			int lenY = y.length;
			
			if(lenX==0 || lenY ==0) return inf;
			
			//int n=0;
			double sumXY = 0; double sumXq = 0; double sumYq = 0;
			
			int i=0; int j=0;
			
			long start = System.currentTimeMillis();
			int count=0;
			while(i < lenX && j < lenY) {
				if(x[i].index < y[j].index) {
					//count++; 
					i++; continue;
				}else if (x[i].index > y[j].index) {
					//count++; 
					j++; continue;
				}else{
					//count++;
					sumXY += x[i].value*y[j].value;
					sumXq += x[i].value*x[i].value;
					sumYq += y[j].value*y[j].value;
					//n++;
					
					i++; j++;
				}
			}
			
			long end = System.currentTimeMillis();
			
			double sim = 0;
			if(sumXq <=0 || sumYq <=0) return inf;
			
			sim = sumXY/(Math.sqrt(sumXq)*Math.sqrt(sumYq));
			
			System.out.println("cosineSim2 running time: "+ (end-start)/1000.0 +" count is: "+count);
			
			return sim;
		}catch(Exception e) {
			System.out.println("cosineSim2 Exception: "+e.getMessage());
			return inf;
		}
	}
		
	
	/**
	 * Cosine Similarity: Improved Vector Similarity
	 * @param x vector x (Index-value Pair List type)
	 * @param y vector y (Index-value Pair List  type)
	 * @return IST similarity object (ISTPair_CosineSim type)
	 */
	public ISTPair_CosineSim cosineSim_ISTPair(IndexPair[] x, IndexPair[] y) {
		try {
			if(x==null || y==null) return null;
			
			int lenX = x.length;
			int lenY = y.length;
			
			if(lenX==0 || lenY ==0) return null;
			
			//int n=0;
			double sumXY = 0; double sumXq = 0; double sumYq = 0;
			
			int i=0; int j=0;
			
			long start = System.currentTimeMillis();
			int count=0;
			while(i < lenX && j < lenY) {
				if(x[i].index < y[j].index) {
					//count++; 
					i++; continue;
				}else if (x[i].index > y[j].index) {
					//count++; 
					j++; continue;
				}else{
					//count++;
					sumXY += x[i].value*y[j].value;
					sumXq += x[i].value*x[i].value;
					sumYq += y[j].value*y[j].value;
					//n++;
					
					i++; j++;
				}
			}
			
			long end = System.currentTimeMillis();
			
			double sim = 0;
			if(sumXq <=0 || sumYq <=0) return null;
			
			sim = sumXY/(Math.sqrt(sumXq)*Math.sqrt(sumYq));
			
			System.out.println("cosineSim2 running time: "+ (end-start)/1000.0 +" count is: "+count);
			
			ISTPair_CosineSim result = new ISTPair_CosineSim(sumXY, sumXq, sumYq, sim);
			
			return result;
		}catch(Exception e) {
			System.out.println("cosineSim2 Exception: "+e.getMessage());
			return null;
		}
	}
		
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	
	public IndexPair[] transformToIndexPairArray(double x[]){
		int length = 0;
		int n = x.length;
		for(int i=0; i < n; i++) {
			if(x[i]==inf) continue;
			length++;
		}
		IndexPair[] list = new IndexPair[length];
		
		int j =0;
		for(int i=0; i < n; i++) {
			if(x[i]==inf) continue;
			list[j] = new IndexPair(i, x[i]);
			j++;
		}
		
		return list;
	}
}
