package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.controller.KonfiguraceController;
import cz.diamo.vratnice.dto.InicializaceVratniceKameryDto;
import cz.diamo.vratnice.dto.VratniceKameryDto;
import cz.diamo.vratnice.entity.InicializaceVratniceKamery;
import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryDto;
import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryNgDto;
import cz.diamo.vratnice.rest.dto.VjezdVyjezdVozidlaDto;
import cz.diamo.vratnice.service.InicializaceVratniceKameryService;
import cz.diamo.vratnice.service.VratniceKameryService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class VratniceKameryController extends BaseController{
    
    private static final Logger logger = LogManager.getLogger(KonfiguraceController.class);

    @Autowired
    private VratniceKameryService vratniceKameryService;

    @Autowired
    private InicializaceVratniceKameryService inicializaceVratniceKameryService;

    @GetMapping("/vratnice-kamery/konfigurace/detail")
    public ResponseEntity<KonfiguraceVratniceKameryDto> getVratniceKameryKonfigurace(@RequestParam String ipAdresa) {
        return ResponseEntity.ok(vratniceKameryService.getKonfiguraceDetail(ipAdresa));
    }

    @GetMapping("/vratnice-kamery/vjezd-vyjezd-vozidla/list")
    public ResponseEntity<List<VjezdVyjezdVozidlaDto>> getVjezdVyjezdVozidlaList(@RequestParam String ipAdresa) {
        return ResponseEntity.ok(vratniceKameryService.getVjezdVyjezd(ipAdresa));
    }



    @PostMapping("/vratnice-kamery/konfigurace/save")
    public ResponseEntity<KonfiguraceVratniceKameryDto> saveKonfigurace(
            @RequestBody @Valid KonfiguraceVratniceKameryDto konfiguraceVratniceKameryDto,
            @RequestParam String ipAdresa) {

        return ResponseEntity.ok(vratniceKameryService.saveKonfigurace(konfiguraceVratniceKameryDto, ipAdresa));
    }

    @PostMapping("/vratnice-kamery/konfigurace-ng/save")
    public ResponseEntity<VratniceKameryDto> saveKonfiguraceNg(
            @RequestBody @Valid VratniceKameryDto vratniceKameryDto) {

        // Uložení konfigurace
        String ipAdresa = vratniceKameryDto.getInicializace().getIpAdresa();
        KonfiguraceVratniceKameryDto konfigurace = new KonfiguraceVratniceKameryDto(vratniceKameryDto.getKonfigurace());

        KonfiguraceVratniceKameryDto savedKonfigurace =  vratniceKameryService.saveKonfigurace(konfigurace, ipAdresa);

        // Načtení nové (uložené) konfigurace
        KonfiguraceVratniceKameryNgDto newKonfiguraceNg = vratniceKameryService.constructKonfiguraceNg(savedKonfigurace);
        InicializaceVratniceKamery inicializace = inicializaceVratniceKameryService.getByIpAdresa(ipAdresa);

        VratniceKameryDto newVratniceKameryDto = new VratniceKameryDto();
        newVratniceKameryDto.setInicializace(new InicializaceVratniceKameryDto(inicializace));
        newVratniceKameryDto.setKonfigurace(newKonfiguraceNg);

        return ResponseEntity.ok(newVratniceKameryDto);


    }


@GetMapping("/vratnice-kamery/konfigurace/list")
public ResponseEntity<List<VratniceKameryDto>> listKonfigurace() {
    List<InicializaceVratniceKamery> inicializaceList = inicializaceVratniceKameryService.list();

    List<VratniceKameryDto> result = inicializaceList.stream().map(inicializace -> {
        try {
            KonfiguraceVratniceKameryDto konfigurace = vratniceKameryService.getKonfiguraceDetail(inicializace.getIpAdresa());
            KonfiguraceVratniceKameryNgDto konfiguraceNg = vratniceKameryService.constructKonfiguraceNg(konfigurace);

            VratniceKameryDto vratniceKameryDto = new VratniceKameryDto();
            vratniceKameryDto.setInicializace(new InicializaceVratniceKameryDto(inicializace));
            vratniceKameryDto.setKonfigurace(konfiguraceNg);
            return vratniceKameryDto;
        } catch (RestClientException e) {
            // Zde můžete zpracovat chybu, např. zalogovat ji nebo vytvořit prázdnou konfiguraci
            logger.error("Nepodařilo se získat konfiguraci pro IP adresu: " + inicializace.getIpAdresa());
            // Můžete se rozhodnout vrátit null nebo prázdný objekt, podle potřeby
            return null;
        }
    }).filter(Objects::nonNull).collect(Collectors.toList());

    return ResponseEntity.ok(result);
}
    @GetMapping("/vratnice-kamery/konfigurace-ng/detail")
    public ResponseEntity<VratniceKameryDto> getVratniceKonfiguraceNgDetail(@RequestParam String ipAdresa) {
        KonfiguraceVratniceKameryDto konfigurace = vratniceKameryService.getKonfiguraceDetail(ipAdresa);

        KonfiguraceVratniceKameryNgDto konfiguraceNg = vratniceKameryService.constructKonfiguraceNg(konfigurace);
        InicializaceVratniceKamery inicializace = inicializaceVratniceKameryService.getByIpAdresa(ipAdresa);

        VratniceKameryDto vratniceKameryDto = new VratniceKameryDto();
        vratniceKameryDto.setInicializace(new InicializaceVratniceKameryDto(inicializace));
        vratniceKameryDto.setKonfigurace(konfiguraceNg);

        return ResponseEntity.ok(vratniceKameryDto);
    }
    
    
    

}
