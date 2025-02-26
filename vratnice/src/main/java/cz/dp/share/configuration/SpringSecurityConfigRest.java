package cz.dp.share.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import cz.dp.share.constants.Constants;

@Configuration
@Order(2)
public class SpringSecurityConfigRest {

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder()).dataSource(dataSource)
				.authoritiesByUsernameQuery(
						"select u.username, a.authority from " + Constants.SCHEMA + ".externi_uzivatel u, "
								+ Constants.SCHEMA + ".externi_role a, " + Constants.SCHEMA
								+ ".externi_uzivatel_role v where u.id_externi_uzivatel = v.id_externi_uzivatel and a.authority = v.authority and u.username = ?")
				.usersByUsernameQuery(
						"select username, password, 1 from " + Constants.SCHEMA
								+ ".externi_uzivatel where upper(username) = upper(?) and aktivita = true");
	}

	@Bean
	public SecurityFilterChain securityFilterChainRest(HttpSecurity http) throws Exception {
		http.securityMatcher("/rest/**")
				.authorizeHttpRequests(t -> t.anyRequest().authenticated())
				.httpBasic(t -> t.toString()).csrf(t -> t.disable());
		return http.build();
	}
}