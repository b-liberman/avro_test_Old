package boris.test.avro;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class PersonConsumer implements ApplicationRunner {

	@Autowired
	private KStream<String, GenericRecord> personKStream;
//	private KStream<String, Person> personKStream;
	
	@Autowired()
	@Qualifier("&kStreamBuilder")
	private StreamsBuilderFactoryBean kStreamBuilderFactoryBean;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		personKStream.foreach((k, v) -> {
//			System.out.println("&&&&&&&&&&&&&&&& " + k + ":" + v.getAge() + ":" + v.getNewOptField() + ":" + v.getAddress().getCity());
			System.out.println("&&&&&&&&&&&&&&&& " + k + ":" + ((GenericRecord)v.get("address")).get("city"));
		});
		kStreamBuilderFactoryBean.start();
	}

}
