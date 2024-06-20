package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.dto.RidicDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.service.PovoleniVjezduVozidlaService;
import cz.diamo.vratnice.service.RidicService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class PovoleniVjezduVozidlaController extends BaseController {

    final static Logger logger = LogManager.getLogger(PovoleniVjezduVozidlaController.class);

    @Autowired
    private PovoleniVjezduVozidlaService povoleniVjezduVozidlaService;

    @Autowired
    private RidicService ridicService;

    @PostMapping("/povoleni-vjezdu-vozidla/save")
    public ResponseEntity<PovoleniVjezduVozidlaDto> save(@RequestBody @Valid PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto) {
        // Uložení řidiče, pokud je vyplněn
        if (povoleniVjezduVozidlaDto.getRidic() != null) {
            Ridic savedRidic =  ridicService.create(povoleniVjezduVozidlaDto.getRidic().toEntity());
            povoleniVjezduVozidlaDto.setRidic(new RidicDto(savedRidic));
        }
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaService.create(povoleniVjezduVozidlaDto.toEntity());
        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla));
    }

    @GetMapping("/povoleni-vjezdu-vozidla/list-all")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> listAll() {
        List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidel = povoleniVjezduVozidlaService.getAll().stream()
            .map(PovoleniVjezduVozidlaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(povoleniVjezduVozidel);
    }

    @GetMapping("/povoleni-vjezdu-vozidla/detail")
    public ResponseEntity<PovoleniVjezduVozidlaDto> getDetail(@RequestParam String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaService.getDetail(idPovoleniVjezduVozidla);
        if (povoleniVjezduVozidla == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla));
    }
    

    @GetMapping("/povoleni-vjezdu-vozidla/get-by-stav")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> getByStav(@RequestParam String stav) {
        List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidel = povoleniVjezduVozidlaService.getByStav(stav).stream()
            .map(PovoleniVjezduVozidlaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(povoleniVjezduVozidel);
    }
    
    

}
