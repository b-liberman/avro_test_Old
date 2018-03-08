package boris.test.avro;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import boris.test.mdb.domain.MdbPersonRepository;

@Configuration
//@EnableMongoRepositories(basePackageClasses = MdbPersonRepository.class)
@EnableReactiveMongoRepositories(basePackageClasses = MdbPersonRepository.class)
public class MongoDbConfig {
}
