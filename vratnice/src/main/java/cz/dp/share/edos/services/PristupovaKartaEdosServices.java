package cz.dp.share.edos.services;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.dto.AktualizacePristupovychKaretResponseDto;
import cz.dp.share.edos.dto.UzivatelPristupovaKartaEdosDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.entity.Zavod;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.services.UzivatelServices;

@Service
@TransactionalROE
public class PristupovaKartaEdosServices {

    final static Logger logger = LogManager.getLogger(PristupovaKartaEdosServices.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    protected RestOperations restEdosRest;

    @TransactionalWrite
    public AktualizacePristupovychKaretResponseDto aktualizacePristupovychKaret(Zavod zavod)
            throws BaseException {

        // volání EDOS
        try {

            AktualizacePristupovychKaretResponseDto resp = new AktualizacePristupovychKaretResponseDto();
            String idZavod = null;
            HashMap<String, Object> params = new HashMap<String, Object>();
            String uri = "/rest/zamestnanec/pristupove-karty/list";
            if (zavod != null) {
                params.put("sapIdPersonalniOblast", zavod.getSapId());
                idZavod = zavod.getIdZavod();
                uri = "/rest/zamestnanec/pristupove-karty/list?sapIdPersonalniOblast={sapIdPersonalniOblast}";
            }

            ResponseEntity<UzivatelPristupovaKartaEdosDto[]> result = restEdosRest.exchange(uri,
                    HttpMethod.GET,
                    null, UzivatelPristupovaKartaEdosDto[].class, params);

            if (result.getStatusCode().isError())
                throw new BaseException(
                        String.format("Při volání EDOS - přístupové karty došlo k chybě./n%s", result.getStatusCode()));

            // načtení seznamu uživatelů
            List<UzivatelPristupovaKartaEdosDto> listKaret = null;
            if (result.getBody() != null && result.getBody().length > 0)
                listKaret = Arrays.asList(result.getBody());
            if (listKaret != null && listKaret.size() > 0) {
                List<Uzivatel> listUzivatel = uzivatelServices.getList(idZavod, null);

                if (listUzivatel != null && listUzivatel.size() > 0) {
                    for (Uzivatel uzivatel : listUzivatel) {
                        for (UzivatelPristupovaKartaEdosDto karta : listKaret) {
                            if (uzivatel.getSapId().equals(karta.getSapId())) {

                                boolean zmena = false;
                                if (!StringUtils.equals(uzivatel.getCip1(), karta.getPrimarniKartaRfid())) {
                                    uzivatel.setCip1(karta.getPrimarniKartaRfid());
                                    zmena = true;
                                }

                                if (!StringUtils.equals(uzivatel.getCip2(), karta.getSekundarniKartaRfid())) {
                                    uzivatel.setCip2(karta.getSekundarniKartaRfid());
                                    zmena = true;
                                }

                                if (zmena) {
                                    uzivatelServices.save(uzivatel, false, false, false);
                                    resp.setPocetAktualizovanych(resp.getPocetAktualizovanych() + 1);
                                    resp.getListSapIdZamestnanec().add(uzivatel.getSapId());
                                }

                                break;
                            }
                        }
                    }
                }
            }

            return resp;

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
}