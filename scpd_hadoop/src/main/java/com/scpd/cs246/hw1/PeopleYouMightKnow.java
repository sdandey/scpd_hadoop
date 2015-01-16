package com.scpd.cs246.hw1;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PeopleYouMightKnow {

	public static class Map
	extends Mapper<Object, Text, Text, Text>{


		public void map(Object key, Text value, Context context
				) throws IOException, InterruptedException {

			//value will be of format of <USER> <TAB>  <FRIENDS LIST, delimited>

			String[] userAndFriends = null;

			if(value != null && !"".equals(value.toString().trim()))
				userAndFriends = value.toString().split("\\s+");

			if(userAndFriends.length != 2)
				return;
			
			String userId = userAndFriends[0];
			String friends = userAndFriends[1];

			//load all the friends list for the user into the List
			List<String> friendsList = new ArrayList<String>(Arrays.asList(friends.split(",")));

			
			//Load the entire document into a hashmap with userID as a key and List of Friends as value
			
			if(context.getCacheFiles() == null || context.getCacheFiles().length != 1){
				context.write(new Text(userId), new Text("CACHE FILE NOT FOUND"));
				return;
			}

			URI fileURI = context.getCacheFiles()[0];
			HashMap<String, List<String>> mapCachedDocument = Utils.getCacheFileAsHashMap(FileUtils.readLines(new File("/Users/santoshdandey/Desktop/Dropbox/stanford/cache.txt")));
			//System.out.println("mapCachedDocument:" + mapCachedDocument.toString());
			
			//now perform analysis on each friend and output friendId, number of mutual friends,  mutualFriendsList, potentialMutualFriends
			
			UserIntersectionFriend userIntersectionFriend = null;
			
			List<UserIntersectionFriend> listFriends = new ArrayList<UserIntersectionFriend>();
		
			for (String friendId : friendsList) {
				userIntersectionFriend = Utils.getMutualFriend(friendsList, friendId, mapCachedDocument.get(friendId));
				listFriends.add(userIntersectionFriend);
//				System.out.println("userId:" + userId + "values " + userIntersectionFriend.toString());
//				context.write(new Text(userId), new Text(userIntersectionFriend.toString()));
			}

			
			//Sort the list based on the mutual friends count
			Collections.sort(listFriends,new Comparator<UserIntersectionFriend>() {

				public int compare(UserIntersectionFriend o1,
						UserIntersectionFriend o2) {
					return o1.getMutualFriendsCount() - o2.getMutualFriendsCount();
				}
			});
			
			String  mutualFriendsSuggestions = "";
			
			
			int n = 0;
			for (UserIntersectionFriend userIntersectionFriend2 : listFriends) {
				
				List<String> potentialFriendsList = userIntersectionFriend2.getPotentialFriends();
				
				if(n == 10)
					break;
				
				for (String potentialFriend : potentialFriendsList) {
					
					if(n == 10)
						break;
					
					mutualFriendsSuggestions += potentialFriend + "(" + userIntersectionFriend2.getMutualFriendsCount() + ")     " ;
					n++;
				}
				
			}
			
			context.write(new Text(userId), new Text(mutualFriendsSuggestions));
		}
	}
	

	public static class Reduce extends Reducer<Text,Text,Text,Text> {

		public void reduce(Text userId, Iterable<Text> mapperOutputWithMutualFriendsInfo,
				Context context
				) throws IOException, InterruptedException {
			
			for (Text mapperIntersectionOutput : mapperOutputWithMutualFriendsInfo){
				context.write(userId, new Text(mapperIntersectionOutput));
				
				
			}
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf, "word count");

		job.setJarByClass(PeopleYouMightKnow.class);
		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.addCacheFile(new URI("/Users/santoshdandey/Desktop/Dropbox/stanford/cache.txt"));

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}