package cz.diamo.vratnice.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.dto.RzTypVozidlaDto;
import cz.diamo.vratnice.dto.StatDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.service.PovoleniVjezduVozidlaService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;



@RestController
public class PovoleniVjezduVozidlaController extends BaseController {

    final static Logger logger = LogManager.getLogger(PovoleniVjezduVozidlaController.class);

    @Autowired
    private PovoleniVjezduVozidlaService povoleniVjezduVozidlaService;

    @PostMapping("/povoleni-vjezdu-vozidla/save")
    public ResponseEntity<PovoleniVjezduVozidlaDto> save(@RequestBody @Valid PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto) throws UniqueValueException, NoSuchMessageException {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaService.create(povoleniVjezduVozidlaDto);
        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla));
    }

    @GetMapping("/povoleni-vjezdu-vozidla/list")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> list(
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam @Nullable Date datumOd,
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam @Nullable Date datumDo,
                        @RequestParam @Nullable Integer minimalniPocetVjezdu) {

        boolean isAnyParamFilled = datumOd != null || datumDo != null || minimalniPocetVjezdu != null;

        // Kontrola, zda jsou vyplněny všechny parametry, pokud je alespoň jeden vyplněn
        if (isAnyParamFilled && (datumOd == null || datumDo == null || minimalniPocetVjezdu == null)) {
            return ResponseEntity.badRequest().body(null); // Vrátíme 400 Bad Request, pokud není vyplněn alespoň jeden parametr
        }
        
        if (isAnyParamFilled) {
            // Pokud jsou vyplněny všechny parametry, provede se tato logika
            List<PovoleniVjezduVozidlaDto> result = new ArrayList<>();
            List<PovoleniVjezduVozidla> list = povoleniVjezduVozidlaService.getAll();
    
            if (list != null && !list.isEmpty()) {
                for (PovoleniVjezduVozidla povoleniVjezduVozidla : list) {
                    PovoleniVjezduVozidlaDto povoleni = new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla);
                    Integer pocetVjezdu = povoleniVjezduVozidlaService.pocetVjezdu(povoleni.getIdPovoleniVjezduVozidla());
    
                    if (pocetVjezdu < minimalniPocetVjezdu) {
                        result.add(povoleni);
                    }
                }
            }
    
            return ResponseEntity.ok(result);
        } else {
            // Pokud není vyplněn žádný parametr, provede se tato logika
            List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidel = povoleniVjezduVozidlaService.getAll().stream()
                .map(PovoleniVjezduVozidlaDto::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(povoleniVjezduVozidel);
        }
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

    @GetMapping("/povoleni-vjezdu-vozidla/je-rz-vozidla-povolena")
    public ResponseEntity<Optional<PovoleniVjezduVozidlaDto>> jeRzVozidlaPovolena(@RequestParam String rzVozidla, @RequestParam String idVratnice) throws RecordNotFoundException, NoSuchMessageException {
        Optional<PovoleniVjezduVozidla> povoleniVjezduVozidla = povoleniVjezduVozidlaService.jeRzVozidlaPovolena(rzVozidla, idVratnice);
        Optional<PovoleniVjezduVozidlaDto> optionalPovoleniVjezduVozidlaDto = povoleniVjezduVozidla.map(PovoleniVjezduVozidlaDto::new);
        return ResponseEntity.ok(optionalPovoleniVjezduVozidlaDto);
    }
    
    
    @GetMapping("/povoleni-vjezdu-vozidla/zeme-registrace-vozidla")
    public ResponseEntity<StatDto> zemeRegistracePuvodu(@RequestParam String idPovoleniVjezduVozidla) {
        Stat stat = povoleniVjezduVozidlaService.getZemeRegistraceVozidla(idPovoleniVjezduVozidla);
        return ResponseEntity.ok(new StatDto(stat));
    }

    @PostMapping(value = "/povoleni-vjezdu-vozidla/povoleni-csv", consumes = {"multipart/form-data"})
    public ResponseEntity<Set<PovoleniVjezduVozidlaDto>> povoleniCsv(@RequestPart("file")MultipartFile file) throws IOException, ParseException, UniqueValueException, NoSuchMessageException {
        return ResponseEntity.ok(povoleniVjezduVozidlaService.processPovoleniCsvData(file));
    }


    @PostMapping(value = "/povoleni-vjezdu-vozidla/rz-typ-vozidla-csv", consumes = {"multipart/form-data"})
    public ResponseEntity<RzTypVozidlaDto> rzTypVozidlaCsv(@RequestPart("file")MultipartFile file) throws IOException, ParseException {
        return ResponseEntity.ok(povoleniVjezduVozidlaService.processRzTypVozidlaCsvData(file));
    }

    @GetMapping("/povoleni-vjezdu-vozidla/pocet-vjezdu")
    public ResponseEntity<Integer> pocetVjezdu(@RequestParam String idPovoleni) {
        return ResponseEntity.ok(povoleniVjezduVozidlaService.pocetVjezdu(idPovoleni));
    }
    
    
    

}
