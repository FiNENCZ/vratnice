package cz.diamo.vratnice.zadosti.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
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
import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.BudovaDto;
import cz.diamo.share.dto.LokalitaDto;
import cz.diamo.share.dto.security.AuthCookieDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.ZadostExterni;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.repository.ZadostExterniZaznamRepository;
import cz.diamo.share.security.SecurityUtils;
import cz.diamo.share.services.AuthServices;
import cz.diamo.share.services.ZadostiExterniServices;
import cz.diamo.vratnice.dto.KlicDto;
import cz.diamo.vratnice.dto.NavstevniListekDto;
import cz.diamo.vratnice.dto.NavstevniListekUzivatelStavDto;
import cz.diamo.vratnice.dto.PoschodiDto;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.Poschodi;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.entity.ZadostStav;
import cz.diamo.vratnice.enums.ZadostStavEnum;
import cz.diamo.vratnice.repository.PoschodiRepository;
import cz.diamo.vratnice.service.KlicService;
import cz.diamo.vratnice.service.ZadostKlicService;
import cz.diamo.vratnice.zadosti.dto.ZadostKlicExtDto;
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
    private ZadostKlicService zadostKlicService;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private RestOperations restZadosti;

    @Autowired
    private ZadostExterniZaznamRepository zadostExterniZaznamRepository;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private MessageSource messageSource;

    /**
     * Vrací seznam poschodí pro danou budovu.
     *
     * Tato metoda načte seznam poschodí z databáze na základě ID budovy
     * a vrátí jej jako seznam objektů {@link PoschodiDto}.
     *
     * @param idBudova   ID budovy, pro kterou se mají načíst poschodí.
     * @param appUserDto Objekt {@link AppUserDto}, který obsahuje informace o
     *                   uživateli.
     * @return Seznam {@link PoschodiDto} obsahující poschodí pro danou budovu.
     * @throws BaseException Pokud dojde k chybě při načítání poschodí z databáze.
     */
    @TransactionalROE
    public List<PoschodiDto> seznamPoschodi(String idBudova, AppUserDto appUserDto) throws BaseException {
        List<Poschodi> poschodiList = poschodiRepository.getList(idBudova, true);

        List<PoschodiDto> poschodiDtos = new ArrayList<PoschodiDto>(poschodiList.size());
        for (Poschodi poschodi : poschodiList) {
            poschodiDtos.add(new PoschodiDto(poschodi));
        }

        return poschodiDtos;
    }

    /**
     * Vrací seznam klíčů pro danou lokalitu, budovu a poschodí.
     *
     * Tato metoda načte seznam klíčů z databáze na základě ID lokality,
     * ID budovy a ID poschodí a vrátí jej jako seznam objektů {@link KlicDto}.
     *
     * @param idLokalita ID lokality, pro kterou se mají načíst klíče.
     * @param idBudova   ID budovy, pro kterou se mají načíst klíče.
     * @param idPoschodi ID poschodí, pro které se mají načíst klíče.
     * @param appUserDto Objekt {@link AppUserDto}, který obsahuje informace o
     *                   uživateli.
     * @return Seznam {@link KlicDto} obsahující klíče pro danou lokalitu, budovu a
     *         poschodí.
     * @throws BaseException Pokud dojde k chybě při načítání klíčů z databáze.
     */
    @TransactionalROE
    public List<KlicDto> seznamKlic(String idLokalita, String idBudova, String idPoschodi, AppUserDto appUserDto)
            throws BaseException {
        List<Klic> klicList = klicService.getList(idLokalita, idBudova, idPoschodi, true, null);

        List<KlicDto> klicDtos = new ArrayList<KlicDto>(klicList.size());
        for (Klic klic : klicList) {
            klicDtos.add(new KlicDto(klic));
        }

        return klicDtos;
    }

    /**
     * Ukládá žádost o klíč pro zaměstnance.
     *
     * Tato metoda nejprve ověří existenci uživatele na základě SAP ID
     * a poté buď aktualizuje existující žádost, nebo vytvoří novou žádost
     * o klíč. Uloží informace o žádosti a externí žádosti do databáze.
     *
     * @param zadostKlicDto Objekt {@link ZadostKlicExtDto}, který obsahuje detaily
     *                      žádosti o klíč.
     * @param appUserDto    Objekt {@link AppUserDto}, který obsahuje informace o
     *                      uživateli.
     * @throws ValidationException Pokud dojde k chybě při validaci žádosti.
     * @throws BaseException       Pokud dojde k chybě při ukládání žádosti nebo
     *                             při volání externí služby.
     */
    @TransactionalWrite
    public void saveZadostKlic(ZadostKlicExtDto zadostKlicDto, AppUserDto appUserDto)
            throws ValidationException, BaseException {
        Uzivatel uzivatel = uzivatelRepository.getDetailBySapId(zadostKlicDto.getSapIdZamestnance());
        if (uzivatel == null)
            throw new RecordNotFoundException(
                    messageSource.getMessage("uzivatel.not.found", null, LocaleContextHolder.getLocale()), null, true);

        List<String> idZaznamList = zadostExterniZaznamRepository.listIdZaznam(zadostKlicDto.getId());

        ZadostKlic zadost;

        if (idZaznamList != null && idZaznamList.size() > 0) {
            // aktualizuji původní žádost
            zadost = zadostKlicService.getDetail(idZaznamList.get(0));
        } else {
            // vytvořím novou žádost
            zadost = new ZadostKlic();
        }

        zadost.setDatumOd(zadostKlicDto.getDatumOd());
        zadost.setDatumDo(zadostKlicDto.getDatumDo());
        zadost.setDuvod(zadostKlicDto.getDuvod());
        zadost.setKlic(new Klic(zadostKlicDto.getKlicId()));
        zadost.setZadostStav(new ZadostStav(ZadostStavEnum.SCHVALENO));
        zadost.setUzivatel(uzivatel);
        zadost.setTrvala(zadostKlicDto.getDatumDo() == null);

        zadost = zadostKlicService.save(zadost, appUserDto);

        ZadostExterni zadostExterni = new ZadostExterni();
        zadostExterni.setIdZadostExterni(zadostKlicDto.getId());
        zadostExterni.setCas(Calendar.getInstance().getTime());
        zadostExterni.setUzivatel(uzivatel);
        zadostExterni.setUzivatelVytvoril(new Uzivatel(appUserDto.getIdUzivatel()));
        zadostExterni.setDatumPredani(zadostKlicDto.getDatumPredani());
        zadostExterni.setTyp(zadostKlicDto.getTyp());
        save(zadostExterni, List.of(zadost.getIdZadostKlic()));
    }

    /**
     * Ukládá informace o budově.
     *
     * @param budovaDto  Objekt {@link BudovaDto}, který obsahuje informace o
     *                   budově.
     * @param request    HTTP požadavek, který obsahuje informace o uživatelském
     *                   kontextu.
     * @param appUserDto Objekt {@link AppUserDto}, který obsahuje informace o
     *                   uživateli.
     * @throws BaseException Pokud dojde k chybě při ukládání budovy nebo
     *                       při volání externí služby.
     */
    public void saveBudova(BudovaDto budovaDto, HttpServletRequest request, AppUserDto appUserDto)
            throws BaseException {

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

    /**
     * Ukládá informace o lokalitě.
     * 
     * @param lokalitaDto Objekt {@link LokalitaDto}, který obsahuje informace o
     *                    lokalitě.
     * @param request     HTTP požadavek, který obsahuje informace o uživatelském
     *                    kontextu.
     * @param appUserDto  Objekt {@link AppUserDto}, který obsahuje informace o
     *                    uživateli.
     * @throws BaseException Pokud dojde k chybě při ukládání lokality nebo
     *                       při volání externí služby.
     */
    public void saveLokalita(LokalitaDto lokalitaDto, HttpServletRequest request, AppUserDto appUserDto)
            throws BaseException {

        // volání aplikace Žádosti
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<LokalitaDto> requestEntity = new HttpEntity<LokalitaDto>(lokalitaDto,
                    getZadostiHttpHeaders(request));

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

    /**
     * Získává HTTP hlavičky pro žádosti o autentizaci.
     *
     * @param request HTTP požadavek, ze kterého se získávají cookies.
     * @return {@link HttpHeaders} obsahující cookie pro autentizaci, nebo null,
     *         pokud nebyla nalezena platná cookie nebo došlo k chybě.
     */
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

    /**
     * Nastavuje zástupce pro uživatele na základě informací z {@link AppUserDto}.
     *
     * @param request    HTTP požadavek, který obsahuje informace o uživatelském
     *                   kontextu.
     * @param appUserDto Objekt {@link AppUserDto}, který obsahuje informace o
     *                   uživateli.
     */
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

    /**
     * Ukládá návštěvní lístek.
     *
     * @param request             HTTP požadavek, který obsahuje informace o
     *                            uživatelském kontextu.
     * @param appUserDto          Objekt {@link AppUserDto}, který obsahuje
     *                            informace o uživateli.
     * @param navstevniListekdDto Objekt {@link NavstevniListekDto}, který obsahuje
     *                            detaily návštěvního lístku, který se má uložit.
     * @throws BaseException Pokud dojde k chybě při ukládání návštěvního lístku
     *                       nebo při volání externí služby.
     */
    // TODO: -- ŽÁDOSTI -- upravit dle potřeby
    public void saveNavstevniListek(HttpServletRequest request, AppUserDto appUserDto,
            NavstevniListekDto navstevniListekdDto) throws BaseException {

        // volání aplikace Žádosti
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<NavstevniListekDto> requestEntity = new HttpEntity<NavstevniListekDto>(
                    navstevniListekdDto,
                    getZadostiHttpHeaders(request));

            ResponseEntity<Void> result = restZadosti
                    .exchange("/vratnice/navstevni-listek/save", HttpMethod.POST,
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

    /**
     * Přidává poznámku k návštěvnímu lístku.
     *
     * @param request         HTTP požadavek, který obsahuje informace o
     *                        uživatelském kontextu.
     * @param appUserDto      Objekt {@link AppUserDto}, který obsahuje informace o
     *                        uživateli.
     * @param uzivatelStavDto Objekt {@link NavstevniListekUzivatelStavDto}, který
     *                        obsahuje detaily stavu uživatele návštěvního lístku.
     * @throws BaseException Pokud dojde k chybě při přidávání poznámky nebo
     *                       při volání externí služby.
     */
    // TODO: -- ŽÁDOSTI -- upravit dle potřeby - metoda například pro odůvodnění,
    // proč neproběhla návštěva
    public void navstevniListekPridatPoznamku(HttpServletRequest request, AppUserDto appUserDto,
            NavstevniListekUzivatelStavDto uzivatelStavDto) throws BaseException {

        // volání aplikace Žádosti
        try {

            // nastavení zástupu
            nastavitZastup(request, appUserDto);

            HttpEntity<NavstevniListekUzivatelStavDto> requestEntity = new HttpEntity<NavstevniListekUzivatelStavDto>(
                    uzivatelStavDto,
                    getZadostiHttpHeaders(request));

            ResponseEntity<Void> result = restZadosti
                    .exchange("/vratnice/navstevni-listek/pridat-poznamku", HttpMethod.POST,
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

}
