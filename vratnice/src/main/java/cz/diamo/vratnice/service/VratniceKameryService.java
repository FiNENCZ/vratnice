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
import cz.diamo.vratnice.dto.VratniceDto;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.repository.VratniceRepository;
import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryDto;
import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryNgDto;
import cz.diamo.vratnice.rest.dto.VjezdVyjezdVozidlaDto;

@Service
@TransactionalROE
public class VratniceKameryService {

    @Autowired
    RestTemplate restVratniceKameryTemplate;

    @Autowired
    private VratniceRepository vratniceRepository;

    /**
     * Vrací detail konfigurace vratnice kamery na základě zadané IP adresy.
     *
     * @param ipAdresa IP adresa zařízení, jehož konfiguraci se má vrátit.
     * @return Objekt {@link KonfiguraceVratniceKameryDto} obsahující detailní
     *         informace o konfiguraci.
     */
    public KonfiguraceVratniceKameryDto getKonfiguraceDetail(String ipAdresa) {
        String url = "http://" + ipAdresa + ":8091/api/konfigurace/detail";
        return restVratniceKameryTemplate.getForObject(url, KonfiguraceVratniceKameryDto.class);
    }

    /**
     * Vrací seznam vjezdů a výjezdů vozidel na základě zadané IP adresy.
     *
     * @param ipAdresa IP adresa zařízení, jehož vjezdy a výjezdy se mají vrátit.
     * @return Seznam objektů {@link VjezdVyjezdVozidlaDto} obsahující informace o
     *         vjezdech a výjezdech.
     */
    public List<VjezdVyjezdVozidlaDto> getVjezdVyjezd(String ipAdresa) {
        String url = "http://" + ipAdresa + ":8091/api/vjezd-vyjezd-vozidla/list";
        ResponseEntity<List<VjezdVyjezdVozidlaDto>> response = restVratniceKameryTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<VjezdVyjezdVozidlaDto>>() {
                });
        return response.getBody();
    }

    /**
     * Ukládá konfiguraci vratnice kamery na základě zadané IP adresy.
     *
     * @param konfiguraceDto Objekt {@link KonfiguraceVratniceKameryDto} obsahující
     *                       konfiguraci, která se má uložit.
     * @param ipAdresa       IP adresa zařízení, na které se má konfigurace uložit.
     * @return Uložený objekt {@link KonfiguraceVratniceKameryDto} s aktualizovanými
     *         informacemi.
     * @throws RestClientException Pokud dojde k chybě při volání API pro uložení
     *                             konfigurace.
     */
    public KonfiguraceVratniceKameryDto saveKonfigurace(KonfiguraceVratniceKameryDto konfiguraceDto, String ipAdresa) {
        String url = "http://" + ipAdresa + ":8091/api/konfigurace/save";

        konfiguraceDto.setCasZmn(Utils.getCasZmn());
        konfiguraceDto.setZmenuProvedl(Utils.getZmenuProv());

        String lang = LocaleContextHolder.getLocale().getLanguage();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("lang", lang);

        try {
            KonfiguraceVratniceKameryDto response = restVratniceKameryTemplate.postForObject(builder.toUriString(),
                    konfiguraceDto, KonfiguraceVratniceKameryDto.class);
            return response;
        } catch (RestClientException e) {

            throw new RestClientException(String.format("Pri volání vratnice-kamery došlo k chybě./n%s", e));
        }
    }

    /**
     * Vytváří objekt konfigurace vratnice kamery pro Angular aplikaci na základě
     * zadané konfigurace.
     *
     * @param konfigurace Objekt {@link KonfiguraceVratniceKameryDto}, ze kterého se
     *                    má vytvořit konfigurace pro Angular.
     * @return Objekt {@link KonfiguraceVratniceKameryNgDto} s informacemi pro
     *         Angular aplikaci.
     */
    public KonfiguraceVratniceKameryNgDto constructKonfiguraceNg(KonfiguraceVratniceKameryDto konfigurace) {
        KonfiguraceVratniceKameryNgDto konfiguraceNg = new KonfiguraceVratniceKameryNgDto(konfigurace);

        if (konfigurace.getIdVratnice() != null) {
            Vratnice vratnice = vratniceRepository.getDetail(konfigurace.getIdVratnice());
            konfiguraceNg.setVratnice(new VratniceDto(vratnice));
        }

        return konfiguraceNg;

    }

}
