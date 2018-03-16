package boris.test.avro;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueIterator;
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
import org.springframework.stereotype.Component;

@Component
public class StringIntConsumer implements ApplicationRunner {

	private static final String LAST_RH_RESULTS_STORE = "lastRHResultsStore";

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("&kStreamStringIntBuilder")
	private StreamsBuilderFactoryBean kStreamBuilderFactoryBean;

	@Value("${avroTest.kafka.baStringIntTopic}")
	private String baTopic;

	@Value("${avroTest.kafka.rhStringIntTopic}")
	private String rhTopic;

	private ReadOnlyKeyValueStore<String, Integer> store;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		StreamsBuilder streamsBuilder = kStreamBuilderFactoryBean.getObject();

		// this is the triggger. just copy over to rh stream
		streamsBuilder.<String, Integer>stream(baTopic).map((k, v) -> new KeyValue<String, Integer>(k, v + 1))
				.peek((k, v) -> log.debug("TO RH STREAM: " + k + ":" + v)).to(rhTopic);

		streamsBuilder.<String, Integer>stream(rhTopic)
				// reduce to just the last entry per back end, i.e. the final result
				.groupByKey()
				.reduce((aggr, v) -> v,
						Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as(LAST_RH_RESULTS_STORE)
								.withCachingDisabled())
				// process the result by checking all related entries from the other back ends
				.toStream().foreach((k, v) -> {
					log.debug("FROM RH LAST RESULT TABLE: " + k + ":" + v);
					String keyPart = k.substring(0, k.indexOf(":"));

					KeyValueIterator<String, Integer> kvIterator = getStore().range(keyPart, keyPart + "xxxxxxxxxx");

					// here we get all related entries
					while (kvIterator.hasNext()) {
						KeyValue<String, Integer> kv = kvIterator.next();
						log.debug("----- RELATED ENTRY " + kv.key + ":" + kv.value);
					}
					kvIterator.close();
				});
		kStreamBuilderFactoryBean.start();
	}

	private ReadOnlyKeyValueStore<String, Integer> getStore() {
		if (store == null) {
			try {
				KafkaStreams kafkaStreams = kStreamBuilderFactoryBean.getKafkaStreams();
				if (kafkaStreams == null) {
					throw new IllegalStateException("racing conditions as streams are not initialized yet");
				}
				store = kafkaStreams.store(LAST_RH_RESULTS_STORE, QueryableStoreTypes.<String, Integer>keyValueStore());
			} catch (InvalidStateStoreException e) {
				log.debug("store not initialized yet: " + e.getMessage());
				throw new IllegalStateException(
						"racing conditions as streams are not initialized yet: " + e.getMessage());
			}
		}
		return store;
	}
}
