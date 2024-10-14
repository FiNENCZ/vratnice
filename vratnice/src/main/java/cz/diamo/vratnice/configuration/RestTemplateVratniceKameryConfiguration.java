package cz.diamo.vratnice.configuration;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateVratniceKameryConfiguration {

    @Bean
    public RestTemplate restVratniceKameryTemplate(RestTemplateBuilder restTemplateBuilder) {
        // nastavení timeout
        return restTemplateBuilder 
            .setConnectTimeout(Duration.ofSeconds(3))
            .setReadTimeout(Duration.ofSeconds(3))
            .build();
    }

}
