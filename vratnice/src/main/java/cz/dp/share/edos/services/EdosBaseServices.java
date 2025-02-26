package cz.dp.share.edos.services;

import java.util.Base64;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import com.google.gson.Gson;

import cz.dp.share.annotation.TransactionalRO;
import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.configuration.AppProperties;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.security.AuthCookieDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.repository.UzivatelRepository;
import cz.dp.share.security.SecurityUtils;
import cz.dp.share.services.AuthServices;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
@TransactionalROE
public class EdosBaseServices {

    final static Logger logger = LogManager.getLogger(EdosBaseServices.class);

    @Autowired
    private AuthServices authServices;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    protected RestOperations restEdos;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AppProperties appProperties;

    @TransactionalROE
    public HttpHeaders getEdosHttpHeaders(HttpServletRequest request) {

        AuthCookieDto authCookieDto = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(SecurityUtils.cookieName)) {
                    authCookieDto = new Gson().fromJson(new String(Base64.getDecoder().decode(cookie.getValue())),
                            AuthCookieDto.class);
                    break;
                }
            }
        }

        if (authCookieDto != null) {
            Cookie edosCookie = null;
            if (securityUtils.getClientId(authCookieDto.getRefreshToken())
                    .equals(appProperties.getKeycloakClientId())) {
                AccessTokenResponse accessTokenResponse = authServices
                        .refreshToken(authCookieDto.getRefreshToken());
                edosCookie = securityUtils.generateAuthCookieKeyCloak(accessTokenResponse, "edos_auth");
            } else {
                edosCookie = securityUtils.generateAuthCookieKeyCloak(authCookieDto, "edos_auth");
            }
            if (edosCookie != null) {

                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", edosCookie.getName() + "=" + edosCookie.getValue());
                return requestHeaders;
            }
        }
        return null;
    }

    @TransactionalRO
    public void nastavitZastup(HttpServletRequest request, AppUserDto appUserDto) {

        // volání EDOS
        try {

            String sapIdZastupu = "";
            if (appUserDto.getZastup() != null) {
                Uzivatel uzivatel = uzivatelRepository.getDetail(appUserDto.getZastup().getIdUzivatel());
                if (uzivatel != null)
                    sapIdZastupu = uzivatel.getSapId();
            }

            HttpEntity<Void> requestEntity = new HttpEntity<Void>(null,
                    getEdosHttpHeaders(request));

            HashMap<String, String> params = new HashMap<>();
            params.put("sapIdZastup", sapIdZastupu);

            ResponseEntity<Void> result = restEdos
                    .exchange("/zadosti/zastup?sapIdZastup={sapIdZastup}", HttpMethod.POST,
                            requestEntity, Void.class,
                            params);

            if (result.getStatusCode().isError())
                logger.error(result.getStatusCode());

        } catch (Exception e) {
            logger.error(e);
        }
    }

}
