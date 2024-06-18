package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.HistorieSluzebniVozidloDto;

import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.service.HistorieSluzebniVozidloService;
import cz.diamo.vratnice.service.SluzebniVozidloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class HistorieSluzebniVozidloController extends BaseController {

    final static Logger logger = LogManager.getLogger(HistorieSluzebniVozidloController.class);

    @Autowired
    private HistorieSluzebniVozidloService historieSluzebniVozidloService;

    @Autowired
    private SluzebniVozidloService sluzebniVozidloService;

    

    @GetMapping("/historie-sluzebni-vozidlo/list-by-sluzebni-vozidlo")
    public ResponseEntity<List<HistorieSluzebniVozidloDto>> listBySluzebniVozidlo(@RequestParam String idSluzebniVozidlo) {
        SluzebniVozidlo sluzebniVozidloEntity = sluzebniVozidloService.getDetail(idSluzebniVozidlo);
        List<HistorieSluzebniVozidloDto> historieSluzebniVozidloDtos = historieSluzebniVozidloService.findBySluzebniVozidlo(sluzebniVozidloEntity).stream()
            .map(HistorieSluzebniVozidloDto::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(historieSluzebniVozidloDtos);
    }
    
    

}
