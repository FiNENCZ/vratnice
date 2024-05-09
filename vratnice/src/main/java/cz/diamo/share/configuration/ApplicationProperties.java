package cz.diamo.share.configuration;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@Validated
@ConfigurationProperties
public class ApplicationProperties {

	@NotEmpty
	@Value("${application.version}")
	private String version;

	@NotEmpty
	@Value("${application.description}")
	private String description;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

