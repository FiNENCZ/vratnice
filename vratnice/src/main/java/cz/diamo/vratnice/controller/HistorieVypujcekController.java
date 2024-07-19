package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.dto.HistorieVypujcekDto;
import cz.diamo.vratnice.dto.ZadostKlicDto;
import cz.diamo.vratnice.entity.HistorieVypujcek;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.service.HistorieVypujcekService;
import cz.diamo.vratnice.service.KlicService;
import cz.diamo.vratnice.service.ZadostKlicService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;



@RestController
public class HistorieVypujcekController extends BaseController {

    final static Logger logger = LogManager.getLogger(HistorieVypujcekController.class);

    @Autowired
    private HistorieVypujcekService historieVypujcekService;

    @Autowired
    private ZadostKlicService zadostKlicService;

    @Autowired
    private KlicService klicService;

    @Autowired
    private UzivatelServices uzivatelServices;

    @PostMapping("/historie-vypujcek/save")
    public ResponseEntity<HistorieVypujcekDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid ZadostKlicDto zadostKlicDto, @RequestParam String stav) throws RecordNotFoundException {
        //Změna stavu i klíče
        String klicId = zadostKlicDto.getKlic().getId();
        Klic klic = klicService.getDetail(klicId);

        if (klic == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Vytvoření historie výpůjčky
        HistorieVypujcek historieVypujcek = new HistorieVypujcek();
        Uzivatel vratny = uzivatelServices.getDetail(appUserDto.getIdUzivatel());
        
        ZadostKlic zadostKlic = zadostKlicDto.toEntity();
        
        historieVypujcek.setZadostKlic(zadostKlic);
        historieVypujcek.setStav(stav);
        historieVypujcek.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        historieVypujcek.setVratny(vratny);
        
        HistorieVypujcek newHistorieVypujcek = historieVypujcekService.create(historieVypujcek);
        return ResponseEntity.ok(new HistorieVypujcekDto(newHistorieVypujcek));
    }


    @GetMapping("/historie-vypujcek/list-by-zadost")
    public ResponseEntity<List<HistorieVypujcekDto>> listByZadost(@RequestParam String idZadostiKlic) {
        ZadostKlic zadostKlicEntity = zadostKlicService.getDetail(idZadostiKlic);
        List<HistorieVypujcekDto> historieVypujcekDtos = historieVypujcekService.findByZadostKlic(zadostKlicEntity).stream()
            .map(HistorieVypujcekDto::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(historieVypujcekDtos);
    }

    @GetMapping("historie-vypujcek/list-by-id-klic")
    public ResponseEntity<List<HistorieVypujcekDto>> listByIdKlic(@RequestParam String idKlic) {
        Klic klic = klicService.getDetail(idKlic);
        List<ZadostKlic> zadostKlicList = zadostKlicService.findByKlic(klic);

        List<HistorieVypujcekDto> historieVypujcekDtos = zadostKlicList.stream()
            .flatMap(zadostKlic -> historieVypujcekService.findByZadostKlic(zadostKlic).stream())
            .map(HistorieVypujcekDto::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(historieVypujcekDtos);
    }

    @GetMapping("historie-vypujcek/last-vypujcka-by-id-zadost-klic")
    public ResponseEntity<HistorieVypujcekDto> lastVypujckaByIdZadostKlic(@RequestParam String idZadostKlic) {

        ZadostKlic zadostKlic = zadostKlicService.getDetail(idZadostKlic);
        if (zadostKlic == null) {
            return ResponseEntity.notFound().build();
        }

        // Načtení historie vypůjček pro daný ZadostKlic
        List<HistorieVypujcekDto> historieVypujcekDtos = historieVypujcekService.findByZadostKlic(zadostKlic).stream()
            .map(HistorieVypujcekDto::new)
            .collect(Collectors.toList());

        if (historieVypujcekDtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Najdeme nejnovější záznam podle data
        HistorieVypujcekDto nejnovejsiVypujcka = historieVypujcekDtos.stream()
            .max(Comparator.comparing(HistorieVypujcekDto::getDatum))
            .orElse(null);

        if (nejnovejsiVypujcka == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(nejnovejsiVypujcka);
    }
    
    
    
    
}
