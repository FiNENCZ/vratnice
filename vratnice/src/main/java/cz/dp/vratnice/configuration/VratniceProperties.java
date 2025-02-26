package cz.dp.vratnice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@ConfigurationProperties(prefix = "vratnice")
@Component
@Validated
@Data
public class VratniceProperties {

    private String zavodNazev;

    private Integer dobaPouceni;

    private String barvaVyprseniPouceni;

    private String barvaNepovoleneSpz;

    private String barvaPovoleneSpz;

    private String barvaSluzebnihoVozidla;

    private String ngServerUrl;
    

}
