package boris.test.avro;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import boris.test.avro.domain.AddressUSRecord;
import boris.test.avro.domain.Person;

@Component
public class PersonProducer {

	@Autowired
	private KafkaTemplate<String, Person> kafkaPersonTemplate;

	@Value("${avroTest.kafka.topic}")
	private String topic;

	@Scheduled(fixedDelay = 3000)
	private void producePersonRecord() {
		Person person = Person.newBuilder().setAge(35).setName("name123").setTags(Arrays.asList("t1", "t2", "t3"))
				.setOptField("my opt opt opt")
				.setAddress(AddressUSRecord.newBuilder().build())
				.build();
//		Person person = new Person();
//		person.setAge(35);
//		person.setName("name345");
//		person.setTags(Arrays.asList("t1", "t2", "t3"));
//		person.setOptField("my opt field");

//		logRecord(person);

		final ProducerRecord<String, Person> record = new ProducerRecord<String, Person>(topic,
				"key" + UUID.randomUUID(), person);
		ListenableFuture<SendResult<String, Person>> future = kafkaPersonTemplate.send(record);
		future.addCallback(new ListenableFutureCallback<SendResult<String, Person>>() {

			@Override
			public void onSuccess(SendResult<String, Person> result) {
				System.out.println("SUCCESS " + result.getProducerRecord().key());
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
