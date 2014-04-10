package edu.sapienza.informatica.websocial.homework2;

import java.util.Comparator;
import java.util.Map;

public class MyComparator<T> implements Comparator<T> {
	
	private final Map<T,Integer> FMAP;
	
	public MyComparator(Map<T,Integer> map){
		FMAP = map;
	}
	
	public int compare(T o1, T o2) {
		int i1 = FMAP.get(o1);
		int i2 = FMAP.get(o2);
		if(i1>i2) return 1;
		else if(i1<i2) return -1;
		else return 0;
	}

}
