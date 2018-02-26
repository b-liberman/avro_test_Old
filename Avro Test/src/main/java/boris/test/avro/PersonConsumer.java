package boris.test.avro;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import boris.test.avro.domain.Person;

@Component
public class PersonConsumer implements ApplicationRunner {

	@Autowired
	private KStream<String, Person> personKStream;
	
	@Autowired()
	@Qualifier("&kStreamBuilder")
	private StreamsBuilderFactoryBean kStreamBuilderFactoryBean;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		personKStream.foreach((k, v) -> {
			System.out.println("&&&&&&&&&&&&&&&& " + k + ":" + v.getAge() + ":" + v.getNewOptField() + ":" + v.getAddress().getCity());
		});
		kStreamBuilderFactoryBean.start();
	}

}
