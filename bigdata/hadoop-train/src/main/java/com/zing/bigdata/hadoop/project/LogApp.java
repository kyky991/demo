package com.zing.bigdata.hadoop.project;

import com.kumkee.userAgent.UserAgent;
import com.kumkee.userAgent.UserAgentParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogApp {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

        LongWritable one = new LongWritable(1);

        private UserAgentParser userAgentParser;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            userAgentParser = new UserAgentParser();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            String source = line.substring(getCharacterPosition(line, "\"", 7)) + 1;
            UserAgent agent = userAgentParser.parse(source);
            String browser = agent.getBrowser();

            context.write(new Text(browser), one);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            userAgentParser = null;
        }
    }

    private static int getCharacterPosition(String value, String operator, int index) {
        Matcher matcher = Pattern.compile(operator).matcher(value);
        int idx = 0;
        while (matcher.find()) {
            idx++;
            if (idx == index) {
                break;
            }
        }
        return matcher.start();
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


    //hadoop jar hadoop-train-1.0.0.jar com.zing.hadoop.mapreduce.LogApp /10000_access.log /output/log
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();

        Path path = new Path(args[1]);
        FileSystem fileSystem = FileSystem.get(configuration);
        if (fileSystem.exists(path)) {
            fileSystem.delete(path, true);
            System.out.println("output file exists, but is has been deleted.");
        }

        Job job = Job.getInstance(configuration, "LogApp");

        job.setJarByClass(LogApp.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setCombinerClass(MyReducer.class);

        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
