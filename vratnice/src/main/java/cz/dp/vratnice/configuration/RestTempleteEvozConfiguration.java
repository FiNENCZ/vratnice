package cz.dp.vratnice.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.DefaultUriBuilderFactory;

import cz.dp.share.configuration.AppProperties;

@Configuration
public class RestTempleteEvozConfiguration {

    @Bean
    RestOperations restZadosti(RestTemplateBuilder restTemplateBuilder, AppProperties appProperties) {
        if (StringUtils.isNotBlank(appProperties.getZadostiApiUrl()))
            return restTemplateBuilder
                    .uriTemplateHandler(new DefaultUriBuilderFactory(appProperties.getZadostiApiUrl()))
                    .messageConverters(new MappingJackson2HttpMessageConverter()).build();
        else
            return restTemplateBuilder.build();
    }

}
