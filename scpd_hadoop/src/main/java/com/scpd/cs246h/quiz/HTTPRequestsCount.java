package com.scpd.cs246h.quiz;

import java.io.IOException;
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

public class HTTPRequestsCount {

	public static class Map
	extends Mapper<LongWritable, Text, Text, IntWritable>{

		
		/**
		 * The mapper class provides an output of below for each userID
		 *  list of (1 - friends)
		 *  list of (2 - potential friends)
		 */

		public void map(LongWritable key, Text value, Context context
				) throws IOException, InterruptedException {

			String[] eachLine = value.toString().split("\\s+");
			
			
			context.write(new Text(eachLine[0]), new IntWritable(1));
		
		}
	}
	
	public static class Reduce extends Reducer<Text,IntWritable,Text,IntWritable> {

		public void reduce(Text ipaddress, Iterable<IntWritable> values,
				Context context
				) throws IOException, InterruptedException {
			
			int sum = 0;
			for (IntWritable intWritable : values) {
				sum += intWritable.get();
			}
			
			context.write(ipaddress, new IntWritable(sum));
		}
		
	}
	

	public static void main(String[] args) throws Exception {
       
		
		Configuration conf = new Configuration();
        
        Job job = Job.getInstance(conf, "IP Logs");
        job.setJarByClass(HTTPRequestsCount.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
 
        job.waitForCompletion(true);
    

	}
}