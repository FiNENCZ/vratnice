package cz.dp.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.dto.HistorieVypujcekDto;
import cz.dp.vratnice.dto.ZadostKlicDto;
import cz.dp.vratnice.entity.HistorieVypujcek;
import cz.dp.vratnice.entity.Klic;
import cz.dp.vratnice.entity.ZadostKlic;
import cz.dp.vratnice.enums.HistorieVypujcekAkceEnum;
import cz.dp.vratnice.service.HistorieVypujcekService;
import cz.dp.vratnice.service.KlicService;
import cz.dp.vratnice.service.ZadostKlicService;
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
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYPUJCEK_KLICU')")
    public ResponseEntity<HistorieVypujcekDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                                @RequestBody @Valid ZadostKlicDto zadostKlicDto, 
                                @RequestParam HistorieVypujcekAkceEnum akce,
                                HttpServletRequest request) throws NoSuchMessageException, BaseException {  

        HistorieVypujcek newHistorieVypujcek = historieVypujcekService.create(zadostKlicDto.toEntity(), appUserDto, akce, request);
        return ResponseEntity.ok(new HistorieVypujcekDto(newHistorieVypujcek));
    }

    @PostMapping("/historie-vypujcek/vratit-klic-by-rfid")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYPUJCEK_KLICU')")
    public ResponseEntity<HistorieVypujcekDto> vratitKlicByRfid(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                        @RequestParam String rfid,
                        HttpServletRequest request) throws NoSuchMessageException, BaseException {

        HistorieVypujcek newHistorieVypujcek = historieVypujcekService.vratitKlicByRfid(rfid, appUserDto, request);
        return ResponseEntity.ok(new HistorieVypujcekDto(newHistorieVypujcek));

    }
    
    @GetMapping("/historie-vypujcek/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYPUJCEK_KLICU')")
    public ResponseEntity<List<HistorieVypujcekDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam @Nullable String idKlic, @RequestParam @Nullable String idZadostKlic) throws RecordNotFoundException, NoSuchMessageException {
        List<HistorieVypujcekDto> result = new ArrayList<HistorieVypujcekDto>();
        List<HistorieVypujcek> list = historieVypujcekService.getList(idKlic, idZadostKlic, appUserDto);

        if (list != null && list.size() > 0) {
            for (HistorieVypujcek vypujcka : list) {
                result.add(new HistorieVypujcekDto(vypujcka));
            }
        }

        return ResponseEntity.ok(result);
    }



    @GetMapping("/historie-vypujcek/list-by-zadost")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYPUJCEK_KLICU')")
    public ResponseEntity<List<HistorieVypujcekDto>> listByZadost(@RequestParam String idZadostiKlic) throws RecordNotFoundException, NoSuchMessageException {
        ZadostKlic zadostKlicEntity = zadostKlicService.getDetail(idZadostiKlic);
        List<HistorieVypujcekDto> historieVypujcekDtos = historieVypujcekService.findByZadostKlic(zadostKlicEntity).stream()
            .map(HistorieVypujcekDto::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(historieVypujcekDtos);
    }

    @GetMapping("historie-vypujcek/list-by-id-klic")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYPUJCEK_KLICU')")
    public ResponseEntity<List<HistorieVypujcekDto>> listByIdKlic(@RequestParam String idKlic) throws RecordNotFoundException, NoSuchMessageException {
        Klic klic = klicService.getDetail(idKlic);
        List<ZadostKlic> zadostKlicList = zadostKlicService.findByKlic(klic);

        List<HistorieVypujcekDto> historieVypujcekDtos  = new ArrayList<HistorieVypujcekDto>();

        if (zadostKlicList != null) {
            for (ZadostKlic zadostKlic : zadostKlicList) {
                List<HistorieVypujcek> historieVypujcekList = historieVypujcekService.findByZadostKlic(zadostKlic);

                if (historieVypujcekList != null) {
                    for (HistorieVypujcek historieVypujcek : historieVypujcekList) {
                        historieVypujcekDtos.add(new HistorieVypujcekDto(historieVypujcek));
                    }
                }
            }
        }

        return ResponseEntity.ok(historieVypujcekDtos);
    }

    @GetMapping("historie-vypujcek/list-nevracene-klice")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYPUJCEK_KLICU')")
    public ResponseEntity<List<HistorieVypujcekDto>> listNevraceneKlice(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        List<HistorieVypujcekDto> result = new ArrayList<HistorieVypujcekDto>();
        List<HistorieVypujcek> list = historieVypujcekService.listNevraceneKlice(appUserDto);

        if (list != null && list.size() > 0) {
            for (HistorieVypujcek vypujcka : list) {
                result.add(new HistorieVypujcekDto(vypujcka));
            }
        }

        return ResponseEntity.ok(result);
    }
}
