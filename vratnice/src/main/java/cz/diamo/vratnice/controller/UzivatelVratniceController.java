package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.dto.UzivatelVratniceDto;
import cz.diamo.vratnice.dto.VratniceDto;
import cz.diamo.vratnice.entity.UzivatelVratnice;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.service.UzivatelVratniceService;
import io.swagger.v3.oas.annotations.Parameter;
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
    public ResponseEntity<UzivatelVratniceDto> save(@RequestBody @Valid UzivatelVratniceDto uzivatelVratniceDto) throws UniqueValueException, NoSuchMessageException {
        UzivatelVratnice uzivatelVratnice = uzivatelVratniceService.save(uzivatelVratniceDto.toEntity());
        return ResponseEntity.ok(new UzivatelVratniceDto(uzivatelVratnice));
    }
    

    @GetMapping("/uzivatel-vratnice/list")
    public ResponseEntity<List<UzivatelVratniceDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
                @RequestParam @Nullable Boolean aktivni) throws RecordNotFoundException, NoSuchMessageException {

        List<UzivatelVratniceDto> result = new ArrayList<UzivatelVratniceDto>();
        List<UzivatelVratnice> list = uzivatelVratniceService.getList(aktivni, false, appUserDto);

        if (list != null && list.size() > 0) {
            for (UzivatelVratnice uzivatelVratnice : list) {
                result.add(new UzivatelVratniceDto(uzivatelVratnice));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/uzivatel-vratnice/list-vratnice-by-uzivatel")
    public ResponseEntity<List<UzivatelVratniceDto>> listVratniceByUzivatel(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {

        List<UzivatelVratniceDto> result = new ArrayList<UzivatelVratniceDto>();
        List<UzivatelVratnice> list = uzivatelVratniceService.getList(true, true, appUserDto);

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

    @PostMapping("/uzivatel-vratnice/nastav-vratnici")
    public ResponseEntity<UzivatelVratniceDto> nastavVratnici(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody VratniceDto vratniceDto) throws RecordNotFoundException, NoSuchMessageException, UniqueValueException {
        UzivatelVratnice uzivatelVratnice = uzivatelVratniceService.getByUzivatel(appUserDto);
        uzivatelVratnice.setNastavenaVratnice(vratniceDto.toEntity());

        UzivatelVratnice savedUzivatelVratnice = uzivatelVratniceService.save(uzivatelVratnice);
        
        return ResponseEntity.ok(new UzivatelVratniceDto(savedUzivatelVratnice));
    }
    

    @GetMapping("/uzivatel-vratnice/je-nastavena-vratnice-vjezdova")
    public ResponseEntity<Boolean> jeNastavenaVratniceVjezdova(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Boolean jeVjezdova = uzivatelVratniceService.jeVjezdova(appUserDto);
        return ResponseEntity.ok(jeVjezdova);
    }

    @GetMapping("/uzivatel-vratnice/je-nastavena-vratnice-osobni")
    public ResponseEntity<Boolean> jeNastavenaVratniceOsobni(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Boolean jeOsobni = uzivatelVratniceService.jeOsobni(appUserDto);
        return ResponseEntity.ok(jeOsobni);
    }

    @GetMapping("/uzivatel-vratnice/je-nastavena-vratnice-navstevni")
    public ResponseEntity<Boolean> jeNastavenaVratniceNavstevni(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Boolean jeNavstevni = uzivatelVratniceService.jeNavstevni(appUserDto);
        return ResponseEntity.ok(jeNavstevni);
    }
}
