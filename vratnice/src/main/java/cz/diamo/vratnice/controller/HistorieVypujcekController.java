package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.HistorieVypujcekDto;
import cz.diamo.vratnice.dto.ZadostKlicDto;
import cz.diamo.vratnice.dto.ZadostStavDto;
import cz.diamo.vratnice.entity.HistorieVypujcek;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.entity.ZadostStav;
import cz.diamo.vratnice.enums.HistorieVypujcekAkceEnum;
import cz.diamo.vratnice.service.HistorieVypujcekService;
import cz.diamo.vratnice.service.KlicService;
import cz.diamo.vratnice.service.ZadostKlicService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class HistorieVypujcekController extends BaseController {

    final static Logger logger = LogManager.getLogger(HistorieVypujcekController.class);

    @Autowired
    private HistorieVypujcekService historieVypujcekService;

    @Autowired
    private ZadostKlicService zadostKlicService;

    @Autowired
    private KlicService klicService;


    @PostMapping("/historie-vypujcek/save")
    public ResponseEntity<HistorieVypujcekDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                                @RequestBody @Valid ZadostKlicDto zadostKlicDto, 
                                @RequestParam HistorieVypujcekAkceEnum akce,
                                HttpServletRequest request) throws NoSuchMessageException, BaseException {  
    
        HistorieVypujcek newHistorieVypujcek = historieVypujcekService.create(zadostKlicDto.toEntity(), appUserDto, akce, request);
        return ResponseEntity.ok(new HistorieVypujcekDto(newHistorieVypujcek));
    }

    @PostMapping("/historie-vypujcek/vratit-klic-by-rfid")
    public ResponseEntity<HistorieVypujcekDto> vratitKlicByRfid(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                        @RequestParam String rfid,
                        HttpServletRequest request) throws NoSuchMessageException, BaseException {

        HistorieVypujcek newHistorieVypujcek = historieVypujcekService.vratitKlicByRfid(rfid, appUserDto, request);
        return ResponseEntity.ok(new HistorieVypujcekDto(newHistorieVypujcek));

    }
    
    @GetMapping("/historie-vypujcek/list")
    public ResponseEntity<List<HistorieVypujcekDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam @Nullable String idKlic, @RequestParam @Nullable String idZadostKlic) throws RecordNotFoundException, NoSuchMessageException {
        List<HistorieVypujcekDto> result = new ArrayList<HistorieVypujcekDto>();
        List<HistorieVypujcek> list = historieVypujcekService.getList(idKlic, idZadostKlic, appUserDto);

        if (list != null && list.size() > 0) {
            for (HistorieVypujcek vypujcka : list) {
                vypujcka.setAkce(historieVypujcekService.getHistorieVypujcekAkce(vypujcka.getIdHistorieVypujcek()));
                result.add(new HistorieVypujcekDto(vypujcka));
            }
        }

        return ResponseEntity.ok(result);
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

    @GetMapping("historie-vypujcek/list-nevracene-klice")
    public ResponseEntity<List<HistorieVypujcekDto>> listNevraceneKlice(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) {
        List<HistorieVypujcekDto> result = new ArrayList<HistorieVypujcekDto>();
        List<HistorieVypujcek> list = historieVypujcekService.listNevraceneKlice(appUserDto);

        if (list != null && list.size() > 0) {
            for (HistorieVypujcek vypujcka : list) {
                vypujcka.setAkce(historieVypujcekService.getHistorieVypujcekAkce(vypujcka.getIdHistorieVypujcek()));
                result.add(new HistorieVypujcekDto(vypujcka));
            }
        }

        return ResponseEntity.ok(result);
    }
    

    public ResponseEntity<ZadostStavDto> stav (@RequestParam String idZadostKlic) {
        ZadostStav stav = zadostKlicService.getZadostStav(idZadostKlic);
        return ResponseEntity.ok(new ZadostStavDto(stav));
    }
    
}
