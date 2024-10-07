package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.NavstevniListekUzivatelStavDto;
import cz.diamo.vratnice.entity.NavstevniListekUzivatelStav;
import cz.diamo.vratnice.service.NavstevniListekUzivatelStavService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
public class NavstevniListekUzivatelStavController extends BaseController{

    final static Logger logger = LogManager.getLogger(NavstevniListekController.class);

    @Autowired
    private NavstevniListekUzivatelStavService navstevniListekUzivatelStavService;

    @GetMapping("/navstevni-listek-uzivatel-stav/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<List<NavstevniListekUzivatelStavDto>> list(@RequestParam String idNavstevniListek) throws RecordNotFoundException, NoSuchMessageException {
        List<NavstevniListekUzivatelStavDto> result = new ArrayList<NavstevniListekUzivatelStavDto>();
        List<NavstevniListekUzivatelStav> list = navstevniListekUzivatelStavService.getByNavstevniListek(idNavstevniListek);

        if (list != null && list.size() > 0) {
            for (NavstevniListekUzivatelStav navstevniListekUzivatelStav : list) {
                result.add(new NavstevniListekUzivatelStavDto(navstevniListekUzivatelStav));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/navstevni-listek-uzivatel-stav/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<NavstevniListekUzivatelStavDto> detail(@RequestParam String idNavstevniListekUzivatelStav) {
        NavstevniListekUzivatelStav uzivatelStav = navstevniListekUzivatelStavService.getDetail(idNavstevniListekUzivatelStav);
        return ResponseEntity.ok(new NavstevniListekUzivatelStavDto(uzivatelStav));
    }
    
    
    @PostMapping("/navstevni-listek-uzivatel-stav/pridat-poznamku")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<NavstevniListekUzivatelStavDto> pridatPoznamku(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request,
            @RequestParam String idNavstevniListekUzivatelStav, @RequestParam String poznamka) throws NoSuchMessageException, BaseException {
        
        NavstevniListekUzivatelStav savedNavstevniListekUzivatelStav = navstevniListekUzivatelStavService.pridatPoznamku(request, appUserDto, idNavstevniListekUzivatelStav, poznamka);

        return ResponseEntity.ok(new NavstevniListekUzivatelStavDto(savedNavstevniListekUzivatelStav));
    }
}
