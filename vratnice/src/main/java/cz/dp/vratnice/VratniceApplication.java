package cz.dp.vratnice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@ComponentScan(basePackages = { "cz.dp.share", "cz.dp.vratnice" })
@EnableJpaRepositories(basePackages = { "cz.dp.share.repository", "cz.dp.vratnice.repository" })
@EntityScan(basePackages = { "cz.dp.share.entity", "cz.dp.vratnice.entity" })
@EnableMethodSecurity(prePostEnabled = true)
public class VratniceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VratniceApplication.class, args);
	}

}
