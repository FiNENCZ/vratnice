package cz.dp.vratnice.edos.service;

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

import cz.dp.share.annotation.TransactionalRO;
import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.security.AuthCookieDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.repository.UzivatelRepository;
import cz.dp.share.security.SecurityUtils;
import cz.dp.share.services.AuthServices;
import cz.dp.vratnice.edos.dto.RucniZaznamSnimaceDto;
import cz.dp.vratnice.edos.dto.SnimacAkceVratniceDto;
import cz.dp.vratnice.edos.dto.SnimacVratniceDto;
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

    /**
     * Získává HTTP hlavičky pro autentizaci pomocí tokenu z cookie.
     * 
     * @param request HTTP požadavek, ze kterého se získávají cookies.
     * @return {@link HttpHeaders} obsahující cookie pro autentizaci, nebo null,
     *         pokud
     *         nebyla nalezena platná cookie nebo došlo k chybě.
     */
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

    /**
     * Nastavuje zástupce pro uživatele na základě poskytnutých informací.
     *
     * @param request    HTTP požadavek, který obsahuje informace o uživatelském
     *                   kontextu.
     * @param appUserDto Objekt {@link AppUserDto}, který obsahuje informace o
     *                   uživateli
     *                   a jeho zástupci.
     */
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

    /**
     * Vrací seznam snímačů vratnic pro daného uživatele.
     *
     * @param appUserDto Objekt {@link AppUserDto}, který obsahuje informace o
     *                   uživateli.
     * @param request    HTTP požadavek, který obsahuje informace o uživatelském
     *                   kontextu.
     * @return {@link ResponseEntity} obsahující seznam {@link SnimacVratniceDto}
     *         snímačů.
     * @throws BaseException Pokud dojde k chybě při získávání seznamu snímačů
     *                       nebo při volání externí služby.
     */
    public ResponseEntity<List<SnimacVratniceDto>> listSnimac(AppUserDto appUserDto, HttpServletRequest request)
            throws BaseException {
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<Void> requestEntity = new HttpEntity<>(getEdosHttpHeaders(request));

            ResponseEntity<List<SnimacVratniceDto>> result = restEdos.exchange(
                    "/vratnice/snimac/list",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<SnimacVratniceDto>>() {
                    },
                    new HashMap<>());

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

    /**
     * Vrací seznam akcí snímačů vratnic pro daného uživatele.
     *
     * @param appUserDto Objekt {@link AppUserDto}, který obsahuje informace o
     *                   uživateli.
     * @param request    HTTP požadavek, který obsahuje informace o uživatelském
     *                   kontextu.
     * @return {@link ResponseEntity} obsahující seznam
     *         {@link SnimacAkceVratniceDto} akcí snímačů.
     * @throws BaseException Pokud dojde k chybě při získávání seznamu akcí snímačů
     *                       nebo při volání externí služby.
     */
    public ResponseEntity<List<SnimacAkceVratniceDto>> listSnimacAkce(AppUserDto appUserDto, HttpServletRequest request)
            throws BaseException {
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<Void> requestEntity = new HttpEntity<>(getEdosHttpHeaders(request));

            ResponseEntity<List<SnimacAkceVratniceDto>> result = restEdos.exchange(
                    "/vratnice/snimac-akce/list",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<SnimacAkceVratniceDto>>() {
                    },
                    new HashMap<>());

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

    /**
     * Ukládá ruční záznam snímače vratnice pro daného uživatele.
     *
     * @param appUserDto Objekt {@link AppUserDto}, který obsahuje informace o
     *                   uživateli.
     * @param request    HTTP požadavek, který obsahuje informace o uživatelském
     *                   kontextu.
     * @param detail     Objekt {@link RucniZaznamSnimaceDto}, který obsahuje
     *                   detaily záznamu snímače, který se má uložit.
     * @throws BaseException Pokud dojde k chybě při ukládání záznamu snímače
     *                       nebo při volání externí služby.
     * @throws JSONException Pokud dojde k chybě při zpracování JSON odpovědi.
     */
    public void zaznamSnimaceSave(AppUserDto appUserDto, HttpServletRequest request, RucniZaznamSnimaceDto detail)
            throws BaseException, JSONException {
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

    /**
     * Extrahuje chybovou zprávu z REST odpovědi.
     *
     * @param errorMessage Chybová zpráva, která se má zpracovat.
     * @return Extrahovaná chybová zpráva nebo výchozí chybová zpráva.
     * @throws JSONException Pokud dojde k chybě při parsování JSON.
     */
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
