package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
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
    public ResponseEntity<NavstevaOsobaDto> save(@RequestBody @Valid NavstevaOsobaDto navstevaOsobaDto) {
        NavstevaOsoba newNavstevaOsoba = navstevaOsobaService.create(navstevaOsobaDto.toEntity());
        return ResponseEntity.ok(new NavstevaOsobaDto(newNavstevaOsoba));
    }

    @GetMapping("/navsteva-osoba/list-all")
    public ResponseEntity<List<NavstevaOsobaDto>> listAll() {
        List<NavstevaOsobaDto> navstevaOsobaDtos = navstevaOsobaService.list().stream()
            .map(NavstevaOsobaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(navstevaOsobaDtos);
    }

    @GetMapping("/navsteva-osoba/detail")
    public ResponseEntity<NavstevaOsobaDto> getDetail(@RequestParam String idNavstevaOsoba) {
        NavstevaOsoba navstevaOsoba = navstevaOsobaService.getDetail(idNavstevaOsoba);
        if (navstevaOsoba == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NavstevaOsobaDto(navstevaOsoba));
    }

    @GetMapping("/navsteva-osoba/list-by-cislo-op")
    public ResponseEntity<NavstevaOsobaDto> getByCisloOp(@RequestParam String cisloOp) {
        NavstevaOsoba navstevaOsoba = navstevaOsobaService.getRidicByCisloOp(cisloOp);
        if (navstevaOsoba == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NavstevaOsobaDto(navstevaOsoba));
    }
    
}
