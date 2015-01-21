package com.scpd.cs246.hw1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class PeopleYouMightKnow {

	public static class Map
	extends Mapper<LongWritable, Text, IntWritable, Text>{

		
		/**
		 * The mapper class provides an output of below for each userID
		 *  list of (1 - friends)
		 *  list of (2 - potential friends)
		 */

		public void map(LongWritable key, Text value, Context context
				) throws IOException, InterruptedException {

			//value will be of format of <USER> <TAB>  <FRIENDS LIST, delimited>
			String[] userAndFriends = value.toString().split("\t");

			if(userAndFriends.length != 2)
				return;
			String userId = userAndFriends[0];
			String[] friends = userAndFriends[1].split(",");
			
			for (int i = 0; i < friends.length; i ++) {
				//suppose A->B,C,D,E Then (A,B,1) (A,C,1) (A,D,1) (A,E,1) are first degree friends
				context.write(new IntWritable(Integer.valueOf(userId)), new Text("F,"+friends[i]));
				for(int j=i+1; j< friends.length; j++){
					//Suppose A->B,C,D,E  B can be friends with c (B,C,2) and c can be friends with d (C,B,2)
					context.write(new IntWritable(Integer.valueOf(friends[i])), new Text(friends[j]));
					context.write(new IntWritable(Integer.valueOf(friends[j])), new Text(friends[i]));
				}
			}
		}
	}
	
	public static class Reduce extends Reducer<IntWritable,Text,IntWritable,Text> {

		public void reduce(IntWritable userId, Iterable<Text> mapperOutput,
				Context context
				) throws IOException, InterruptedException {
			
			
			
			List<Integer> friends = new ArrayList<Integer>();
			HashMap<Integer, Integer> potentialFriendsCounts = new HashMap<Integer, Integer>();
			
			
			String value;
			for (Text text : mapperOutput) {
				
				value = text.toString();
				
				if(value.split(",").length == 2){
					friends.add(Integer.valueOf(value.split(",")[1]));
				}
				else {
					if(potentialFriendsCounts.containsKey(value)){
						potentialFriendsCounts.put(Integer.valueOf(value), potentialFriendsCounts.get(value) + 1);
					}
					else
						potentialFriendsCounts.put(Integer.valueOf(value), 1);
				}
				
			}
			
			//System.out.println("Friends List:" + friends);
		
			//Remove friends from potentialFriendsCount
			for (Integer friend : friends) {
				if(potentialFriendsCounts.containsKey(friend))
					potentialFriendsCounts.remove(friend);
			}
			
			 ArrayList<Entry<Integer, Integer>> potentialFriendsByHighesCount = new ArrayList<Entry<Integer, Integer>>();
	            for (Entry<Integer, Integer> entry : potentialFriendsCounts.entrySet()) {
	            	potentialFriendsByHighesCount.add(entry);
	            }

			 Collections.sort(potentialFriendsByHighesCount, new Comparator<Entry<Integer, Integer>>() {
	                public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
	                    return o2.getValue().compareTo(o1.getValue());
	                }
	            });
			
			 
			 String friendSuggestions = "\t";
			 
			 int n = 0;
			 for (Entry<Integer, Integer> entry : potentialFriendsByHighesCount) {
				 if(n<10)
					 friendSuggestions += entry.getKey() + "(" + entry.getValue() + ")" + ",";
				 else
					 break;
				 
				 n++;
			}
			 
			 String usersText = "924,8941,8942,9019,9020,9021,9022,9990,9992,9993";
             
			 String[] users = usersText.split(",");
			 for (String user : users) {
				if(user.equals(userId.toString()))
					context.write(userId, new Text(friendSuggestions));
			 }
		}
		
	}
	

	public static void main(String[] args) throws Exception {
       
		
		Configuration conf = new Configuration();
        
        Job job = Job.getInstance(conf, "Find Friends");
        job.setJarByClass(PeopleYouMightKnow.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
 
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
 
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
 
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
 
        job.waitForCompletion(true);
    

	}
}