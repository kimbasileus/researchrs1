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
public class StatisticTool {
	public static final int inf = -999;
	
	public double average(double[] x){
		if(x==null) return inf;
		int l = x.length;
		
		if(l==0) return inf;
		
		double sum = 0;
		int n = 0;
		for(int i=0; i < l ; i++){
			if(x[i]==inf) continue;
			
			sum += x[i];
			n++;
		}
		
		if(n==0) return inf;
		
		return sum/n;
	}
	
}
