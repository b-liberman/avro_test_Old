package boris.test.avro;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;

import boris.test.avro.domain.Person;
import boris.test.mdb.domain.MdbPerson;
import boris.test.mdb.domain.MdbPersonRepository;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

//@Component
public class StringPersonConsumer implements ApplicationRunner {

	private static final String AGGREGATED_AGE_PERSON_STORE = "aggregated_age_person_store";

	@Autowired
	// private KStream<String, GenericRecord> personKStream;
	private KStream<String, Person> stringPersonKStream;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("&kStreamStringPersonBuilder")
	private StreamsBuilderFactoryBean kStreamBuilderFactoryBean;

	@Autowired
	private WebClient webClient;

	@Autowired
	private MdbPersonRepository mdbPersonRepository;

	@Autowired
	private Map<Integer, String> entryKeys;

	private ReadOnlyKeyValueStore<String, Person> store;
	
	@Value("${avroTest.kafka.schemaRegistry}")
	private String schemaRegistry;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		stringPersonKStream.foreach((k, v) -> {
			handlePerson(k, v);
		});

		SpecificAvroSerde<Person> valueSerde = new SpecificAvroSerde<Person>();
		Map<String, Object> valueSerdeConfig = new HashMap<>();
		valueSerdeConfig.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
		valueSerde.configure(valueSerdeConfig, true);
		
		stringPersonKStream.groupByKey().reduce((aggrAgePerson, person) -> {
			aggrAgePerson.setAge(aggrAgePerson.getAge() + person.getAge());
			return aggrAgePerson;
		}, Materialized.<String, Person, KeyValueStore<Bytes, byte[]>>as(AGGREGATED_AGE_PERSON_STORE)
				.withKeySerde(Serdes.String()).withValueSerde(valueSerde));

		Thread.sleep(4000);
		kStreamBuilderFactoryBean.start();
	}

	private void handlePerson(String k, Person v) {
		// System.out.println("&&&&&&&&&&&&&&&& " + k + ":" + v.getAge() + ":" +
		// v.getNewOptField() + ":" + v.getAddress().getCity());
		Object city = ((GenericRecord) v.get("address")).get("city");
		log.debug("&&&&&&&&&&&&&&&& " + k + ":" + city);
		// webClient.get().uri("/mock/{id}", city).exchange().subscribe(response ->
		// response.bodyToMono(String.class)
		// .subscribe(str -> System.out.println("------ " + str)));

		webClient.post().uri("/mock/{id}", city).body(fromObject(v.toString())).exchange()
				.subscribe(response -> response.bodyToMono(String.class).subscribe(str -> log.debug(str)));

		mdbPersonRepository.insert(new MdbPerson(v)).subscribe(p -> log.debug("ENTERED RECORD " + p.id));
	}

	@Scheduled(fixedDelay = 5000)
	private void printStore() {
		if (store == null) {
			try {
				KafkaStreams kafkaStreams = kStreamBuilderFactoryBean.getKafkaStreams();
				if(kafkaStreams == null) {
					return;
				}
				store = kafkaStreams.store(AGGREGATED_AGE_PERSON_STORE,
						QueryableStoreTypes.<String, Person>keyValueStore());
			} catch (InvalidStateStoreException e) {
				log.debug("store not initialized yet: " + e.getMessage());
				return;
			}
		}

		log.debug("........................");
		entryKeys.keySet().stream().forEach(k -> {
			String key = entryKeys.get(k);
			Person person = store.get(key);
			if (person == null) {
				log.debug("---" + key + ": empty");
			} else {
				log.debug("---" + key + ": " + person.getAge());
			}
		});
		log.debug("........................");
	}

}
