package org.fhmuenster.bde.mr;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.FileInputFormat;

class MapClass extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, IntWritable> {

	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		String line = value.toString();
		StringTokenizer itr = new StringTokenizer(line);
		while (itr.hasMoreTokens()) {
			word.set(itr.nextToken());
			output.collect(word, one);
		}
	}
}

/**
 * A reducer class that just emits the sum of the input values.
 */
class ReduceClass extends MapReduceBase implements
		Reducer<Text, IntWritable, Text, IntWritable> {

	@Override
	public void reduce(Text key, Iterator<IntWritable> values,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		int sum = 0;
		while (values.hasNext()) {
			sum += values.next().get();
		}
		output.collect(key, new IntWritable(sum));
	}
}

public class WordCount {
	public void run(String inputPath, String outputPath) throws Exception {
		JobConf conf = new JobConf(WordCount.class);
		conf.setJobName("wordcount");

		// the keys are words (strings)
		conf.setOutputKeyClass(Text.class);
		// the values are counts (ints)
		conf.setOutputValueClass(IntWritable.class);

		conf.setMapperClass(MapClass.class);
		conf.setReducerClass(ReduceClass.class);

		FileInputFormat.addInputPath(conf, new Path(inputPath));
		FileOutputFormat.setOutputPath(conf, new Path(outputPath));

		JobClient.runJob(conf);
	}

	public static void main(String[] args) {
		WordCount wc = new WordCount();
		try {
			wc.run(args[0], args[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
