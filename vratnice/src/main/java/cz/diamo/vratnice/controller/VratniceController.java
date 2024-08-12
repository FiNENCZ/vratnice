package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.NavstevniListekTypDto;
import cz.diamo.vratnice.dto.VratniceDto;
import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.service.VratniceService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class VratniceController extends BaseController {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloController.class);

    @Autowired
    private VratniceService vratniceService;

    @PostMapping("/vratnice/save")
    public ResponseEntity<VratniceDto> save(@RequestBody @Valid VratniceDto vratniceDto) {
        Vratnice vratnice = vratniceService.save(vratniceDto.toEntity());
        return ResponseEntity.ok(new VratniceDto(vratnice));
    }
    
    @GetMapping("/vratnice/list")
    public ResponseEntity<List<VratniceDto>> list(@RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idLokalita) {
        List<VratniceDto> result = new ArrayList<VratniceDto>();
        List<Vratnice> list = vratniceService.getList(aktivni, idLokalita);

        if (list != null && list.size() > 0) {
            for (Vratnice vratnice : list) {
                result.add(new VratniceDto(vratnice));
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vratnice/detail")
    public ResponseEntity<VratniceDto> getDetail(@RequestParam String id) {
        Vratnice vratnice = vratniceService.getDetail(id);
        if (vratnice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new VratniceDto(vratnice));
    }

    @GetMapping("/vratnice/vstupni-karty-typ")
    public ResponseEntity<NavstevniListekTypDto> vstupniKarty(@RequestParam String idVratnice) {
        NavstevniListekTyp vstupniKartyTyp = vratniceService.getVstupniKartyTyp(idVratnice);
        return ResponseEntity.ok(new NavstevniListekTypDto(vstupniKartyTyp));
    }
     

}
