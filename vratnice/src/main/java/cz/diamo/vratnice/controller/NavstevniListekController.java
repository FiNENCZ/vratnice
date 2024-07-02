package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.dto.NavstevaOsobaDto;
import cz.diamo.vratnice.dto.NavstevniListekDto;
import cz.diamo.vratnice.dto.NavstevniListekTypDto;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;
import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.service.NavstevaOsobaService;
import cz.diamo.vratnice.service.NavstevniListekService;
import cz.diamo.vratnice.service.QrCodeService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class NavstevniListekController extends BaseController {

    final static Logger logger = LogManager.getLogger(NavstevniListekController.class);

    @Autowired
    private NavstevniListekService navstevniListekService;

    @Autowired
    private NavstevaOsobaService navstevaOsobaService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private UzivatelServices uzivatelService;


    @PostMapping("/navstevni-listek/create")
    public ResponseEntity<NavstevniListekDto> save(@RequestBody @Valid NavstevniListekDto navstevniListekDto) {
        if (navstevniListekDto.getNavstevaOsoba() != null && !navstevniListekDto.getNavstevaOsoba().isEmpty()) {
    
            List<NavstevaOsoba> navstevaOsobaEntities = navstevniListekDto.getNavstevaOsoba().stream()
                .map(NavstevaOsobaDto::toEntity)
                .collect(Collectors.toList());
    
            List<NavstevaOsoba> savedNavstevaOsoby = navstevaOsobaEntities.stream()
                .map(navstevaOsobaService::create)
                .collect(Collectors.toList());
    
            navstevniListekDto.setNavstevaOsoba(savedNavstevaOsoby.stream()
                .map(NavstevaOsobaDto::new)
                .collect(Collectors.toList()));
        }
    
        NavstevniListek navstevniListek = navstevniListekService.create(navstevniListekDto.toEntity());
        return ResponseEntity.ok(new NavstevniListekDto(navstevniListek));
    }
    

    @GetMapping("/navstevni-listek/list-all")
    public ResponseEntity<List<NavstevniListekDto>> getAll() {
        List<NavstevniListekDto> navstevniListky = navstevniListekService.getAll().stream()
            .map(NavstevniListekDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(navstevniListky);
    }

    @GetMapping("/navstevni-listek/detail")
    public ResponseEntity<NavstevniListekDto> getDetail(@RequestParam String idNavstevniListek) {
        NavstevniListek navstevniListek = navstevniListekService.getDetail(idNavstevniListek);
        if (navstevniListek == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NavstevniListekDto(navstevniListek));
    }
    
    
    @GetMapping("/navstevni-listek/get-by-uzivatel")
    public ResponseEntity<List<NavstevniListekDto>> getNavstevniListkyByUzivatel(@RequestParam String uzivatelId) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelService.getDetail(uzivatelId); 
        List<NavstevniListekDto> navstevniListky = navstevniListekService.getNavstevniListkyByUzivatel(uzivatel).stream()
            .map(NavstevniListekDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(navstevniListky);
    }

    @GetMapping("/navstevni-listek/get-by-navsteva-osoba")
    public ResponseEntity<List<NavstevniListekDto>> getNavstevniListkyByNavstevaOsoba(@RequestParam String navstevaOsobaId) throws RecordNotFoundException, NoSuchMessageException {
        NavstevaOsoba navstevaOsoba = navstevaOsobaService.getDetail(navstevaOsobaId);
        List<NavstevniListekDto> navstevniListky = navstevniListekService.getNavstevniListkyByNavstevaOsoba(navstevaOsoba).stream()
            .map(NavstevniListekDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(navstevniListky);
    }

    @GetMapping("/navstevni-listek/qrcode")
    public ResponseEntity<byte[]> getQRCode(@RequestParam String idNavstevniListek) {
        NavstevniListek navstevniListek = navstevniListekService.getDetail(idNavstevniListek);
        if (navstevniListek == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] qrCode = qrCodeService.generateQRCodeImage(navstevniListek);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qrcode.png\"")
                    .contentType(MediaType.IMAGE_PNG).body(qrCode);
        } catch (Exception e) {
            // Handle exception appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/navstevni-listek/typ")
    public ResponseEntity<NavstevniListekTypDto> navstevniListekTyp(@RequestParam String idNavstevniListek) {
        NavstevniListekTyp navstevniListekTyp = navstevniListekService.getNavstevniListekTyp(idNavstevniListek);
        return ResponseEntity.ok(new NavstevniListekTypDto(navstevniListekTyp));
    }

    @GetMapping("/navstevni-listek/typ-by-uzivatel")
    public ResponseEntity<NavstevniListekTypDto> typByUzivatele(@RequestParam String idUzivatele) {
        NavstevniListekTyp navstevniListekTypUzivatele = navstevniListekService.getNavstevniListekTypByUzivatel(idUzivatele);
        return ResponseEntity.ok(new NavstevniListekTypDto(navstevniListekTypUzivatele));
    }


    

}
