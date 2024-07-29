package cz.diamo.vratnice.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.controller.KonfiguraceController;
import cz.diamo.vratnice.dto.VjezdVozidlaDto;
import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryDto;
import cz.diamo.vratnice.service.VratniceKameryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class VratniceKameryController extends BaseController{
    
    private static final Logger logger = LogManager.getLogger(KonfiguraceController.class);

    @Autowired
    private VratniceKameryService vratniceKameryService;

    @GetMapping("/vratnice-kamery/konfigurace/detail")
    public ResponseEntity<KonfiguraceVratniceKameryDto> getVratniceKameryKonfigurace(@RequestParam String ipAdresa) {
        return ResponseEntity.ok(vratniceKameryService.getKonfiguraceDetail(ipAdresa));
    }

    @GetMapping("/vratnice-kamery/vjezd-vyjezd-vozidla/list")
    public ResponseEntity<List<VjezdVozidlaDto>> getVjezdVyjezdVozidlaList(@RequestParam String ipAdresa) {
        return ResponseEntity.ok(vratniceKameryService.getVjezdVyjezd(ipAdresa));
    }
    

}
