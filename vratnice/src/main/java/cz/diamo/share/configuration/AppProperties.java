package cz.diamo.share.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import cz.diamo.share.enums.ColorSchemeEnum;
import lombok.Data;

@ConfigurationProperties(prefix = "app")
@Component
@Validated
@Data
public class AppProperties {

    private ColorSchemeEnum colorScheme;

    private String keycloakUrl;

    private String keycloakClientId;

    private String keycloakSecret;

    private String keycloakRealm;

    private String keycloakSigningKeyAlgType;

    private String keycloakSigningPublicKey;

    private String portalUrl;

    private String edosApiUrl;

    private String evozApiUrl;

    private String zadostiApiUrl;

    private String wso2Url;

    private String avizaceUrl;

    private String jasperServerUrl;

    private String jasperServerJmeno;

    private String jasperServerHeslo;
}
