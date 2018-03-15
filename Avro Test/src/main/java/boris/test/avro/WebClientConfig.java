package boris.test.avro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

	@Bean
	public WebClient mockWebClient() {
		return WebClient.create("http://localhost:" + MockServerConfig.PORT);
	}
}
