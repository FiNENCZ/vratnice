package cz.dp.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import cz.dp.share.controller.BaseController;
import cz.dp.vratnice.dto.InicializaceVratniceKameryDto;
import cz.dp.vratnice.dto.VratniceKameryDto;
import cz.dp.vratnice.entity.InicializaceVratniceKamery;
import cz.dp.vratnice.rest.dto.KonfiguraceVratniceKameryDto;
import cz.dp.vratnice.rest.dto.KonfiguraceVratniceKameryNgDto;
import cz.dp.vratnice.rest.dto.VjezdVyjezdVozidlaDto;
import cz.dp.vratnice.service.InicializaceVratniceKameryService;
import cz.dp.vratnice.service.VratniceKameryService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class VratniceKameryController extends BaseController{
    
    private static final Logger logger = LogManager.getLogger(VratniceKameryController.class);

    @Autowired
    private VratniceKameryService vratniceKameryService;

    @Autowired
    private InicializaceVratniceKameryService inicializaceVratniceKameryService;

    @GetMapping("/vratnice-kamery/konfigurace/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRANTNICE_KAMERY')")
    public ResponseEntity<KonfiguraceVratniceKameryDto> getVratniceKameryKonfigurace(@RequestParam String ipAdresa) {
        return ResponseEntity.ok(vratniceKameryService.getKonfiguraceDetail(ipAdresa));
    }

    @GetMapping("/vratnice-kamery/vjezd-vyjezd-vozidla/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRANTNICE_KAMERY')")
    public ResponseEntity<List<VjezdVyjezdVozidlaDto>> getVjezdVyjezdVozidlaList(@RequestParam String ipAdresa) {
        return ResponseEntity.ok(vratniceKameryService.getVjezdVyjezd(ipAdresa));
    }



    @PostMapping("/vratnice-kamery/konfigurace/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRANTNICE_KAMERY')")
    public ResponseEntity<KonfiguraceVratniceKameryDto> saveKonfigurace(
            @RequestBody @Valid KonfiguraceVratniceKameryDto konfiguraceVratniceKameryDto,
            @RequestParam String ipAdresa) {

        return ResponseEntity.ok(vratniceKameryService.saveKonfigurace(konfiguraceVratniceKameryDto, ipAdresa));
    }

    @PostMapping("/vratnice-kamery/konfigurace-ng/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRANTNICE_KAMERY')")
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
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRANTNICE_KAMERY')")
    public ResponseEntity<List<VratniceKameryDto>> listKonfigurace() {
        List<InicializaceVratniceKamery> inicializaceList = inicializaceVratniceKameryService.list();
    
        List<VratniceKameryDto> result = inicializaceList.stream().map(inicializace -> {
            VratniceKameryDto vratniceKameryDto = new VratniceKameryDto();
            vratniceKameryDto.setInicializace(new InicializaceVratniceKameryDto(inicializace));
    
            try {
                KonfiguraceVratniceKameryDto konfigurace = vratniceKameryService.getKonfiguraceDetail(inicializace.getIpAdresa());
                KonfiguraceVratniceKameryNgDto konfiguraceNg = vratniceKameryService.constructKonfiguraceNg(konfigurace);
                vratniceKameryDto.setKonfigurace(konfiguraceNg);
            } catch (RestClientException e) {
                // Zde můžete zpracovat chybu, např. zalogovat ji nebo vytvořit prázdnou konfiguraci
                logger.error("Nepodařilo se získat konfiguraci pro IP adresu: " + inicializace.getIpAdresa());
                // konfigurace zůstane null
            }
    
            return vratniceKameryDto;
        }).collect(Collectors.toList());
    
        return ResponseEntity.ok(result);
    }
    
    
    @GetMapping("/vratnice-kamery/konfigurace-ng/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRANTNICE_KAMERY')")
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
