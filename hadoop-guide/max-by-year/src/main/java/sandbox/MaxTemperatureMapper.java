package sandbox;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MaxTemperatureMapper extends
		Mapper<LongWritable, Text, Text, DoubleWritable> {
	private static final double LIMIT = 300.0;
	private static final String HEADER = "STN";

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {		
		String line = value.toString();
		if(line.startsWith(HEADER)) return;
		String year = line.substring(14, 18).trim();
		String tempStr = line.substring(103, 108).trim();
		double temp = Double.parseDouble(tempStr);
		if (temp < LIMIT) {
			context.write(new Text(year), new DoubleWritable(temp));
		}
	}
}