package cz.diamo.share.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTempleteConfiguration {

    // @Bean
    // public RestTemplate getRestTemplate() {

    // RestTemplate restTemplate = new RestTemplate();
    // return restTemplate;
    // }

    @Bean
    RestOperations restKeyCloak(RestTemplateBuilder restTemplateBuilder, AppProperties appProperties) {
        return restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(appProperties.getKeycloakUrl()))
                .messageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Bean
    RestOperations restAvizace(RestTemplateBuilder restTemplateBuilder, AppProperties appProperties) {
        if (!StringUtils.isBlank(appProperties.getAvizaceUrl()))
            return restTemplateBuilder
                    .uriTemplateHandler(new DefaultUriBuilderFactory(appProperties.getAvizaceUrl()))
                    .messageConverters(new MappingJackson2HttpMessageConverter()).build();
        else
            return restTemplateBuilder.build();
    }

    @Bean
    RestOperations restWso2(RestTemplateBuilder restTemplateBuilder, AppProperties appProperties) {
        if (!StringUtils.isBlank(appProperties.getWso2Url())) {
            return restTemplateBuilder
                    .uriTemplateHandler(new DefaultUriBuilderFactory(appProperties.getWso2Url()))
                    .messageConverters(new MappingJackson2HttpMessageConverter()).build();
        } else
            return restTemplateBuilder.build();

    }
}
