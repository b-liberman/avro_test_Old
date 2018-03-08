package boris.test.avro;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import boris.test.mdb.domain.MdbPersonRepository;
import reactor.core.publisher.Mono;

@Component
public class MockServerHandler {

	@Autowired
	private MdbPersonRepository mdbPersonRepository;

	public Mono<ServerResponse> getMockResponse(ServerRequest request) {
		Mono<String> string = request.bodyToMono(String.class);
		String id = request.pathVariable("id");

		mdbPersonRepository.findByAddressStreetaddress("street123").count()
				.subscribe(l -> System.out.println("SERVER: FOUND " + l + " ENTRIES"));

		return ServerResponse.ok().body(fromObject("response from the mock server: " + id + " " + string.block()));
	}
}
