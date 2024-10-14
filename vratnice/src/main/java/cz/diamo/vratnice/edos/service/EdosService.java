package cz.diamo.vratnice.edos.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import com.google.gson.Gson;

import cz.diamo.share.annotation.TransactionalRO;
import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.security.AuthCookieDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.security.SecurityUtils;
import cz.diamo.share.services.AuthServices;
import cz.diamo.vratnice.edos.dto.RucniZaznamSnimaceDto;
import cz.diamo.vratnice.edos.dto.SnimacAkceVratniceDto;
import cz.diamo.vratnice.edos.dto.SnimacVratniceDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
@TransactionalROE
public class EdosService {

    final static Logger logger = LogManager.getLogger(EdosService.class);

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RestOperations restEdos;

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
            AccessTokenResponse accessTokenResponse = authServices
                    .refreshToken(authCookieDto.getRefreshToken());
            Cookie edosCookie = securityUtils.generateAuthCookieKeyCloak(accessTokenResponse, "edos_auth");
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
                    .exchange("/vratnice/zastup?sapIdZastup={sapIdZastup}", HttpMethod.POST,
                            requestEntity, Void.class,
                            params);

            if (result.getStatusCode().isError())
                logger.error(result.getStatusCode());

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public ResponseEntity<List<SnimacVratniceDto>> listSnimac(AppUserDto appUserDto, HttpServletRequest request) throws BaseException {
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<Void> requestEntity = new HttpEntity<>(getEdosHttpHeaders(request));

            ResponseEntity<List<SnimacVratniceDto>> result = restEdos.exchange(
                    "/vratnice/snimac/list", 
                    HttpMethod.GET, 
                    requestEntity, 
                    new ParameterizedTypeReference<List<SnimacVratniceDto>>() {}, 
                    new HashMap<>()
            );


            if (result.getStatusCode().isError())
                throw new BaseException(
                        String.format(messageSource.getMessage("edos.error", null, LocaleContextHolder.getLocale()),
                                result.getStatusCode()));

            return result;

        } catch (HttpClientErrorException he) {
            try {
                JSONObject obj = new JSONObject(new String(he.getResponseBodyAsByteArray(), StandardCharsets.UTF_8));
                throw new BaseException(obj.getString("message"));
            } catch (JSONException e) {
                throw new BaseException(he.getMessage());
            }
        } catch (BaseException be) {
            logger.error(be);
            throw be;
        } catch (Exception e) {
            throw new BaseException(
                    messageSource.getMessage("edos.nelze.spojit", null, LocaleContextHolder.getLocale()));
        }
    }

    public ResponseEntity<List<SnimacAkceVratniceDto>> listSnimacAkce(AppUserDto appUserDto, HttpServletRequest request) throws BaseException {
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<Void> requestEntity = new HttpEntity<>(getEdosHttpHeaders(request));

            ResponseEntity<List<SnimacAkceVratniceDto>> result = restEdos.exchange(
                    "/vratnice/snimac-akce/list", 
                    HttpMethod.GET, 
                    requestEntity, 
                    new ParameterizedTypeReference<List<SnimacAkceVratniceDto>>() {}, 
                    new HashMap<>()
            );


            if (result.getStatusCode().isError())
                throw new BaseException(
                        String.format(messageSource.getMessage("edos.error", null, LocaleContextHolder.getLocale()),
                                result.getStatusCode()));

            return result;

        } catch (HttpClientErrorException he) {
            try {
                JSONObject obj = new JSONObject(new String(he.getResponseBodyAsByteArray(), StandardCharsets.UTF_8));
                throw new BaseException(obj.getString("message"));
            } catch (JSONException e) {
                throw new BaseException(he.getMessage());
            }
        } catch (BaseException be) {
            logger.error(be);
            throw be;
        } catch (Exception e) {
            logger.error(e);
            throw new BaseException(
                    messageSource.getMessage("edos.nelze.spojit", null, LocaleContextHolder.getLocale()));
        }
    }


    public void zaznamSnimaceSave(AppUserDto appUserDto, HttpServletRequest request, RucniZaznamSnimaceDto detail) throws BaseException, JSONException {
        // volání aplikace Žádosti
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<RucniZaznamSnimaceDto> requestEntity = new HttpEntity<RucniZaznamSnimaceDto>(
                detail,
                getEdosHttpHeaders(request));

            ResponseEntity<Void> result = restEdos
                    .exchange("/vratnice/zaznam-snimace/save", HttpMethod.POST,
                            requestEntity, Void.class,
                            new HashMap<>());

            if (result.getStatusCode().isError())
                throw new BaseException(
                        String.format(messageSource.getMessage("edos.error", null, LocaleContextHolder.getLocale()),
                                result.getStatusCode()));

            return;

        } catch (HttpClientErrorException he) {
            try {
                JSONObject obj = new JSONObject(new String(he.getResponseBodyAsByteArray(), StandardCharsets.UTF_8));
                throw new BaseException(obj.getString("message"));
            } catch (JSONException e) {
                throw new BaseException(he.getMessage());
            }
        } catch (BaseException be) {
            logger.error(be);
            throw be;
        } catch (Exception e) {
            logger.error(e);
            logger.info("-------");
            logger.info(e);

            throw new BaseException(extractErrorMessageFromRest(e.getMessage())); 

        }
    }

    public String extractErrorMessageFromRest(String errorMessage) throws JSONException {
        try {
            if (errorMessage != null) {
                // Pokud zpráva začíná číslem (jako HTTP status), oddělí se až JSON část
                int jsonStartIndex = errorMessage.indexOf("{");
                if (jsonStartIndex != -1) {
                    String jsonPart = errorMessage.substring(jsonStartIndex);
                    JSONObject json = new JSONObject(jsonPart);
                    String message = json.optString("message", "Chyba zpráva nenalezena");
                    
                    // Pokud existuje message v JSON
                    if (message != null && !message.isEmpty()) {
                        return message;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Chyba při parsování JSON z e.getMessage(): ", ex);
        }

        return messageSource.getMessage("edos.nelze.spojit", null, LocaleContextHolder.getLocale());
    }
}
