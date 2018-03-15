package boris.test.avro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import boris.test.mdb.domain.MdbPersonRepository;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class MockServerVerticle extends AbstractVerticle {

	private HttpServer httpServer;
	private Router router;

	public MockServerVerticle(HttpServer httpServer, Router router) {
		super();
		this.httpServer = httpServer;
		this.router = router;
	}

	@Autowired
	private MdbPersonRepository mdbPersonRepository;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		router.route().handler(BodyHandler.create());

		router.post("/mock/:id").handler(rc -> {
			String id = rc.request().getParam("id");
			String body = rc.getBodyAsString();
			log.debug("body: " + body);
			mdbPersonRepository.findByAddressStreetaddress("street123").count()
					.subscribe(l -> log.debug(": FOUND " + l + " ENTRIES"));
			
			mdbPersonRepository.aggregate(36, 43).subscribe(aggr -> {
				System.out.println("\n===================================");
				System.out.println(aggr.getAge() + ":" + aggr.getCities() + ":" + aggr.getCount());
				System.out.println("\n===================================");
			});
			
			rc.response().end("response form V-Server: processed the body: \n" + body);
		});
		httpServer.requestHandler(router::accept).rxListen(MockServerConfig.PORT).subscribe(hs -> startFuture.complete());
	}
}
