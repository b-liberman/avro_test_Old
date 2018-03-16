package boris.test.avro;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;

@Configuration
@EnableKafka
public class StringIntKafkaConfig {

	@Value("${avroTest.kafka.bootstrapServers}")
	private String kafkaBootstrapServers;

	@Value("${avroTest.kafka.streams.applicationId}")
	private String streamsApplicationId;

	@Value("${avroTest.kafka.baStringIntTopic}")
	private String baTopic;

	@Value("${avroTest.kafka.rhStringIntTopic}")
	private String rhTopic;

	@Bean
	public Map<String, Object> stringIntProducerConfigs() {
		Map<String, Object> producerProps = new HashMap<>();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, "AvroStringIntProducer");
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
		return producerProps;
	}

	@Bean
	public ProducerFactory<String, Integer> stringIntProducerFactory() {
		return new DefaultKafkaProducerFactory<String, Integer>(stringIntProducerConfigs());
	}

	@Bean
	public KafkaTemplate<String, Integer> kafkaStringIntTemplate() {
		return new KafkaTemplate<String, Integer>(stringIntProducerFactory());
	}

	@Bean
	public Map<String, Object> streamsStringIntConfigs() {
		Map<String, Object> streamsConfiguration = new HashMap<>();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, streamsApplicationId + "StringInt");
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());

//		streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
//		streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
		
//		streamsConfiguration.put(StreamsConfig.producerPrefix(ProducerConfig.BATCH_SIZE_CONFIG), 1);
//		streamsConfiguration.put(StreamsConfig.consumerPrefix(ConsumerConfig.MAX_POLL_RECORDS_CONFIG), 1);
		
		return streamsConfiguration;
	}

	@Bean
	public StreamsConfig kStreamsStringIntConfigs() {
		return new StreamsConfig(streamsStringIntConfigs());
	}

	@Bean
	public StreamsBuilderFactoryBean kStreamStringIntBuilder() {
		StreamsBuilderFactoryBean streamsBuilderFactoryBean = new StreamsBuilderFactoryBean(kStreamsStringIntConfigs());
		streamsBuilderFactoryBean.setAutoStartup(false);
		return streamsBuilderFactoryBean;
	}

	@Bean
	public Map<Integer, String> entryStringKeys() {
		return Stream
				.of(new SimpleEntry<Integer, String>(0, "key0"), new SimpleEntry<Integer, String>(1, "key1"),
						new SimpleEntry<Integer, String>(2, "key2"), new SimpleEntry<Integer, String>(3, "key3"))
				.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
	}

	@Bean
	public KafkaAdmin admin() {
		return new KafkaAdmin(stringIntProducerConfigs());
	}

	@Bean
	public NewTopic baTopic() {
		return new NewTopic(baTopic, 1, (short) 1);
	}

	@Bean
	public NewTopic rhTopic() {
		return new NewTopic(rhTopic, 1, (short) 1);
	}
}
