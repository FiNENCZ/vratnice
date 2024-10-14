package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.dto.NavstevaOsobaDto;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.service.NavstevaOsobaService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class NavstevaOsobaController extends BaseController {

    final static Logger logger = LogManager.getLogger(RidicController.class);

    @Autowired
    private NavstevaOsobaService navstevaOsobaService;

    @PostMapping("/navsteva-osoba/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')") //TODO: popřemýšlet, jestli zde nedat omezení na roli správy návštěvních lístku: @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<NavstevaOsobaDto> save(@RequestBody @Valid NavstevaOsobaDto navstevaOsobaDto) throws UniqueValueException, NoSuchMessageException {
        NavstevaOsoba newNavstevaOsoba = navstevaOsobaService.create(navstevaOsobaDto.toEntity());
        return ResponseEntity.ok(new NavstevaOsobaDto(newNavstevaOsoba));
    }

    @GetMapping("/navsteva-osoba/list-all")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<List<NavstevaOsobaDto>> listAll() {
        List<NavstevaOsobaDto> navstevaOsobaDtos = navstevaOsobaService.list().stream()
            .map(NavstevaOsobaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(navstevaOsobaDtos);
    }

    @GetMapping("/navsteva-osoba/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<NavstevaOsobaDto> getDetail(@RequestParam String idNavstevaOsoba) {
        NavstevaOsoba navstevaOsoba = navstevaOsobaService.getDetail(idNavstevaOsoba);
        if (navstevaOsoba == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NavstevaOsobaDto(navstevaOsoba));
    }

    @GetMapping("/navsteva-osoba/list-by-cislo-op")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<NavstevaOsobaDto> getByCisloOp(@RequestParam String cisloOp) {
        NavstevaOsoba navstevaOsoba = navstevaOsobaService.getByCisloOp(cisloOp);
        if (navstevaOsoba == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NavstevaOsobaDto(navstevaOsoba));
    }
    
}
