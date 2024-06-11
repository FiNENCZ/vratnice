package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.SluzebniVozidloDto;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.service.SluzebniVozidloService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class SluzebniVozidloController extends BaseController {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloController.class);

    @Autowired
    private SluzebniVozidloService sluzebniVozidloService;

    @PostMapping("/sluzebni-vozidlo/save")
    public ResponseEntity<SluzebniVozidloDto> save(@RequestBody @Valid SluzebniVozidloDto sluzebniVozidloDto) {
        SluzebniVozidlo newSluzebniVozidlo = sluzebniVozidloService.create(sluzebniVozidloDto.toEntity());
        return ResponseEntity.ok(new SluzebniVozidloDto(newSluzebniVozidlo));
    }
    
    @GetMapping("/sluzebni-vozidlo/list-all")
    public ResponseEntity<List<SluzebniVozidloDto>> listAll() {
        List<SluzebniVozidloDto> sluzebniVozidla = sluzebniVozidloService.getAll().stream()
            .map(SluzebniVozidloDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(sluzebniVozidla);
    }

    @GetMapping("/sluzebni-vozidlo/detail")
    public ResponseEntity<SluzebniVozidloDto> getDetail(@RequestParam String id) {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloService.getDetail(id);
        if (sluzebniVozidlo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new SluzebniVozidloDto(sluzebniVozidlo));
    }

    @GetMapping("/sluzebni-vozidlo/dle-stavu")
    public  ResponseEntity<List<SluzebniVozidloDto>> getSluzebniVozidloByStav(@RequestParam String stav) {
        List<SluzebniVozidloDto> sluzebniVozidla = sluzebniVozidloService.getSluzebniVozidloByStav(stav).stream()
            .map(SluzebniVozidloDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(sluzebniVozidla);
    }

    
}
