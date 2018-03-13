package boris.test.mdb.domain;

import java.util.List;

import lombok.Data;

@Data
public class AggrResult {
	private int age;
	private List<String> cities;
	private int count;
}
