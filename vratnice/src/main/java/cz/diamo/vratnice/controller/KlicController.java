package cz.diamo.vratnice.controller;

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
    private KlicService keyService;

    @PostMapping("/key/create-key")
    public ResponseEntity<KlicDto> createKey(@RequestBody @Valid KlicDto keyDto) {
        Klic newKey = keyService.createKey(keyDto.toEntity());
        return ResponseEntity.ok(new KlicDto(newKey));
    }

    @GetMapping("/key/list-all")
    public ResponseEntity<List<KlicDto>> getAllKeys() {
        List<KlicDto> keys = keyService.getAllKeys().stream()
            .map(KlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(keys);
    }

    @GetMapping("/key/list-by-aktivita")
    public ResponseEntity<List<KlicDto>> listByAktivita(@RequestParam Boolean aktivita) {
        List<KlicDto> sluzebniVozidla = keyService.getKlicByAktivita(aktivita).stream()
            .map(KlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(sluzebniVozidla);
    }

    @GetMapping("key/list-by-specialni")
    public ResponseEntity<List<KlicDto>> listBySpecialni(@RequestParam Boolean specialni) {
        List<KlicDto> keys = keyService.getBySpecialni(specialni).stream()
            .map(KlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(keys);
    }
    

    @GetMapping("/key/detail")
    public ResponseEntity<KlicDto> getDetail(@RequestParam String idKey) {
        Klic key = keyService.getDetail(idKey);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new KlicDto(key));
    }

    @PostMapping("/key/toggle-aktivita")
    public ResponseEntity<KlicDto> toggleAktivita(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid KlicDto klicDto) {
        klicDto.setAktivita(!klicDto.getAktivita());
        Klic newKlic = keyService.createKey(klicDto.toEntity());

        return ResponseEntity.ok(new KlicDto(newKlic));
    }
    
}
