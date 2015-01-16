package com.scpd.cs246.hw1;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
			System.out.println("File Found! Loading File to HashMap" + fileURI.toString());
			HashMap<String, List<String>> mapCachedDocument = Utils.getCacheFileAsHashMap(FileUtils.readLines(new File("/Users/santoshdandey/Desktop/Dropbox/stanford/cache.txt")));
			//System.out.println("mapCachedDocument:" + mapCachedDocument.toString());
			
			//now perform analysis on each friend and output friendId, number of mutual friends,  mutualFriendsList, potentialMutualFriends
			
			UserIntersectionFriend userIntersectionFriend = null;
			for (String friendId : friendsList) {
				userIntersectionFriend = Utils.getMutualFriend(friendsList, friendId, mapCachedDocument.get(friendId));
				System.out.println("userId:" + userId + "values " + userIntersectionFriend.toString());
				context.write(new Text(userId), new Text(userIntersectionFriend.toString()));
			}
		}
	}


	public static class Reduce extends Reducer<Text,Text,Text,Text> {

		public void reduce(Text userId, Iterable<Text> mapperOutputWithMutualFriendsInfo,
				Context context
				) throws IOException, InterruptedException {
			
			for (Text mapperIntersectionOutput : mapperOutputWithMutualFriendsInfo){
				
				
				
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
		job.addCacheFile(new URI(args[0]));

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}