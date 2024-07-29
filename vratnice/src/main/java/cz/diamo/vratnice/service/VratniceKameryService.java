package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.vratnice.dto.VjezdVozidlaDto;
import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryDto;

@Service
@TransactionalROE
public class VratniceKameryService {

    @Autowired
    RestTemplate restVratniceKameryTemplate;

    public KonfiguraceVratniceKameryDto getKonfiguraceDetail(String ipAdresa) {
        String url = "http://" + ipAdresa + ":8080/api/konfigurace/detail";
        return restVratniceKameryTemplate.getForObject(url, KonfiguraceVratniceKameryDto.class);
    }

    public List<VjezdVozidlaDto> getVjezdVyjezd(String ipAdresa) {
        String url = "http://" + ipAdresa + ":8080/api/vjezd-vyjezd-vozidla/list";
        ResponseEntity<List<VjezdVozidlaDto>> response = restVratniceKameryTemplate.exchange(
            url, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<VjezdVozidlaDto>>() {}
        );
        return response.getBody();
    }

}
