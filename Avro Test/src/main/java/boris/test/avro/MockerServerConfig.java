package boris.test.avro;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class MockerServerConfig {

	@Bean
	public RouterFunction<ServerResponse> mockRouterFunction(MockServerHandler mockServerHandler) {
		return route(GET("/mock/{id}"), mockServerHandler::getMockResponse);
	}
}
