package hyu.kskim.recomsys.prepare.utils;

import java.util.*;

public class RandomNumbers {
	public int[] getNRandomNumbers(int min, int max, int length, boolean isAllowDuplicate) {
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
			
			if(!isAllowDuplicate && isDuplicate) continue;
			else {
				//System.out.println("\t-Final selected number is "+num);
				list[count] = num;
				count++;
			}
		}
		
		return list;
	}
	
	
	
	public int[] getNRandomNumbers_Set_AllowDuplicate(int min, int max, int length){
		Random r = new Random();
		int[] list = new int[length];
		
		for(int i=0; i<length; i++)	list[i] = r.nextInt(max-min+1)+min;
		
		return list;
	}
	
	
	// max~min 범위의 임의의 랜덤 값들을 추출하여 HashSet 타입으로 반환한다. (중복 X)
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
		
		// 기존에 추출된 랜덤 집합을 추가한다.
		if(existedSet != null) set.addAll(existedSet);
		int num = 0;
		int count = 0;
		
		while(count < length) {
			num = r.nextInt(max-min+1)+min;
			
			if(set.contains(num)) continue;
			set.add(num);
			count++;
		}
		
		// 기존에 추출된 랜덤 집합과 새로 추출된 값들을 합하여 반환한다.
		return set;
	}
}
