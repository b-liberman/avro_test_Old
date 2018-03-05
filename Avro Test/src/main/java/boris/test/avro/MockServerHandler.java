package boris.test.avro;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class MockServerHandler {

	public Mono<ServerResponse> getMockResponse(ServerRequest request) {
		Mono<String> string = request.bodyToMono(String.class);
		String id = request.pathVariable("id");
		return ServerResponse.ok().body(fromObject("response from the mock server: " + id + " " + string.block()));
	}

}
