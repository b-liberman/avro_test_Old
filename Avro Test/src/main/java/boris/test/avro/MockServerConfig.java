package boris.test.avro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;

@Configuration
public class MockServerConfig {

//	@Bean
//	public RouterFunction<ServerResponse> mockRouterFunction(MockServerHandler mockServerHandler) {
//		return route(GET("/mock/{id}"), mockServerHandler::getMockResponse).andRoute(POST("/mock/{id}"),
//				mockServerHandler::postMockResponse);
//	}
	
	static int PORT = 8082;

	@Bean
	public Vertx vertx() {
		return Vertx.vertx();
	}
	
	@Bean 
	public HttpServer httpServer() {
		return vertx().createHttpServer();
	}
	
	@Bean
	public Router router() {
		return Router.router(vertx());
	}
	
	@Bean
	@DependsOn("mdbPersonRepository")
	public MockServerVerticle mockServerVerticle() {
		return new MockServerVerticle(httpServer(), router());
	}
}
