package cz.diamo.share.configuration;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.google.gson.Gson;

import cz.diamo.share.dto.security.AuthCookieDto;
import cz.diamo.share.repository.UzivatelModulRepository;
import cz.diamo.share.repository.UzivatelOpravneniRepository;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.repository.UzivatelZavodRepository;
import cz.diamo.share.security.AuthFilter;
import cz.diamo.share.security.SecurityUtils;
import cz.diamo.share.services.AuthServices;
import cz.diamo.share.services.ZastupServices;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@Order(1)
// @EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfigApi {

	@Autowired
	private UzivatelRepository uzivatelRepository;

	@Autowired
	private AuthServices authServices;

	@Autowired
	private UzivatelOpravneniRepository uzivatelOpravneniRepository;

	@Autowired
	private UzivatelZavodRepository uzivatelZavodRepository;

	@Autowired
	private UzivatelModulRepository uzivatelModulRepository;

	@Autowired
	private ZastupServices zastupServices;

	@Autowired
	private SecurityUtils securityUtils;

	@Bean
	public CustomLogoutSuccessHandler logoutSuccessHandler() {
		return new CustomLogoutSuccessHandler();
	}

	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return authentication -> {
			throw new AuthenticationServiceException("Cannot authenticate " + authentication);
		};
	}

	@Bean
	public AuthFilter authCookieFilter() {
		return new AuthFilter(securityUtils, uzivatelRepository, authServices, uzivatelOpravneniRepository,
				uzivatelZavodRepository, uzivatelModulRepository, zastupServices);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.sessionManagement(cust -> cust.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.headers(cust -> cust.contentSecurityPolicy(
						t -> t.policyDirectives("script-src 'self'; object-src'none'; base-uri 'self'")))
				.csrf(cust -> cust.disable()).logout(cust -> {
					cust.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(Directive.ALL)));
					cust.logoutSuccessHandler(logoutSuccessHandler());
					cust.logoutRequestMatcher(new AntPathRequestMatcher("/**/logout"));
					cust.deleteCookies(SecurityUtils.cookieName);
				}).securityMatcher("/api/**").authorizeHttpRequests(cust -> {
					cust.requestMatchers("/login", "/*/login", "/login-test", "/*/login-test", "/login-sso-complete",
							"/*/login-sso-complete", "/login-token", "/*/login-token", "/verify", "/*/verify",
							"/api/public/**", "/api/test/**",
							"/*/konfigurace", "/*/main/zadost-reset-hesla",
							"/*/main/reset-hesla", "/v3/api-docs", "/v3/api-docs/*", "/v3/swagger-ui.html",
							"/v3/swagger-ui/*", "/api/wsapi/*", "/api/wsapi/**").permitAll()
							.anyRequest().authenticated();
				})
				.exceptionHandling(
						cust -> cust.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.addFilterAfter(authCookieFilter(), ConcurrentSessionFilter.class);
		return http.build();
	}

	private class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

		@Override
		public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException {

			if (request.getCookies() != null) {
				for (Cookie cookie : request.getCookies()) {
					if (cookie.getName().equals(SecurityUtils.cookieName)) {

						AuthCookieDto authCookieDto = new Gson().fromJson(
								new String(Base64.getDecoder().decode(cookie.getValue())),
								AuthCookieDto.class);
						authServices.logout(request, authCookieDto.getRefreshToken());

						Cookie cookieNew = new Cookie(SecurityUtils.cookieName, null);
						cookieNew.setMaxAge(0);
						cookieNew.setHttpOnly(true);
						cookieNew.setPath("/");

						// add cookie to response
						response.addCookie(cookieNew);

						Cookie cookieExp = new Cookie(SecurityUtils.cookieNameExp, null);
						cookieExp.setMaxAge(0);
						cookieExp.setHttpOnly(false);
						cookieExp.setPath("/");

						// add cookie to response
						response.addCookie(cookieExp);
					}
				}
			}

			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().flush();
		}

	}
}