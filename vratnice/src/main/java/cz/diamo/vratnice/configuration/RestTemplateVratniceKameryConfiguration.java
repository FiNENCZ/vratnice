package cz.diamo.vratnice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateVratniceKameryConfiguration {

    @Bean
    public RestTemplate restVratniceKameryTemplate() {
        return new RestTemplate();
    }

}
