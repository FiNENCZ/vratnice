package cz.diamo.vratnice.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    @Autowired
    private ZadostKlicService zadostKlicService;

    @PostMapping("/zadost-klic/save")
    public ResponseEntity<ZadostKlicDto> saveKey(@RequestBody @Valid ZadostKlicDto zadostKlicDto) {
        ZadostKlic newZadostKlic = zadostKlicService.create(zadostKlicDto.toEntity());
        return ResponseEntity.ok(new ZadostKlicDto(newZadostKlic));
    }

    @PostMapping("/zadost-klic/zmena-stavu")
    public ResponseEntity<ZadostKlicDto> zmenaStavuZadosti(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid ZadostKlicDto zadostKlicDto, @RequestParam String stav) {
            // Získání jména uživatele
        String userName = appUserDto.getName();
        System.out.println(userName);

        // Zápis jména uživatele do souboru
        try {
            Files.write(Paths.get("userTest.txt"), userName.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    

}
