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
import java.util.Date;



@RestController
public class HistorieVypujcekController extends BaseController {

    final static Logger logger = LogManager.getLogger(HistorieVypujcekController.class);

    @Autowired
    private HistorieVypujcekService historieVypujcekService;

    @Autowired
    private ZadostKlicService zadostKlicService;

    @Autowired
    private KlicService KlicService;

    @PostMapping("/historie-vypujcek/save")
    public ResponseEntity<HistorieVypujcekDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid ZadostKlicDto zadostKlicDto, @RequestParam String stav) {
        HistorieVypujcek historieVypujcek = new HistorieVypujcek();
        
        ZadostKlic zadostKlic = zadostKlicDto.toEntity();
        
        historieVypujcek.setZadostKlic(zadostKlic);
        historieVypujcek.setStav(stav);
        historieVypujcek.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        
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
        Klic klic = KlicService.getDetail(idKlic);
        List<ZadostKlic> zadostKlicList = zadostKlicService.findByKlic(klic);

        List<HistorieVypujcekDto> historieVypujcekDtos = zadostKlicList.stream()
            .flatMap(zadostKlic -> historieVypujcekService.findByZadostKlic(zadostKlic).stream())
            .map(HistorieVypujcekDto::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(historieVypujcekDtos);
    }
    
    
    
}
