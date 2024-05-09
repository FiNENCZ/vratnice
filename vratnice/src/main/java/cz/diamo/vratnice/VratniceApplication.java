package cz.diamo.vratnice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@ComponentScan(basePackages = { "cz.diamo.share", "cz.diamo.vratnice" })
@EnableJpaRepositories(basePackages = { "cz.diamo.share.repository", "cz.diamo.vratnice.repository" })
@EntityScan(basePackages = { "cz.diamo.share.entity", "cz.diamo.vratnice.entity" })
@EnableMethodSecurity(prePostEnabled = true)
public class VratniceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VratniceApplication.class, args);
	}

}
