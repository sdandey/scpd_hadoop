package com.scpd.cs246.hw1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class FindFrequentItems {

	
	public static void main(String[] args) throws IOException {
		
		List<String> baskets = FileUtils.readLines(new File(args[0]));
		HashMap<String,Integer> mapSupportedItems = filterFrequentItemsBySupportThreshold(getItemCounts(baskets));
		HashMap<String, Integer> mapTwoItemsSets =  filterFrequentItemsBySupportThreshold(generateTwoItemPairs(baskets, 
				toList(mapSupportedItems.keySet())));
		HashMap<String, Integer> mapThreeItemsSets = filterFrequentItemsBySupportThreshold(genrateThreeItemPairs(baskets, 
				toList(mapSupportedItems.keySet())));
		

		//System.out.println(mapTwoItemsSets.toString());
		
		Set<String> keys = mapThreeItemsSets.keySet();
		for (String key : keys) {
			System.out.println(key + "->" + mapThreeItemsSets.get(key));
		}
		
	}
	
	
	public static HashMap<String, Integer> genrateThreeItemPairs(List<String> baskets, List<String> frequentItems){
		
		HashMap<String, Integer>  mapItemPairs = new HashMap<String, Integer>();
		
		List<String> filteredItems;
		
		String pair;
		for (String basketLine : baskets) {
			filteredItems=filterFrequentItems(basketLine.split("\\s+"), frequentItems);
			if(filteredItems.size()>=3){
				for(int i=0;i<filteredItems.size();i++)
					for(int j=i+1; j<filteredItems.size();j++)
						for(int k=j+1; k<filteredItems.size();k++){
							pair = i+","+j+","+k;
							
							if(mapItemPairs.containsKey(pair))
								mapItemPairs.put(pair, mapItemPairs.get(pair) + 1);
							else
								mapItemPairs.put(pair, 1);
						}
			}
		}
		return mapItemPairs;
	}
	
	public static HashMap<String, Integer> generateTwoItemPairs(List<String> baskets, List<String> frequentItems){
		
		HashMap<String, Integer>  mapItemPairs = new HashMap<String, Integer>();
		
		List<String> filteredItems;
		
		String pair;
		for (String basketLine : baskets) {
			filteredItems=filterFrequentItems(basketLine.split("\\s+"), frequentItems);
			if(filteredItems.size() >=2){
				for(int i = 0; i< filteredItems.size(); i++){
					for(int j=i+1;j<filteredItems.size();j++){
						
						pair = i+","+j;
						
						if(mapItemPairs.containsKey(pair))
							mapItemPairs.put(pair, mapItemPairs.get(pair) + 1);
						else
							mapItemPairs.put(i+","+j, 1);
					}
				}
			}
		}
		return mapItemPairs;
	}
	
	public static List<String> filterFrequentItems(String[] itemsinBasket, List<String> frequentItems){
		
		List<String> filteredItemsInBasket = new ArrayList<String>();
				
		for (String basketItem : itemsinBasket) 
			if(frequentItems.contains(basketItem))
				filteredItemsInBasket.add(basketItem);
		
		return filteredItemsInBasket;
	}
	
	public static List<String> toList(Set<String> itemName){
		List<String> items = new ArrayList<String>();
		
		for (String string : itemName) {
			items.add(string);
		}
		return items;
	}
	
	public static HashMap<String, Integer>  filterFrequentItemsBySupportThreshold(HashMap<String, Integer>
		mapItemsByCount){
		
		HashMap<String,Integer> filteredMap = new HashMap<String, Integer>();
		
		Set<Entry<String, Integer>> entrySets =  mapItemsByCount.entrySet();
		for (Entry<String, Integer> entry : entrySets) {
			if(entry.getValue() >= 100)
				filteredMap.put(entry.getKey(), entry.getValue());
		}
		return filteredMap;
	}
	
	public static HashMap<String, Integer> getItemCounts(List<String> baskets){
		
		HashMap<String, Integer>  mapItems = new HashMap<String, Integer>();
		
		String[] items;
		
		for (String basket : baskets) {
			items = basket.split("\\s+");
			for (String item : items) {
				if(mapItems.containsKey(item)){
					mapItems.put(item, mapItems.get(item) + 1);
				}
				else
					mapItems.put(item, 1);
			}
		}
		return mapItems;
	}
	
}
