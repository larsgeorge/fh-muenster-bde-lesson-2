package org.fhmuenster.bde.mr;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class LogGenerator {

	private String[] uri = new String[] { "index.html", "contacts.html", "logo.gif"};
	private Random rand = new Random();
	
	public void writeLine(FSDataOutputStream out) throws IOException {
		// 127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
		StringBuilder sb = new StringBuilder();
		sb.append("10.0.1." + (rand.nextInt(10) + 1)).append(" - - ");
		sb.append("[10/Oct/2000:13:55:36 -0700] \"GET /").append(uri[rand.nextInt(uri.length)]).append(" HTTP/1.0\" ");
		sb.append(200).append(" ").append(rand.nextInt(3000));
		sb.append("\n");
		out.write(sb.toString().getBytes("ASCII"));
	}
	
	public void run(String outputPath, int count) throws Exception {
		Configuration conf = new Configuration();
	    FileSystem fs = FileSystem.get(conf);
	    Path outPath = new Path(outputPath);
	    FSDataOutputStream out = fs.create(outPath);
	    for (int n = 0; n < count; n++) {
	    	writeLine(out);
	    }
	    out.flush();
	    out.close();
	}

	public static void main(String[] args) {
		LogGenerator lg = new LogGenerator();
		try {
			lg.run(args[0], Integer.parseInt(args[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
