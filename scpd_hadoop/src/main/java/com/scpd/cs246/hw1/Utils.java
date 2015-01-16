package com.scpd.cs246.hw1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Utils {
	
	public static UserIntersectionFriend getMutualFriend(List<String> userFriends, String friendId, List<String> friendFriends){
		
		UserIntersectionFriend userIntersectionFriend = new UserIntersectionFriend();
		
		List<String> mutualFriends = new ArrayList<String>();
		List<String> potentialFriends = new ArrayList<String>();
		
		//find if there are mutual friends
		for (String friend : friendFriends) {
			if(userFriends.contains(friend))
				mutualFriends.add(friend);
			else
				potentialFriends.add(friend);
		}
		
		userIntersectionFriend.setFriendId(friendId);
		userIntersectionFriend.setMutualFriends(mutualFriends);
		userIntersectionFriend.setPotentialFriends(potentialFriends);
		userIntersectionFriend.setMutualFriendsCount(mutualFriends.size());
		return userIntersectionFriend;
	}

	public static HashMap<String, List<String>> getCacheFileAsHashMap(List<String> cachedDocument) throws IOException{
				
		HashMap<String, List<String>>  mapListOfFriendsByUserId = new HashMap<String, List<String>>();
		
		String[] userFriendsText = null;
		
		for (String eachLine : cachedDocument) {
			userFriendsText = eachLine.split("\\s+");
			
			if(userFriendsText.length == 2)
				mapListOfFriendsByUserId.put(userFriendsText[0], Arrays.asList(userFriendsText[1].split(",")));
		}
		
		return mapListOfFriendsByUserId;
	}

}
