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
import cz.diamo.vratnice.dto.UzivatelVratniceDto;
import cz.diamo.vratnice.entity.UzivatelVratnice;
import cz.diamo.vratnice.service.UzivatelVratniceService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class UzivatelVratniceController extends BaseController {

    final static Logger logger = LogManager.getLogger(NavstevniListekController.class);

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @PostMapping("/uzivatel-vratnice/save")
    public ResponseEntity<UzivatelVratniceDto> save(@RequestBody @Valid UzivatelVratniceDto uzivatelVratniceDto) {
        UzivatelVratnice uzivatelVratnice = uzivatelVratniceService.save(uzivatelVratniceDto.toEntity());
        return ResponseEntity.ok(new UzivatelVratniceDto(uzivatelVratnice));
    }
    

    @GetMapping("/uzivatel-vratnice/list")
    public ResponseEntity<List<UzivatelVratniceDto>> list(@RequestParam @Nullable Boolean aktivni) {
        List<UzivatelVratniceDto> result = new ArrayList<UzivatelVratniceDto>();
        List<UzivatelVratnice> list = uzivatelVratniceService.getList(aktivni);

        if (list != null && list.size() > 0) {
            for (UzivatelVratnice uzivatelVratnice : list) {
                result.add(new UzivatelVratniceDto(uzivatelVratnice));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/uzivatel-vratnice/detail")
    public ResponseEntity<UzivatelVratniceDto> getDetail(@RequestParam String idUzivatelVratnice) {
        UzivatelVratnice uzivatelVratnice = uzivatelVratniceService.getDetail(idUzivatelVratnice);
        if (uzivatelVratnice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UzivatelVratniceDto(uzivatelVratnice));
    }
}
