package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.ZadostKlicDto;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.service.ZadostKlicService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class ZadostKlicController extends BaseController{

    final static Logger logger = LogManager.getLogger(KlicController.class);

    @Autowired
    private ZadostKlicService zadostKlicService;

    @PostMapping("/zadost-klic/save")
    public ResponseEntity<ZadostKlicDto> saveKey(@RequestBody @Valid ZadostKlicDto zadostKlicDto) {
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
        List<ZadostKlicDto> zadostiKlic = zadostKlicService.getZadostiByKlic(stav).stream()
            .map(ZadostKlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(zadostiKlic);
        
    }
    

}
