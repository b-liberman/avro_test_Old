package boris.test.avro.mdb.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MdbPersonRepository extends MongoRepository<MdbPerson, String> {
	
}
