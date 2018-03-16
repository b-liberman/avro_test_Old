package boris.test.avro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;

//@Component
public class VertxAppRunner implements ApplicationRunner {

	@Autowired
	private Vertx vertx;

	@Autowired
	private MockServerVerticle mockServerVerticle;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.debug("deploying the verticle");
		RxHelper.deployVerticle(vertx, mockServerVerticle).subscribe(str -> log.debug("deployed verticle " + str),
				err -> log.debug("failed to deploy the verticle " + err.getMessage()));
	}

}
