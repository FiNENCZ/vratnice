package cz.dp.vratnice.controller;

import java.sql.Timestamp;
import java.time.Instant;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.controller.BaseController;
import cz.dp.vratnice.dto.InicializaceVratniceKameryDto;
import cz.dp.vratnice.entity.InicializaceVratniceKamery;
import cz.dp.vratnice.service.InicializaceVratniceKameryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class InicializaceVratniceKameryController extends BaseController {

    final static Logger logger = LogManager.getLogger(InicializaceVratniceKameryController.class);

    @Autowired
    private InicializaceVratniceKameryService inicializaceVratniceKameryService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/inicializace-vratnice-kamery/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRANTNICE_KAMERY')")
    public ResponseEntity<InicializaceVratniceKameryDto> save(@RequestParam String ipAdresaKamery) {
        InetAddressValidator validator = new InetAddressValidator();
        if (!validator.isValid(ipAdresaKamery)) {
            				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						messageSource.getMessage("ip.address.invalid", null, LocaleContextHolder.getLocale()));
        }


        InicializaceVratniceKamery newInicializace = new InicializaceVratniceKamery();
        newInicializace.setIpAdresa(ipAdresaKamery);
        newInicializace.setCasInicializace(Timestamp.from(Instant.now()));

        InicializaceVratniceKamery savedInicializace = inicializaceVratniceKameryService.save(newInicializace);
        
        return ResponseEntity.ok(new InicializaceVratniceKameryDto(savedInicializace));
    }
    
    
    @DeleteMapping("/inicializace-vratnice-kamery/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRANTNICE_KAMERY')")
    public ResponseEntity<Void> deleteById(@RequestParam String id) {
        InicializaceVratniceKamery inicializace = inicializaceVratniceKameryService.getDetail(id);
        if (inicializace == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        }

        inicializaceVratniceKameryService.delete(id);
        return ResponseEntity.noContent().build();  // HTTP 204 - Žádný obsah
    }


}
