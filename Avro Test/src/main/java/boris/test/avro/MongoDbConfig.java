package boris.test.avro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;


import boris.test.mdb.domain.MdbPersonRepository;

@Configuration
//@EnableMongoRepositories(basePackageClasses = MdbPersonRepository.class)
@EnableReactiveMongoRepositories(basePackageClasses = MdbPersonRepository.class)
public class MongoDbConfig {
	
	@Value("${spring.data.mongodb.uri}")
	private String mongoConnectinoString;
	
	@Bean
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(mongoConnectinoString);
		return MongoClients.create(connectionString);
	}
	
	@Bean
	public ReactiveMongoTemplate reactiveMongoTemplate() {
		return new ReactiveMongoTemplate(mongoClient(), "myFirstDB");
	}
	
}
