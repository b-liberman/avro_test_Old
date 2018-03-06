package boris.test.avro;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import boris.test.avro.domain.Person;
import boris.test.avro.mdb.domain.MdbPerson;
import boris.test.avro.mdb.domain.MdbPersonRepository;

@Component
public class PersonConsumer implements ApplicationRunner {

	@Autowired
//	private KStream<String, GenericRecord> personKStream;
	 private KStream<String, Person> personKStream;

	@Autowired
	@Qualifier("&kStreamBuilder")
	private StreamsBuilderFactoryBean kStreamBuilderFactoryBean;

	@Autowired
	private WebClient webClient;
	
	@Autowired
	private MdbPersonRepository mdbPersonRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		personKStream.foreach((k, v) -> {
			// System.out.println("&&&&&&&&&&&&&&&& " + k + ":" + v.getAge() + ":" +
			// v.getNewOptField() + ":" + v.getAddress().getCity());
			Object city = ((GenericRecord) v.get("address")).get("city");
			System.out.println("&&&&&&&&&&&&&&&& " + k + ":" + city);
			webClient.get().uri("/mock/{id}", city).exchange().subscribe(response -> response.bodyToMono(String.class)
					.subscribe(str -> System.out.println("------ " + str)));
			
			mdbPersonRepository.save(new MdbPerson(v));
			
		});
		kStreamBuilderFactoryBean.start();
	}

}
