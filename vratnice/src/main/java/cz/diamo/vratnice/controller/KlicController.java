package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.vratnice.dto.KlicDto;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.service.KlicService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class KlicController extends BaseController {

    final static Logger logger = LogManager.getLogger(KlicController.class);

    @Autowired
    private KlicService klicService;

    @PostMapping("/klic/save")
    public ResponseEntity<KlicDto> save(@RequestBody @Valid KlicDto keyDto) {
        Klic newKey = klicService.createKey(keyDto.toEntity());
        return ResponseEntity.ok(new KlicDto(newKey));
    }

    @GetMapping("/klic/list")
    public ResponseEntity<List<KlicDto>> list(@RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable Boolean specialni) {
        List<KlicDto> result = new ArrayList<KlicDto>();
        List<Klic> list = klicService.getList(aktivni, specialni);

        if (list != null && list.size() > 0) {
            for (Klic klic : list) {
                result.add(new KlicDto(klic));
            }
        }

        return ResponseEntity.ok(result);
    }


    @GetMapping("/klic/detail")
    public ResponseEntity<KlicDto> getDetail(@RequestParam String idKey) {
        Klic key = klicService.getDetail(idKey);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new KlicDto(key));
    }

    @PostMapping("/klic/toggle-aktivita")
    public ResponseEntity<KlicDto> toggleAktivita(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid KlicDto klicDto) {
        klicDto.setAktivita(!klicDto.getAktivita());
        

        Boolean aktualniAktivita = klicDto.getAktivita();
        if (aktualniAktivita == true) {
            klicDto.setState("dostupný");
        } else {
            klicDto.setState("odstraněno");
        }

        Klic newKlic = klicService.createKey(klicDto.toEntity());

        return ResponseEntity.ok(new KlicDto(newKlic));
    }
    
}
