package boris.test.avro;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class StringIntProducer {

	@Autowired
	private KafkaTemplate<String, Integer> kafkaStringIntTemplate;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${avroTest.kafka.baStringIntTopic}")
	private String topic;

	@Autowired
	private Map<Integer, String> entryStringKeys;

	@Scheduled(fixedDelay = 1000)
	private void producePersonRecord() {

		ThreadLocalRandom currentTlr = ThreadLocalRandom.current();
		String key = entryStringKeys.get(currentTlr.nextInt(0, 4)) + ":ba" + currentTlr.nextInt(1, 5);
		int intValue = currentTlr.nextInt(0, 5);

		final ProducerRecord<String, Integer> record = new ProducerRecord<String, Integer>(topic, key, intValue);
		ListenableFuture<SendResult<String, Integer>> future = kafkaStringIntTemplate.send(record);
		future.addCallback(new ListenableFutureCallback<SendResult<String, Integer>>() {

			@Override
			public void onSuccess(SendResult<String, Integer> result) {
				log.debug("SUCCESS " + result.getProducerRecord().key() + ":" + result.getProducerRecord().value());
			}

			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();
			}

		});
	}
}
