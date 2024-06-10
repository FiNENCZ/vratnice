package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.dto.ZadostKlicDto;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.service.ZadostKlicService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ZadostKlicController extends BaseController{

    final static Logger logger = LogManager.getLogger(ZadostKlicController.class);
    private int MAX_POCET_VYPUJCEK = 15;

    @Autowired
    private ZadostKlicService zadostKlicService;

    @Autowired
    private UzivatelServices uzivatelServices;


    @PostMapping("/zadost-klic/save")
    public ResponseEntity<?> saveKey(@RequestBody @Valid ZadostKlicDto zadostKlicDto) {
        // kontrola zda nebylo přesaženo max počtu výpůjček
        ZadostKlic zadostKlicEntity = zadostKlicDto.toEntity();
        Uzivatel uzivatel = zadostKlicEntity.getUzivatel();
        long pocetVypujcek = zadostKlicService.countByUzivatel(uzivatel);
        int dostupnychVypujcek = MAX_POCET_VYPUJCEK - (int) pocetVypujcek;

        // úprava existující žádosti
        String idZadostiKey = zadostKlicDto.getIdZadostiKey();
        if (idZadostiKey != null && !idZadostiKey.isEmpty()) {
            ZadostKlic newZadostKlic = zadostKlicService.create(zadostKlicEntity);
            return ResponseEntity.ok(new ZadostKlicDto(newZadostKlic));
        }

        // nová žádost
        if(dostupnychVypujcek > 0) {
            //vytvoření záznamu žádosti klíče
            ZadostKlic newZadostKlic = zadostKlicService.create(zadostKlicEntity);
            return ResponseEntity.ok(new ZadostKlicDto(newZadostKlic));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Žádost nelze provést, protože bylo dosaženo maximálního počtu žádostí o klíče.");
        }
    }

    @PostMapping("/zadost-klic/zmena-stavu")
    public ResponseEntity<ZadostKlicDto> zmenaStavuZadosti(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid ZadostKlicDto zadostKlicDto, @RequestParam String stav) {

        zadostKlicDto.setStav(stav);
        ZadostKlic newZadostKlic = zadostKlicService.create(zadostKlicDto.toEntity());
        return ResponseEntity.ok(new ZadostKlicDto(newZadostKlic));
    }
    

    @GetMapping("/zadosti-klic/list-all")
    public ResponseEntity<List<ZadostKlicDto>> getAll() {
        List<ZadostKlicDto> zadostiKlic = zadostKlicService.getAll().stream()
            .map(ZadostKlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(zadostiKlic);
    }

    @GetMapping("/zadosti-klic/detail")
    public ResponseEntity<ZadostKlicDto> getDetail(@RequestParam String idZadostKey) {
        ZadostKlic zadostKlic = zadostKlicService.getDetail(idZadostKey);
        if (zadostKlic == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ZadostKlicDto(zadostKlic));
    }

    @GetMapping("/zadost-klic/zadosti-dle-stavu")
    public  ResponseEntity<List<ZadostKlicDto>> getZadostiByKlic(@RequestParam String stav) {
        List<ZadostKlicDto> zadostiKlic = zadostKlicService.getZadostiByStav(stav).stream()
            .map(ZadostKlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(zadostiKlic);
    }

    @GetMapping("/zadost-klic/list-by-id-uzivatel")
    public ResponseEntity<List<ZadostKlicDto>> listByIdUzivatel(@RequestParam String idUzivatel) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(idUzivatel);

        if (uzivatel == null) {
            return ResponseEntity.notFound().build();
        }

        List<ZadostKlicDto> zadostiKlic = zadostKlicService.findByUzivatel(uzivatel).stream()
            .map(ZadostKlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(zadostiKlic);

    }

    @GetMapping("/zadost-klic/vypujcek-k-dispozici")
    public ResponseEntity<Integer> getPocetVypujcekByUzivatel(@RequestParam String idUzivatel) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(idUzivatel);
        if (uzivatel == null) {
            return ResponseEntity.notFound().build();
        }
        long pocetVypujcek = zadostKlicService.countByUzivatel(uzivatel);

        int dostupnychVypujcek = MAX_POCET_VYPUJCEK - (int) pocetVypujcek;


        return ResponseEntity.ok(dostupnychVypujcek);
    }
    
    

}
