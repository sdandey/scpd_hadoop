package com.scpd.cs246.hw1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
		
		//System.out.println(mapTwoItemsSets.toString());
		
		HashMap<String, Integer> mapThreeItemsSets = filterFrequentItemsBySupportThreshold(genrateThreeItemPairs(baskets, 
				toList(mapSupportedItems.keySet())));
		
		ArrayList<Entry<String,Double>> associationRuleConfidenceForTwoItemSets = findConfidenceScoreForTwoItemSets(mapTwoItemsSets, mapSupportedItems);
		printTop5ItemSets(associationRuleConfidenceForTwoItemSets);
		
		ArrayList<Entry<String,Double>> associationRuleConfidenceForThreeItemSets = findConfidenceScoreForThreeItemSets(mapTwoItemsSets, mapThreeItemsSets);
		printTop5ItemSets(associationRuleConfidenceForThreeItemSets);
		
	}
	
	
	public static void printTop5ItemSets(ArrayList<Entry<String,Double>> sortedAssociateReules){
		
		int i=1;
        for (Entry<String, Double> entry : sortedAssociateReules) {
        	
        	if(i>5)
        		break;
        	
        	System.out.println(entry.getKey() + "(" + entry.getValue() + ")");
        	
        	i++;
		}
	}
	
	public static ArrayList<Entry<String,Double>> findConfidenceScoreForThreeItemSets(HashMap<String, Integer> twoItemSets,
			HashMap<String,Integer> threeItemSets){
	
		
		HashMap<String, Double> associationRules = new HashMap<String, Double>();
		
		Set<String> itemPairs = threeItemSets.keySet();
		
		List<String> pairedItems;
		
		Integer denomonitaor1; 
		Integer denominiator2;
		for (String pair : itemPairs) {
			
			pairedItems = new ArrayList<String>();
			pairedItems = Arrays.asList(pair.split(","));
			Collections.sort(pairedItems);
			//find confidence for the A,B->C and B,A->C
			
			if(pairedItems.size() == 3){
				
				if(twoItemSets.containsKey(pairedItems.get(0) +"," + pairedItems.get(1)) || twoItemSets.containsKey(pairedItems.get(1) +"," + pairedItems.get(0))) {
					
					denomonitaor1 = twoItemSets.containsKey(pairedItems.get(0) +"," + pairedItems.get(1))?twoItemSets.get(pairedItems.get(0) +"," + pairedItems.get(1)):0;
					denominiator2 = twoItemSets.containsKey(pairedItems.get(1) +"," + pairedItems.get(0))?twoItemSets.get(pairedItems.get(1) +"," + pairedItems.get(0)):0;
					
					
					associationRules.put("(" + pairedItems.get(0) +"," + pairedItems.get(1) + ")->" + pairedItems.get(2), ((double)threeItemSets.get(pair))/(denomonitaor1 + denominiator2));
							

				}
			}
		}
		
		ArrayList<Entry<String, Double>> sortedAssociateReules = new ArrayList<Entry<String, Double>>();
        for (Entry<String, Double> entry : associationRules.entrySet()) {
        	sortedAssociateReules.add(entry);
        }

        Collections.sort(sortedAssociateReules, new Comparator<Entry<String, Double>>() {
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
		
        
        
        return sortedAssociateReules;
	}
	
	
	public static ArrayList<Entry<String,Double>> findConfidenceScoreForTwoItemSets(HashMap<String, Integer> twoItemSets,
			HashMap<String, Integer> itemCounts){
	
		
		HashMap<String, Double> associationRules = new HashMap<String, Double>();
		
		Set<String> itemPairs = twoItemSets.keySet();
		
		List<String> pairedItems;
		for (String pair : itemPairs) {
			
			pairedItems = Arrays.asList(pair.split(","));
			Collections.sort(pairedItems);
			//find confidence for the A->B and B->A
			
			if(pairedItems.size() == 2){
				associationRules.put(pairedItems.get(0) + "->" + pairedItems.get(1), ((double)twoItemSets.get(pair))/itemCounts.get(pairedItems.get(0)));
				associationRules.put(pairedItems.get(0) + "->" + pairedItems.get(0), ((double)twoItemSets.get(pair))/itemCounts.get(pairedItems.get(1)));
			}
		}
		
		ArrayList<Entry<String, Double>> sortedAssociateReules = new ArrayList<Entry<String, Double>>();
        for (Entry<String, Double> entry : associationRules.entrySet()) {
        	sortedAssociateReules.add(entry);
        }

        Collections.sort(sortedAssociateReules, new Comparator<Entry<String, Double>>() {
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
		
        
        
        return sortedAssociateReules;
	}
	
	
	class ValueComparator implements Comparator<String> {

	    HashMap<String, Double> base;
	    public ValueComparator(HashMap<String, Double> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
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
							pair = filteredItems.get(i)+","+filteredItems.get(j)+","+filteredItems.get(k);
							
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
						
						pair = filteredItems.get(i)+","+filteredItems.get(j);
						
						if(mapItemPairs.containsKey(pair))
							mapItemPairs.put(pair, mapItemPairs.get(pair) + 1);
						else
							mapItemPairs.put(pair, 1);
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
 