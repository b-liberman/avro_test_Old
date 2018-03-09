package boris.test.avro;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public Mono<ServerResponse> getMockResponse(ServerRequest request) {
		String id = request.pathVariable("id");

		mdbPersonRepository.findByAddressStreetaddress("street123").count()
				.subscribe(l -> log.debug("FOUND " + l + " ENTRIES"));

		return ServerResponse.ok().body(fromObject("response from the mock server: " + id));
	}

	public Mono<ServerResponse> postMockResponse(ServerRequest request) {
		log.debug("getting request body");
		String requestBody = "LA LA LA LA";
		request.body(toMono(String.class)).subscribe(str -> log.debug(str));
		log.debug("request body is: " + requestBody);
		String id = request.pathVariable("id");

		mdbPersonRepository.findByAddressStreetaddress("street123").count()
				.subscribe(l -> log.debug(": FOUND " + l + " ENTRIES"));

		return ServerResponse.ok()
				.body(fromObject("response from the mock server: " + id + "\n" + requestBody));
	}
}
