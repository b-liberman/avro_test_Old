package boris.test.mdb.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface MdbPersonRepository extends ReactiveMongoRepository<MdbPerson, String>, MdbPersonRepositoryCustom {
	Flux<MdbPerson> findByAgeGreaterThanEqual(int age);

	Flux<MdbPerson> findByAddressStreetaddress(String street);
}
