package boris.test.avro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.ipc.netty.http.server.HttpServer;

//@Component
public class MockServer implements ApplicationRunner {

	static final int PORT = 8246;

	@Autowired
	RouterFunction<ServerResponse> mockRouterFunction;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(mockRouterFunction);
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer server = HttpServer.create(PORT);
		server.newHandler(adapter).block();
	}

}
