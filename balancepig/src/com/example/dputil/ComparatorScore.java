package com.example.dputil;

import java.util.Comparator;

/**
 * ∞¥scoreΩµ–Ú≈≈¡–
 * 
 */
public class ComparatorScore implements Comparator<Object> {

	@Override
	public int compare(Object arg0, Object arg1) {
		Score s1 = (Score) arg0;
		Score s2 = (Score) arg1;
		if (s1.getScore() < s2.getScore()) {
			return 1;
		}
		return -1;
	}

}
