package cz.diamo.share.configuration;

import org.apache.commons.lang3.StringUtils;
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

    private String s3StorageUrl;

    private String s3AccessKeyId;

    private String s3SecretAccessKey;

    private String s3Region;
    
    private String s3Bucket;

    public boolean isS3Set() {
        return StringUtils.isNotBlank(s3StorageUrl) && StringUtils.isNotBlank(s3AccessKeyId) && 
        StringUtils.isNotBlank(s3SecretAccessKey) && StringUtils.isNotBlank(s3Region) &&
        StringUtils.isNotBlank(s3Bucket);
    }
}
