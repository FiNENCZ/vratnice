package cz.dp.vratnice.rest.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.rest.controller.BaseRestController;
import cz.dp.vratnice.dto.InicializaceVratniceKameryDto;
import cz.dp.vratnice.dto.RzDetectedMessageDto;
import cz.dp.vratnice.entity.InicializaceVratniceKamery;
import cz.dp.vratnice.rest.dto.StatusMessageVjezdVyjezdDto;
import cz.dp.vratnice.rest.dto.VjezdVyjezdVozidlaDto;
import cz.dp.vratnice.rest.service.VratniceKameryRestService;
import cz.dp.vratnice.service.InicializaceVratniceKameryService;
import cz.dp.vratnice.service.RzVozidlaDetektorService;



@RestController
public class VratniceKameryRestController extends BaseRestController {

    final static Logger logger = LogManager.getLogger(VratniceKameryRestController.class);

    @Autowired
    private RzVozidlaDetektorService rzVozidlaDetektorService;

    @Autowired
    private VratniceKameryRestService vratniceKameryRestService;

    @Autowired
    private InicializaceVratniceKameryService inicializaceVratniceKameryService;


    @PostMapping("/rz-vozidla-detektor/detekce")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_KAMERY')")
    public RzDetectedMessageDto processRzVozidla(@RequestParam String idVratnice, @RequestParam String rzVozidla, @RequestParam Boolean vjezd) throws JSONException, RecordNotFoundException, NoSuchMessageException {
        if (vjezd) {
            return rzVozidlaDetektorService.checkIfRzVozidlaIsAllowedAndSendWS(idVratnice, rzVozidla, vjezd);
        } else {
            return rzVozidlaDetektorService.checkIfRzVozidlaCanLeaveAndSendWs(idVratnice, rzVozidla, vjezd);
        }
    }

    @PostMapping("/rz-vozidla-detektor/nevyporadane-zaznamy")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_KAMERY')")
    public ResponseEntity<StatusMessageVjezdVyjezdDto> nevyporadaneZaznamy(@RequestBody List<VjezdVyjezdVozidlaDto> vjezdVyjezdVozidlaDtoList, @RequestParam String idVratnice) throws JSONException{
        try {
            vratniceKameryRestService.saveNevyporadaneZaznamy(vjezdVyjezdVozidlaDtoList, idVratnice);
            return ResponseEntity.ok(new StatusMessageVjezdVyjezdDto("Záznamy byly úspěšně zpracovány.", null));
        } catch (Exception ex) {
            logger.error("Nastala chyba při zpracování záznamů", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new StatusMessageVjezdVyjezdDto("Záznamy byly úspěšně zpracovány.", ex.getMessage()));
        }
    }

    @PostMapping("/vratnice-kamery-konfigurace/inicializace")
    @PreAuthorize("hasAnyAuthority('ROLE_VRATNICE_KAMERY')")
    public ResponseEntity<InicializaceVratniceKameryDto> inicializace(@RequestBody InicializaceVratniceKameryDto inicializaceVratniceKameryDto) throws JSONException{
        try {
            InicializaceVratniceKamery savedInicializaceVratniceKamery = inicializaceVratniceKameryService.save(inicializaceVratniceKameryDto.toEntity());
            return ResponseEntity.ok(new InicializaceVratniceKameryDto(savedInicializaceVratniceKamery));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.toString());
        }
    }
    
    


}
