package cz.diamo.share.services;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import com.google.gson.Gson;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.configuration.AppProperties;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.OznameniDto;
import cz.diamo.share.dto.avizace.AvizaceRequestDto;
import cz.diamo.share.dto.security.AuthCookieDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.share.security.SecurityUtils;
import io.sentry.Sentry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class OznameniServices {

        @Autowired
        private AuthServices authServices;

        @Autowired
        private SecurityUtils securityUtils;

        @Autowired
        private AppProperties appProperties;

        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        private RestOperations restAvizace;

        @Autowired
        private MessageSource messageSource;

        @TransactionalROE
        public List<OznameniDto> list(HttpServletRequest httpRequest, AppUserDto appUserDto)
                        throws NoSuchMessageException, BaseException {
                // kontrola URL
                if (StringUtils.isBlank(appProperties.getAvizaceUrl()))
                        throw new ValidationException(
                                        messageSource.getMessage("url.avizace.null", null,
                                                        LocaleContextHolder.getLocale()));
                HttpEntity<Void> requestEntity = new HttpEntity<Void>(null,
                                getAvizaceHttpHeaders(httpRequest));

                ResponseEntity<OznameniDto[]> result = restAvizace
                                .exchange("/avizace/oznameni/list", HttpMethod.GET,
                                                requestEntity, OznameniDto[].class,
                                                new HashMap<>());

                if (result.getStatusCode().isError())
                        throw new BaseException(
                                        String.format(
                                                        messageSource.getMessage("volani.avizace.error", null,
                                                                        LocaleContextHolder.getLocale()),
                                                        result.getStatusCode()));
                if (result.getBody() != null)
                        return Arrays.asList(result.getBody());
                else
                        return null;
        }

        public void precteno(HttpServletRequest httpRequest, AppUserDto appUserDto, String idOznameni)
                        throws NoSuchMessageException, BaseException {
                // kontrola URL
                if (StringUtils.isBlank(appProperties.getAvizaceUrl()))
                        throw new ValidationException(
                                        messageSource.getMessage("url.avizace.null", null,
                                                        LocaleContextHolder.getLocale()));
                HttpEntity<Void> requestEntity = new HttpEntity<Void>(null,
                                getAvizaceHttpHeaders(httpRequest));

                HashMap<String, String> params = new HashMap<>();
                params.put("id", idOznameni);

                ResponseEntity<Void> result = restAvizace
                                .exchange("/avizace/oznameni/precteno?id={id}", HttpMethod.POST,
                                                requestEntity, Void.class,
                                                params);

                if (result.getStatusCode().isError())
                        throw new BaseException(
                                        String.format(
                                                        messageSource.getMessage("volani.avizace.error", null,
                                                                        LocaleContextHolder.getLocale()),
                                                        result.getStatusCode()));
        }

        public void odstranit(HttpServletRequest httpRequest, AppUserDto appUserDto, String idOznameni)
                        throws NoSuchMessageException, BaseException {
                // kontrola URL
                if (StringUtils.isBlank(appProperties.getAvizaceUrl()))
                        throw new ValidationException(
                                        messageSource.getMessage("url.avizace.null", null,
                                                        LocaleContextHolder.getLocale()));
                HttpEntity<Void> requestEntity = new HttpEntity<Void>(null,
                                getAvizaceHttpHeaders(httpRequest));

                HashMap<String, String> params = new HashMap<>();
                params.put("id", idOznameni);

                ResponseEntity<Void> result = restAvizace
                                .exchange("/avizace/oznameni/odstranit?id={id}", HttpMethod.POST,
                                                requestEntity, Void.class,
                                                params);

                if (result.getStatusCode().isError())
                        throw new BaseException(
                                        String.format(
                                                        messageSource.getMessage("volani.avizace.error", null,
                                                                        LocaleContextHolder.getLocale()),
                                                        result.getStatusCode()));
        }

        public void save(AvizaceRequestDto request, HttpServletRequest httpRequest)
                        throws NoSuchMessageException,
                        BaseException {

                try {
                        // kontrola URL
                        if (StringUtils.isBlank(appProperties.getAvizaceUrl()))
                                throw new ValidationException(
                                                messageSource.getMessage("url.avizace.null", null,
                                                                LocaleContextHolder.getLocale()));

                        String url = "/avizace/save";
                        HttpHeaders httpHeaders = getAvizaceHttpHeaders(httpRequest);
                        if (httpHeaders == null) {
                                httpHeaders = getAvizaceHttpHeadersExterniUzivatel(httpRequest);
                                if (httpHeaders != null)
                                        url = "/rest/save";
                        }

                        HttpEntity<AvizaceRequestDto> requestEntity = new HttpEntity<AvizaceRequestDto>(request,
                                        httpHeaders);

                        ResponseEntity<Void> result = restAvizace
                                        .exchange(url, HttpMethod.POST,
                                                        requestEntity, Void.class,
                                                        new HashMap<>());

                        if (result.getStatusCode().isError())
                                throw new BaseException(
                                                String.format(
                                                                messageSource.getMessage("volani.avizace.error", null,
                                                                                LocaleContextHolder.getLocale()),
                                                                result.getStatusCode()));
                } catch (Exception ex) {
                        Sentry.captureException(ex);
                        throw ex;
                }
        }

        public HttpHeaders getAvizaceHttpHeaders(HttpServletRequest request) {

                AuthCookieDto authCookieDto = null;
                if (request.getCookies() != null) {
                        for (Cookie cookie : request.getCookies()) {
                                if (cookie.getName().equals(SecurityUtils.cookieName)) {
                                        authCookieDto = new Gson().fromJson(
                                                        new String(Base64.getDecoder().decode(cookie.getValue())),
                                                        AuthCookieDto.class);
                                        break;
                                }
                        }
                }

                if (authCookieDto != null) {

                        Cookie avizaceCookie = null;
                        if (securityUtils.getClientId(authCookieDto.getRefreshToken())
                                        .equals(appProperties.getKeycloakClientId())) {
                                AccessTokenResponse accessTokenResponse = authServices
                                                .refreshToken(authCookieDto.getRefreshToken());
                                avizaceCookie = securityUtils.generateAuthCookieKeyCloak(accessTokenResponse,
                                                "avizace_auth");
                        } else {
                                avizaceCookie = securityUtils.generateAuthCookieKeyCloak(authCookieDto, "avizace_auth");
                        }
                        if (avizaceCookie != null) {

                                HttpHeaders requestHeaders = new HttpHeaders();
                                requestHeaders.add("Cookie", avizaceCookie.getName() + "=" + avizaceCookie.getValue());
                                return requestHeaders;
                        }
                }
                return null;
        }

        public HttpHeaders getAvizaceHttpHeadersExterniUzivatel(HttpServletRequest request) {

                if (StringUtils.isBlank(request.getHeader("Authorization")))
                        return null;

                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.set("Authorization", request.getHeader("Authorization"));
                return requestHeaders;
        }
}
