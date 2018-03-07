package boris.test.avro;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import boris.test.mdb.domain.MdbPersonRepository;

@Configuration
@EnableMongoRepositories(basePackageClasses = MdbPersonRepository.class)
public class MongoDbConfig {}
