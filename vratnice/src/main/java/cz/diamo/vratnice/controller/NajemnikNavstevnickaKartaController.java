package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.NajemnikNavstevnickaKartaDto;
import cz.diamo.vratnice.entity.NajemnikNavstevnickaKarta;
import cz.diamo.vratnice.service.NajemnikNavstevnickaKartaService;
import jakarta.validation.Valid;

@RestController
public class NajemnikNavstevnickaKartaController extends BaseController {

    final static Logger logger = LogManager.getLogger(NajemnikNavstevnickaKartaController.class);

    @Autowired
    private NajemnikNavstevnickaKartaService najemnikNavstevnickaKartaService;

     @PostMapping("/najemnik-navstevnicka-karta/save")
    public ResponseEntity<NajemnikNavstevnickaKartaDto> save(@RequestBody @Valid NajemnikNavstevnickaKartaDto najemnikNavstevnickaKartaDto) {
        NajemnikNavstevnickaKarta najemnikNavstevnickaKarta = najemnikNavstevnickaKartaService.create(najemnikNavstevnickaKartaDto.toEntity());
        return ResponseEntity.ok(new NajemnikNavstevnickaKartaDto(najemnikNavstevnickaKarta));
    }

    @GetMapping("/najemnik-navstevnicka-karta/list")
    public ResponseEntity<List<NajemnikNavstevnickaKartaDto>> list() {
        List<NajemnikNavstevnickaKartaDto> najemnikNavstevnickaKartaDtos = najemnikNavstevnickaKartaService.list().stream()
            .map(NajemnikNavstevnickaKartaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(najemnikNavstevnickaKartaDtos);
    }

    @GetMapping("/najemnik-navstevnicka-karta/detail")
    public ResponseEntity<NajemnikNavstevnickaKartaDto> getDetail(@RequestParam String idNajemnikNavstevnickaKarta) {
        NajemnikNavstevnickaKarta najemnikNavstevnickaKarta = najemnikNavstevnickaKartaService.getDetail(idNajemnikNavstevnickaKarta);
        if (najemnikNavstevnickaKarta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NajemnikNavstevnickaKartaDto(najemnikNavstevnickaKarta));
    }

    @GetMapping("/najemnik-navstevnicka-karta/list-by-cislo-op")
    public ResponseEntity<NajemnikNavstevnickaKartaDto> getByCisloOp(@RequestParam String cisloOp) {
        NajemnikNavstevnickaKarta najemnikNavstevnickaKarta = najemnikNavstevnickaKartaService.getByCisloOp(cisloOp);
        if (najemnikNavstevnickaKarta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NajemnikNavstevnickaKartaDto(najemnikNavstevnickaKarta));
    }


}
