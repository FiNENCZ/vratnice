package cz.diamo.share.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import cz.diamo.share.constants.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Pro flyway zde probíhá specifikace cesty k migracím na základě driveru databáze.
 * <p>
 * Pro spuštění potřebuje být specifikován v resources/META-INF/spring.factories
 *
 */
@Order
public class PropertiesPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> database = new HashMap<>();
        var databaseUrl = environment.getRequiredProperty("spring.datasource.url");
        if (databaseUrl.contains("postgresql")) {
            database.put("spring.datasource.driver-class-name", "org.postgresql.Driver");
            database.put("spring.flyway.enabled",true);
            database.put("spring.flyway.schemas", Constants.SCHEMA);
            database.put("spring.flyway.baselineOnMigrate",true); 
            database.put("spring.flyway.locations", "classpath:cz/diamo/" +Constants.BASE_PACKAGE+ "/repository/migration/structure");  
        } else {
            throw new RuntimeException("Unsupported database driver");
        }
        environment.getPropertySources().addFirst(new MapPropertySource("database", database));
       
        
    }

}