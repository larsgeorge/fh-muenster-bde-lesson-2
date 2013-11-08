package org.fhmuenster.bde.mr;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class CopyOfLogGenerator extends MapReduceBase
implements Mapper<NullWritable, NullWritable, NullWritable, Text>{

	private String[] uri = new String[] { "index.html", "contacts.html", "logo.gif"};
	private Random rand = new Random();
	private Text line = new Text();
	
	@Override
	public void map(NullWritable arg0, NullWritable arg1,
			OutputCollector<NullWritable, Text> collector, Reporter reporter)
			throws IOException {
		// 127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
		StringBuilder sb = new StringBuilder();
		sb.append("10.0.1." + (rand.nextInt(10) + 1)).append(" - - ");
		sb.append("[10/Oct/2000:13:55:36 -0700] \"GET /").append(uri[rand.nextInt(uri.length)]).append(" HTTP/1.0\"");
		sb.append(200).append(rand.nextInt(3000));
		line.set(sb.toString());
		collector.collect(null, line);
	}
	
	public void run(String outputPath) throws Exception {
		JobConf conf = new JobConf(CopyOfLogGenerator.class);
		conf.setJobName("log-generator");

		conf.setOutputKeyClass(NullWritable.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(CopyOfLogGenerator.class);
		conf.setNumReduceTasks(0);

		FileOutputFormat.setOutputPath(conf, new Path(outputPath));

		JobClient.runJob(conf);
	}

	public static void main(String[] args) {
		CopyOfLogGenerator lg = new CopyOfLogGenerator();
		try {
			lg.run(args[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
