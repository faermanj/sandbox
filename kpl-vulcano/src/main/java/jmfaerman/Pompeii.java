package jmfaerman;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;

public class Pompeii implements IRecordProcessor, IRecordProcessorFactory {

	public static void main(String[] args) {
		String APP_NAME = "pompeii";
		String STREAM_NAME = "twitch-stream";
		String workerId = UUID.randomUUID().toString();

		final KinesisClientLibConfiguration config = new KinesisClientLibConfiguration(APP_NAME, STREAM_NAME,
				new DefaultAWSCredentialsProviderChain(), workerId);
		final IRecordProcessorFactory recordProcessorFactory = new Pompeii();
		final Worker worker = new Worker.Builder().recordProcessorFactory(recordProcessorFactory).config(config)
				.build();
		int exitCode = 0;
		try {
			worker.run();
		} catch (Throwable t) {
			System.err.println("Caught throwable while processing data.");
			t.printStackTrace();
			exitCode = 1;
		}
		System.exit(exitCode);
	}

	public void initialize(InitializationInput init) {
		System.out.println("Pompeii Initialized");
	}

	public void processRecords(ProcessRecordsInput input) {		
		System.out.println("Incoming Lava");
		List<Record> records = input.getRecords();
		for (Record record : records) {
			ByteBuffer data = record.getData();
			try {
				System.out.println(new String(data.array(),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void shutdown(ShutdownInput shut) {
		System.out.println("Pompeii shutdown");
	}

	public IRecordProcessor createProcessor() {
		return new Pompeii();
	}
}
