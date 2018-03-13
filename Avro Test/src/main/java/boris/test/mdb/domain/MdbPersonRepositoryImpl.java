package boris.test.mdb.domain;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import reactor.core.publisher.Flux;

public class MdbPersonRepositoryImpl implements MdbPersonRepositoryCustom {

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<AggrResult> aggregate(int minAge, int maxAge) {
		MatchOperation matchOperation = getMatchOperation(minAge, maxAge);
		GroupOperation groupOperation = getGroupOperation();
		ProjectionOperation projectionOperation = getProjectionOperation();

		return reactiveMongoTemplate.aggregate(
				Aggregation.newAggregation(matchOperation, groupOperation, projectionOperation), MdbPerson.class,
				AggrResult.class);
	}

	private MatchOperation getMatchOperation(int minAge, int maxAge) {
		Criteria ageCriteria = where("age").gt(minAge).andOperator(where("age").lt(maxAge));
		return match(ageCriteria);
	}

	private GroupOperation getGroupOperation() {
		return group("age")
				.first("age").as("age")
				.addToSet("address.city").as("cities")
				.count().as("count");
	}

	private ProjectionOperation getProjectionOperation() {
		return project("age", "cities", "count");
	}

}
