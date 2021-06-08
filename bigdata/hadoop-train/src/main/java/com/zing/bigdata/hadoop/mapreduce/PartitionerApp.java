package com.zing.bigdata.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class PartitionerApp {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] words = line.split(" ");

            context.write(new Text(words[0]), new LongWritable(Long.parseLong(words[1])));
        }
    }

    public static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long sum = 0;
            for (LongWritable value : values) {
                sum += value.get();
            }

            context.write(key, new LongWritable(sum));
        }
    }

    public static class MyPartitioner extends Partitioner<Text, LongWritable> {
        @Override
        public int getPartition(Text key, LongWritable value, int numPartitions) {
            if (key.toString().equals("xiaomi")) {
                return 0;
            } else if (key.toString().equals("huawei")) {
                return 1;
            } else if (key.toString().equals("iphone7")) {
                return 2;
            }
            return 3;
        }
    }

    //hadoop jar hadoop-train-1.0.0.jar WordCountApp hdfs:/word.txt hdfs:/output/wc
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();

        Path path = new Path(args[1]);
        FileSystem fileSystem = FileSystem.get(configuration);
        if (fileSystem.exists(path)) {
            fileSystem.delete(path, true);
            System.out.println("output file exists, but is has been deleted.");
        }

        Job job = Job.getInstance(configuration, "wordcount");

        job.setJarByClass(PartitionerApp.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setPartitionerClass(MyPartitioner.class);
        job.setNumReduceTasks(4);

        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
