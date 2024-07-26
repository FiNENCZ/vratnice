package cz.diamo.vratnice.zadosti.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import com.google.gson.Gson;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.BudovaDto;
import cz.diamo.share.dto.LokalitaDto;
import cz.diamo.share.dto.security.AuthCookieDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.security.SecurityUtils;
import cz.diamo.share.services.AuthServices;
import cz.diamo.share.services.ZadostiExterniServices;
import cz.diamo.vratnice.dto.KlicDto;
import cz.diamo.vratnice.dto.PoschodiDto;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.Poschodi;
import cz.diamo.vratnice.repository.PoschodiRepository;
import cz.diamo.vratnice.service.KlicService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
@TransactionalROE
public class ZadostiServices extends ZadostiExterniServices {

    final static Logger logger = LogManager.getLogger(ZadostiServices.class);

    @Autowired
    private PoschodiRepository poschodiRepository;

    @Autowired
    private KlicService klicService;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private RestOperations restZadosti;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private MessageSource messageSource;

    public List<PoschodiDto> seznamPoschodi(String idBudova, AppUserDto appUserDto) throws BaseException {
        List<Poschodi> poschodiList = poschodiRepository.getList(idBudova, true);

        List<PoschodiDto> poschodiDtos = new ArrayList<PoschodiDto>(poschodiList.size());
        for (Poschodi poschodi : poschodiList) {
            poschodiDtos.add(new PoschodiDto(poschodi));
        }

        return poschodiDtos;
    }

    public List<KlicDto> seznamKlic(String idLokalita, String idBudova, String idPoschodi, AppUserDto appUserDto) throws BaseException {
        List<Klic> klicList = klicService.getList(idLokalita, idBudova, idPoschodi, true, null);

        List<KlicDto> klicDtos = new ArrayList<KlicDto>(klicList.size());
        for (Klic klic : klicList) {
            klicDtos.add(new KlicDto(klic));
        }

        return klicDtos;
    }

    public void saveBudova(BudovaDto budovaDto, HttpServletRequest request, AppUserDto appUserDto) throws BaseException {

        // volání aplikace Žádosti
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<BudovaDto> requestEntity = new HttpEntity<BudovaDto>(
                    budovaDto,
                    getZadostiHttpHeaders(request));

            ResponseEntity<Void> result = restZadosti
                    .exchange("/vratnice/budova/save", HttpMethod.POST,
                            requestEntity, Void.class,
                            new HashMap<>());

            if (result.getStatusCode().isError())
                throw new BaseException(
                        String.format(messageSource.getMessage("zadosti.error", null, LocaleContextHolder.getLocale()),
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
            throw new BaseException(
                    messageSource.getMessage("zadosti.nelze.spojit", null, LocaleContextHolder.getLocale()));
        }
    }

    public void saveLokalita(LokalitaDto lokalitaDto, HttpServletRequest request, AppUserDto appUserDto) throws BaseException {

        // volání aplikace Žádosti
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<LokalitaDto> requestEntity = new HttpEntity<LokalitaDto>(lokalitaDto, getZadostiHttpHeaders(request));

            ResponseEntity<Void> result = restZadosti
                    .exchange("/vratnice/lokalita/save", HttpMethod.POST,
                            requestEntity, Void.class,
                            new HashMap<>());

            if (result.getStatusCode().isError())
                throw new BaseException(
                        String.format(messageSource.getMessage("zadosti.error", null, LocaleContextHolder.getLocale()),
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
            throw new BaseException(
                    messageSource.getMessage("zadosti.nelze.spojit", null, LocaleContextHolder.getLocale()));
        }
    }

    public HttpHeaders getZadostiHttpHeaders(HttpServletRequest request) {

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
            Cookie zadostiCookie = securityUtils.generateAuthCookieKeyCloak(accessTokenResponse, "zadosti_auth");
            if (zadostiCookie != null) {

                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", zadostiCookie.getName() + "=" + zadostiCookie.getValue());
                return requestHeaders;
            }
        }
        return null;
    }

    public void nastavitZastup(HttpServletRequest request, AppUserDto appUserDto) {

        // volání aplikace Žádosti
        try {

            String sapIdZastupu = "";
            if (appUserDto.getZastup() != null) {
                Uzivatel uzivatel = uzivatelRepository.getDetail(appUserDto.getZastup().getIdUzivatel());
                if (uzivatel != null)
                    sapIdZastupu = uzivatel.getSapId();
            }

            HttpEntity<Void> requestEntity = new HttpEntity<Void>(null,
                    getZadostiHttpHeaders(request));

            HashMap<String, String> params = new HashMap<>();
            params.put("sapIdZastup", sapIdZastupu);

            ResponseEntity<Void> result = restZadosti
                    .exchange("/vratnice/zastup?sapIdZastup={sapIdZastup}", HttpMethod.POST,
                            requestEntity, Void.class,
                            params);

            if (result.getStatusCode().isError())
                logger.error(result.getStatusCode());

        } catch (Exception e) {
            logger.error(e);
        }
    }

}
