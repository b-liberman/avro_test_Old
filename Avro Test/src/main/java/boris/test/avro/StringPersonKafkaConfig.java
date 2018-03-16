package boris.test.avro;

import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;

import boris.test.avro.domain.Person;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

@Configuration
@EnableKafka
public class StringPersonKafkaConfig {

	@Value("${avroTest.kafka.bootstrapServers}")
	private String kafkaBootstrapServers;

	@Value("${avroTest.kafka.schemaRegistry}")
	private String schemaRegistry;

	@Value("${avroTest.kafka.streams.applicationId}")
	private String streamsApplicationId;

	@Value("${avroTest.kafka.stringPersonTopic}")
	private String stringPersonTopic;

	@Bean
	public Map<String, Object> stringPersonProducerConfigs() {
		Map<String, Object> producerProps = new HashMap<>();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, "AvroStringPersonProducer");
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
		producerProps.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
		return producerProps;
	}

	@Bean
	public ProducerFactory<String, Person> stringPersonProducerFactory() {
		return new DefaultKafkaProducerFactory<String, Person>(stringPersonProducerConfigs());
	}

	@Bean
	public KafkaTemplate<String, Person> kafkaStringPersonTemplate() {
		return new KafkaTemplate<String, Person>(stringPersonProducerFactory());
	}

	@Bean
	public Map<String, Object> streamsStringPersonConfigs() {
		Map<String, Object> streamsConfiguration = new HashMap<>();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, streamsApplicationId +  "StringPerson");
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		streamsConfiguration.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
		// streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
		// GenericAvroSerde.class);
		return streamsConfiguration;
	}

	@Bean
	public StreamsConfig kStreamsStringPersonConfigs() {
		return new StreamsConfig(streamsStringPersonConfigs());
	}

	@Bean
	public StreamsBuilderFactoryBean kStreamStringPersonBuilder() {
		StreamsBuilderFactoryBean streamsBuilderFactoryBean = new StreamsBuilderFactoryBean(kStreamsStringPersonConfigs());
		streamsBuilderFactoryBean.setAutoStartup(false);
		return streamsBuilderFactoryBean;
	}

	@Bean
	public KStream<String, Person> stringPersonKStream() throws Exception {
		return kStreamStringPersonBuilder().getObject().stream(stringPersonTopic);
	}

	@Bean
	public Map<Integer, String> entryKeys() {
		return Stream
				.of(new SimpleEntry<Integer, String>(0, "key0"), new SimpleEntry<Integer, String>(1, "key1"),
						new SimpleEntry<Integer, String>(2, "key2"), new SimpleEntry<Integer, String>(3, "key3"),
						new SimpleEntry<Integer, String>(4, "key4"), new SimpleEntry<Integer, String>(5, "key5"))
				.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
	}

	// @Bean
	// public KStream<String, GenericRecord> personKStream() throws Exception {
	// return kStreamBuilder().getObject().stream(topic);
	// }
}
