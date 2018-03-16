package boris.test.avro;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import boris.test.avro.domain.AddressUSRecord;
import boris.test.avro.domain.Person;

//@Component
public class StringPersonProducer {

	@Autowired
	private KafkaTemplate<String, Person> kafkaStringPersonTemplate;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${avroTest.kafka.baStringIntTopic}")
	private String topic;

	@Autowired
	private Map<Integer, String> entryKeys;

	@Scheduled(fixedDelay = 3000)
	private void producePersonRecord() {

		String key = entryKeys.get(ThreadLocalRandom.current().nextInt(0, 6));
		int age = ThreadLocalRandom.current().nextInt(20, 50);
		Person person = Person.newBuilder().setAge(age).setName("name:" + age + ":" + key).setTags(Arrays.asList("t1", "t2", "t3"))
				.setOptField("my opt opt opt")
				// .setAddress(AddressUSRecord.newBuilder().build()).build();
				.setAddress(AddressUSRecord.newBuilder().setCity("city" + age).setStreetaddress("street" + age).build())
				.build();

		// logRecord(person);

		final ProducerRecord<String, Person> record = new ProducerRecord<String, Person>(topic,
				// "key" + UUID.randomUUID(),
				key, person);
		ListenableFuture<SendResult<String, Person>> future = kafkaStringPersonTemplate.send(record);
		future.addCallback(new ListenableFutureCallback<SendResult<String, Person>>() {

			@Override
			public void onSuccess(SendResult<String, Person> result) {
				log.debug("SUCCESS " + result.getProducerRecord().key());
			}

			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();
			}

		});
	}

	private void logRecord(Person person) {
		try {
			JsonEncoder encoder = EncoderFactory.get().jsonEncoder(person.getSchema(), System.out);
			DatumWriter<Person> writer = new SpecificDatumWriter<Person>();
			writer.setSchema(person.getSchema());
			writer.write(person, encoder);
			encoder.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
