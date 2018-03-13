package boris.test.mdb.domain;

import reactor.core.publisher.Flux;

public interface MdbPersonRepositoryCustom {
	Flux<AggrResult> aggregate(int minAge, int maxAge);
}
