package com.scpd.cs246.hw1;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

	public class ListSizeComparator implements Comparator<Integer> {

		private final Map<Integer, List<Integer>> map;

		public ListSizeComparator(final Map<Integer, List<Integer>> map) {
			this.map = map;
		}

		public int compare(Integer o1, Integer o2) {
				//Here I assume both keys exist in the map.
				List<Integer> list1 = this.map.get(o1);
				List<Integer> list2 = this.map.get(o2);
				Integer length1 = list1.size();
				Integer length2 = list2.size();
				return length1.compareTo(length2);
			
		}

}
