package boris.test.avro;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import boris.test.avro.domain.Person;


public class PersonConsumer implements ApplicationRunner {

	@Autowired
	@Value("#{streamsConfigs}")
	private Map<String, Object> streamsConfigs;

	@Value("${avroTest.kafka.topic}")
	private String topic;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		StreamsBuilder builder = new StreamsBuilder();
		builder.<String, Person>stream(topic).foreach((k, v) -> {
			System.out.println("&&&&&&&&&&&&&&&& " + k + ":" + v.getAge());
		});
		
		final Properties props = new Properties();
		streamsConfigs.keySet().stream().forEach(k -> props.put(k, streamsConfigs.get(k)));
		
		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();
	}

}
