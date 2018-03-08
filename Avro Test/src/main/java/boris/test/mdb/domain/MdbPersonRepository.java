package boris.test.mdb.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface MdbPersonRepository extends ReactiveMongoRepository<MdbPerson, String> {
	Flux<MdbPerson> findByAgeGreaterThanEqual(int age);	
}
