package cz.diamo.share.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import com.google.gson.Gson;

import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.configuration.AppProperties;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.edos.LokalitaEdosDto;
import cz.diamo.share.dto.security.AuthCookieDto;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.repository.LokalitaRepository;
import cz.diamo.share.security.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LokalitaServices {

    @Autowired
    private LokalitaRepository lokalitaRepository;

    @Autowired
    private RestOperations restEdos;

    @Autowired
    private AuthServices authServices;

    @Autowired
    private ZavodServices zavodServices;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AppProperties appProperties;

    @PersistenceContext
    private EntityManager entityManager;

    public Lokalita getDetail(String idLokalita) {
        return lokalitaRepository.getDetail(idLokalita);
    }

    public Lokalita getDetailByKod(String kod) {
        return lokalitaRepository.getDetailByKod(kod);
    }

    public List<Lokalita> getList(String idZavod, Boolean aktivita, Boolean verejne) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Lokalita s");
        queryString.append(" join fetch s.zavod zavod");
        queryString.append(" where 1 = 1");

        if (StringUtils.isNotBlank(idZavod))
            queryString.append(" and zavod.idZavod = :idZavod");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (verejne != null)
            queryString.append(" and s.verejne = :verejne");

        queryString.append(" order by zavod.sapId, s.nazev");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (StringUtils.isNotBlank(idZavod))
            vysledek.setParameter("idZavod", idZavod);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (verejne != null)
            vysledek.setParameter("verejne", verejne);

        @SuppressWarnings("unchecked")
        List<Lokalita> list = vysledek.getResultList();

        return list;
    }

    public Lokalita detail(String idLokalita) {
        return lokalitaRepository.getDetail(idLokalita);
    }

    @TransactionalWrite
    public Lokalita save(Lokalita lokalita) throws BaseException {

        lokalita.setCasZmn(Utils.getCasZmn());
        lokalita.setZmenuProvedl(Utils.getZmenuProv());

        Utils.validate(lokalita);

        lokalita = lokalitaRepository.save(lokalita);

        return lokalita;
    }

    /**
     * Odstranění zázmamu
     * 
     * @param idLokalita
     */
    @TransactionalWrite
    public void odstranit(String idLokalita) {
        lokalitaRepository.zmenaAktivity(idLokalita, false, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    /**
     * Obnovení záznamu
     * 
     * @param idLokalita
     */
    @TransactionalWrite
    public void obnovit(String idLokalita) {
        lokalitaRepository.zmenaAktivity(idLokalita, true, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    /**
     * Skrytí zázmamu
     * 
     * @param idLokalita
     */
    @TransactionalWrite
    public void zviditelnit(String idLokalita) {
        lokalitaRepository.zmenaVerejne(idLokalita, true, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    /**
     * Zveřejnění záznamu
     * 
     * @param idLokalita
     */
    @TransactionalWrite
    public void skryt(String idLokalita) {
        lokalitaRepository.zmenaVerejne(idLokalita, false, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    @TransactionalWrite
    public void updateLokalitaFromEdos(HttpServletRequest request, AppUserDto appUserDto) throws BaseException {
        List<LokalitaEdosDto> listLokalitaEdos = listLokalitaEdos(request, appUserDto);
        List<String> updatedIds = new ArrayList<>();

        String lastZavodSapId = "";
        Zavod zavod = null;

        for (LokalitaEdosDto lokalitaEdosDto : listLokalitaEdos) {
            if (StringUtils.isBlank(lastZavodSapId)
                    || !StringUtils.equals(lokalitaEdosDto.getZavodSapId(), lastZavodSapId)) {
                zavod = zavodServices.getDetailBySapId(lokalitaEdosDto.getZavodSapId());
                lastZavodSapId = lokalitaEdosDto.getZavodSapId();
            }

            if (zavod != null) {
                Lokalita lokalita = getDetailByKod(lokalitaEdosDto.getKod());

                if (lokalita == null)
                    lokalita = new Lokalita();

                lokalita.setZavod(zavod);
                lokalita.setKod(lokalitaEdosDto.getKod());
                lokalita.setNazev(lokalitaEdosDto.getNazev());
                lokalita.setAktivita(true);

                lokalita = save(lokalita);
                updatedIds.add(lokalita.getIdLokalita());
            }
        }

        if (updatedIds.size() > 0) {
            lokalitaRepository.zmenaAktivityHromadneNotIn(updatedIds, false, Utils.getCasZmn(), Utils.getZmenuProv());
        }
    }

    private List<LokalitaEdosDto> listLokalitaEdos(HttpServletRequest request, AppUserDto appUserDto)
            throws BaseException {
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

                HttpEntity<Void> requestEntity = new HttpEntity<Void>(null, requestHeaders);

                ResponseEntity<LokalitaEdosDto[]> result = restEdos.exchange("/zadosti/lokalita/list", HttpMethod.GET,
                        requestEntity, LokalitaEdosDto[].class,
                        new HashMap<>());

                if (result.getStatusCode().isError()) {
                    throw new BaseException(
                            String.format("Při volání EDOS došlo k chybě./n%s", result.getStatusCode()));
                } else {
                    return Arrays.asList(result.getBody());
                }

            }
        }

        return new ArrayList<>();
    }
}
