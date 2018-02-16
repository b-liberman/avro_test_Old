package boris.test.avro;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
				.build();
		
		try {
			person.writeExternal(new ObjectOutputStream(System.out));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		final ProducerRecord<String, Person> record = new ProducerRecord<String, Person>(topic, "key" + UUID.randomUUID(), person);
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

}
