/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.tools.mathtool;

/**
 *
 * @author KyungSoo Kim
 */
public class VectorTool {
	public final int inf = -999;
	
	// 1. Inner Product, Outer Product
	public double innerProduct(double x[], double y[]){
		if(x==null || y==null) return inf;
		double innerP = 0;
		int l = x.length;
		
		double sum = 0;
		for(int i=0; i < l ; i++){
			if(x[i]==inf || y[i]==inf) continue;
			sum += x[i]*y[i];
		}
		
		innerP = Math.sqrt(sum);
		
		return innerP;
	}

	
	// 2. Norm, Length, Distance
	public double n_norm(double x[], int n){
		if(x==null || n==0) return inf;
		double norm = 0;
		
		return norm;
	}
	
	public double length(double x[]){
		double length = 0;
		
		return length;
	}

	
	public double EuclidianDistance(double x[], double y[]){
		double dis = 0;
		
		return dis;
	}
	
	public double CityBlockDistance(double x[], double y[]){
		double dis = 0;
		
		return dis;
	}
		
	
	// 3. Orthogonality
	public double cosineAngle(double x[], double y[]){
		double angle = 0;
		
		return angle;
	}
	
	public boolean isOrthogonal(double x[], double y[]){
		boolean perpendicular = false;
		
		return perpendicular;
	}
	
	public boolean isOrthonormal(double x[], double y[]){
		boolean perpendicular = false;
		
		return perpendicular;
	}
	

	
	// 4. Vector Similarity
	public double cosineSim(double x[], double y[]){
		double sim = inf;
		
		int length = x.length;
		
		double sumX = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumXq = 0;
		double sumYq = 0;
		double sqrt_sumXq = 0;
		double sqrt_sumYq = 0;
		
		int n = 0;
		
		for(int i=0; i < length ; i++){
			if(x[i]==inf || y[i]==inf) continue;
			
			n++;
			sumXq += (x[i]*x[i]);
			sumYq += (y[i]*y[i]);
			sumXY += (x[i]*y[i]);
		}
		
		if(sumXq <= 0 || sumYq <=0) return inf;
		
		sqrt_sumXq = Math.sqrt(sumXq);
		sqrt_sumYq = Math.sqrt(sumYq);
		sim = sumXY / ( sqrt_sumXq * sqrt_sumYq );
		if(sim > 1) sim = 1;
		
		return sim;
	}
	
	
	public double PCC(double x[], double y[]){
		double sim = inf;
		int length = x.length;
		
		double sumX = 0;
		double sumY = 0;
		double sumXY = 0;
		double sumXq = 0;
		double sumYq = 0;
		
		int n = 0;
		
		for(int i=0; i < length; i++){
			if(x[i]==inf || y[i]==inf) continue;
			
			sumX += x[i];
			sumY += y[i];
			sumXY += (x[i]*y[i]);
			sumXq += (x[i]*x[i]);
			sumYq += (y[i]*y[i]);
			n++;
		}
		
		double denominatorX = 0;
		double denominatorY = 0;
		double nominator = 0;
		
		denominatorX = Math.sqrt( n*sumXq-(sumX*sumX) );
		denominatorY = Math.sqrt( n*sumYq-(sumY*sumY) );
		
		if(denominatorX <=0 || denominatorY <=0) return inf;
		
		nominator = (n*sumXY) - (sumX*sumY);
		
		sim = nominator/(denominatorX*denominatorY);
		// if(sim>1) return 1;
		
		return sim;
	}
	
	public double correlation(double x[], double y[]){
		double rel = 0;
		
		return rel;
	}
	

}
