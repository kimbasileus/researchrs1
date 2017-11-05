package hyu.kskim.recomsys.prepare.utils;

import java.util.*;

public class RandomNumbers {
	public int[] getNRandomNumbers(int min, int max, int length) {
		int list[] = new int[length];
		
		Random r = new Random();
		int num = 0;
		int count = 0;
		boolean isDuplicate = false;
		
		while(count < length) {
			isDuplicate = false;
			num = r.nextInt(max-min+1)+min;
			//System.out.println("Selected number is "+num);
			
			for(int j=0; j < count; j++) {
				if(list[j]==num) {
					isDuplicate = true;
					continue;
				}
			}
			
			if(isDuplicate) continue;
			else {
				//System.out.println("\t-Final selected number is "+num);
				list[count] = num;
				count++;
			}
		}
		
		return list;
	}
	
	
	public HashSet<Integer> getNRandomNumbers_Set(int min, int max, int length){
		HashSet<Integer> set = new HashSet<Integer>();
		Random r = new Random();
		
		int num = 0;
		int count = 0;
		
		while(count < length) {
			num = r.nextInt(max-min+1)+min;
			System.out.println("Selected number is "+num);
			
			if(set.contains(num)) continue;
			set.add(num);
			count++;
			System.out.println("\t-Final selected number is "+num);
		}
		
		return set;
	}
	
	
	public HashSet<Integer> getNCummulatedRandomNumbers_Set(int min, int max, int length, HashSet<Integer> existedSet){
		HashSet<Integer> set = new HashSet<Integer>();
		Random r = new Random();
		
		if(existedSet != null) set.addAll(existedSet);
		int num = 0;
		int count = 0;
		
		while(count < length) {
			num = r.nextInt(max-min+1)+min;
			
			if(set.contains(num)) continue;
			set.add(num);
			count++;
		}
		
		return set;
	}
}
