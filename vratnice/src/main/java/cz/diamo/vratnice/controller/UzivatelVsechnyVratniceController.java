package cz.diamo.vratnice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.entity.UzivatelVsechnyVratnice;
import cz.diamo.vratnice.service.UzivatelVsechnyVratniceService;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class UzivatelVsechnyVratniceController extends BaseController {

    final static Logger logger = LogManager.getLogger(NavstevniListekController.class);

    @Autowired
    private UzivatelVsechnyVratniceService uzivatelVsechnyVratniceService;

    @PostMapping("/uzivatel-vsechny-vratnice/nastav-vsechny-vratnice")
    @PreAuthorize("hasAnyAuthority('ROLE_VSECHNY_VRATNICE')")
    public ResponseEntity<UzivatelVsechnyVratnice> nastavVsechnyVratnice(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException, UniqueValueException {
        UzivatelVsechnyVratnice savedUzivatelVsechnyVratnice = uzivatelVsechnyVratniceService.nastavVsechnyVratnice(appUserDto);
        return ResponseEntity.ok(savedUzivatelVsechnyVratnice);
    }

    @GetMapping("/uzivatel-vsechny-vratnice/je-nastavena")
    @PreAuthorize("hasAnyAuthority('ROLE_VSECHNY_VRATNICE')")
    public ResponseEntity<Boolean> jeNastavena(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException, UniqueValueException {
        Boolean jeNastaven = uzivatelVsechnyVratniceService.jeNastavena(appUserDto);
        return ResponseEntity.ok(jeNastaven);
    }

}
