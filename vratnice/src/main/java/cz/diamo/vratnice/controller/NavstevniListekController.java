package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.dto.NavstevniListekDto;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;
import cz.diamo.vratnice.service.NavstevaOsobaService;
import cz.diamo.vratnice.service.NavstevniListekService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class NavstevniListekController extends BaseController {

    final static Logger logger = LogManager.getLogger(NavstevniListekController.class);

    @Autowired
    private NavstevniListekService navstevniListekService;

    @Autowired
    private NavstevaOsobaService navstevaOsobaService;

    @Autowired
    private UzivatelServices uzivatelService;

    @PostMapping("/navstevni-listek/create")
    public ResponseEntity<NavstevniListekDto> save(@RequestBody @Valid NavstevniListekDto navstevniListekDto) {
        NavstevniListek navstevniListek = navstevniListekService.create(navstevniListekDto.toEntity());
        return ResponseEntity.ok(new NavstevniListekDto(navstevniListek));
    }

    @GetMapping("/navstevni-listek/list-all")
    public ResponseEntity<List<NavstevniListekDto>> getAll() {
        List<NavstevniListekDto> navstevniListky = navstevniListekService.getAll().stream()
            .map(NavstevniListekDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(navstevniListky);
    }

    @GetMapping("/navstevni-listek/detail")
    public ResponseEntity<NavstevniListekDto> getDetail(@RequestParam String idNavstevniListek) {
        NavstevniListek navstevniListek = navstevniListekService.getDetail(idNavstevniListek);
        if (navstevniListek == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NavstevniListekDto(navstevniListek));
    }
    
    
    @GetMapping("/navstevni-listek/get-by-uzivatel")
    public ResponseEntity<List<NavstevniListek>> getNavstevniListkyByUzivatel(@RequestParam String uzivatelId) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelService.getDetail(uzivatelId); // Assuming Uzivatel entity has a constructor with ID parameter
        List<NavstevniListek> navstevniListky = navstevniListekService.getNavstevniListkyByUzivatel(uzivatel);
        return ResponseEntity.ok(navstevniListky);
    }

    @GetMapping("/navstevni-listek/get-by-navsteva-osoba")
    public ResponseEntity<List<NavstevniListek>> getNavstevniListkyByNavstevaOsoba(@RequestParam String navstevaOsobaId) throws RecordNotFoundException, NoSuchMessageException {
        NavstevaOsoba navstevaOsoba = navstevaOsobaService.getDetail(navstevaOsobaId); // Assuming NavstevaOsoba entity has a constructor with ID parameter
        List<NavstevniListek> navstevniListky = navstevniListekService.getNavstevniListkyByNavstevaOsoba(navstevaOsoba);
        return ResponseEntity.ok(navstevniListky);
    }

}
