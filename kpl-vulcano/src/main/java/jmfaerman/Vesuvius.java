package jmfaerman;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class Vesuvius {
	static final long PROCRASTINATION = 2000;
	
	public static void main(String[] args) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String uuid = UUID.randomUUID().toString();
		System.out.println("Hello World! Streaming from "+uuid );
		KinesisProducerConfiguration config = new KinesisProducerConfiguration();
		config.setRegion("us-east-1");
		KinesisProducer kinesis = new KinesisProducer(config);
		// Put some records
		for (;;) {
			LavaRock lava = createLavaRock();
			String javaJSON = mapper.writeValueAsString(lava);
			ByteBuffer data = ByteBuffer.wrap(javaJSON.getBytes("UTF-8"));
			// doesn't block
		    System.out.println("Kinesis addUserRecord attempt");
			ListenableFuture<UserRecordResult> addUserRecord = kinesis.addUserRecord("twitch-stream", uuid, data);
			Futures.addCallback(addUserRecord, new FutureCallback<UserRecordResult>() {
				  // we want this handler to run immediately after we push the big red button!
				  public void onSuccess(UserRecordResult userRecord) {
				    System.out.println("Kinesis addUserRecord success");
				  }
				  public void onFailure(Throwable thrown) {
				    thrown.printStackTrace(System.err);
				  }
				});
			Thread.sleep(PROCRASTINATION);
		}
	}

	static final Random random = new Random();
	private static LavaRock createLavaRock() {
		return new LavaRock(700+random.nextInt(500));
	}
}
