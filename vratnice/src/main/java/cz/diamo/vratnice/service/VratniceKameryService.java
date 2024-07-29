package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.base.Utils;
import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.services.ZavodServices;
import cz.diamo.vratnice.dto.LokalitaDto;
import cz.diamo.vratnice.dto.VjezdVozidlaDto;
import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryDto;
import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryNgDto;
import cz.diamo.vratnice.rest.dto.VjezdVyjezdVozidlaDto;

@Service
@TransactionalROE
public class VratniceKameryService {

    @Autowired
    RestTemplate restVratniceKameryTemplate;

    @Autowired
    private ZavodServices zavodServices;

    @Autowired
    private LokalitaService lokalitaService;

    public KonfiguraceVratniceKameryDto getKonfiguraceDetail(String ipAdresa) {
        String url = "http://" + ipAdresa + ":8080/api/konfigurace/detail";
        return restVratniceKameryTemplate.getForObject(url, KonfiguraceVratniceKameryDto.class);
    }

    public List<VjezdVyjezdVozidlaDto> getVjezdVyjezd(String ipAdresa) {
        String url = "http://" + ipAdresa + ":8080/api/vjezd-vyjezd-vozidla/list";
        ResponseEntity<List<VjezdVyjezdVozidlaDto>> response = restVratniceKameryTemplate.exchange(
            url, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<VjezdVyjezdVozidlaDto>>() {}
        );
        return response.getBody();
    }

    public KonfiguraceVratniceKameryDto saveKonfigurace(KonfiguraceVratniceKameryDto konfiguraceDto, String ipAdresa) {
        String url = "http://" + ipAdresa + ":8080/api/konfigurace/save";

        konfiguraceDto.setCasZmn(Utils.getCasZmn());
        konfiguraceDto.setZmenuProvedl(Utils.getZmenuProv());

        String lang = LocaleContextHolder.getLocale().getLanguage();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("lang", lang);

        try {
            KonfiguraceVratniceKameryDto response = restVratniceKameryTemplate.postForObject(builder.toUriString(), konfiguraceDto, KonfiguraceVratniceKameryDto.class);
            return response;
        } catch (RestClientException e) {

            throw new RestClientException(String.format("Pri volání vratnice-kamery došlo k chybě./n%s", e));
        }
    }

    public KonfiguraceVratniceKameryNgDto constructKonfiguraceNg(KonfiguraceVratniceKameryDto konfigurace) {
        KonfiguraceVratniceKameryNgDto konfiguraceNg = new KonfiguraceVratniceKameryNgDto(konfigurace);

        if(konfigurace.getIdZavod() != null) {
            Zavod zavod = zavodServices.getDetail(konfigurace.getIdZavod());
            konfiguraceNg.setZavod(new ZavodDto(zavod));
        }

        if(konfigurace.getIdLokalita() != null) {
            Lokalita lokalita = lokalitaService.detail(konfigurace.getIdLokalita());
            konfiguraceNg.setLokalita(new LokalitaDto(lokalita));
        }

        return konfiguraceNg;

    }

}
