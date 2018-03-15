package boris.test.mdb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import boris.test.avro.domain.Person;

@Document(collection = "TestPersonsN")
public class MdbPerson extends Person {

	@Id
	public String id;

	public MdbPerson() {
		super();
	}

	public MdbPerson(Integer age, String name, java.util.List<String> tags, String optField, String newOptField,
			boris.test.avro.domain.AddressUSRecord address) {
		super(age, name, tags, optField, newOptField, address);
	}

	public MdbPerson(Person person) {
		super(person.age, person.name, person.tags, person.optField, person.newOptField, person.address);
	}
}
