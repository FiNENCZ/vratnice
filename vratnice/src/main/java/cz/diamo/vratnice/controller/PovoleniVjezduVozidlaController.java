package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.base.VratniceUtils;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.dto.StatDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.service.PovoleniVjezduVozidlaService;
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
    private MessageSource messageSource;

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


        // Pokud jsou vyplněny všechny parametry vyplněny
        if (datumOd != null && datumDo != null && minimalniPocetVjezdu != null) {
            
            List<PovoleniVjezduVozidlaDto> result = new ArrayList<>();
            List<PovoleniVjezduVozidla> list = povoleniVjezduVozidlaService.getAll();
    
            if (list != null && !list.isEmpty()) {
                for (PovoleniVjezduVozidla povoleniVjezduVozidla : list) {
                    PovoleniVjezduVozidlaDto povoleni = new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla);
                    if (VratniceUtils.isDateInInterval(datumDo, datumDo, povoleni.getDatumOd()) || 
                        VratniceUtils.isDateInInterval(datumDo, datumDo, povoleni.getDatumDo()) ||
                        VratniceUtils.isDateRangeOverlapping(datumDo, datumDo, povoleni.getDatumOd(), povoleni.getDatumDo())) {

                        Integer pocetVjezdu = povoleniVjezduVozidlaService.pocetVjezdu(povoleni.getIdPovoleniVjezduVozidla(), datumOd, datumDo);

                        if (pocetVjezdu < minimalniPocetVjezdu) {
                            povoleni.setPocetVjezdu(pocetVjezdu);
                            result.add(povoleni);
                        }
                    }
                }
            }
    
            return ResponseEntity.ok(result);
        } 
        // pokud není žádný parametr vyplněn
        if (datumOd == null && datumDo == null && minimalniPocetVjezdu == null) {
            List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidel = povoleniVjezduVozidlaService.getAll().stream()
                .map(PovoleniVjezduVozidlaDto::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(povoleniVjezduVozidel);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
			messageSource.getMessage("povoleni_vjezdu_vozidla.list_parametry_error", null, LocaleContextHolder.getLocale()));

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

    @GetMapping("/povoleni-vjezdu-vozidla/pocet-vjezdu")
    public ResponseEntity<Integer> pocetVjezdu(@RequestParam String idPovoleni) {
        return ResponseEntity.ok(povoleniVjezduVozidlaService.pocetVjezdu(idPovoleni));
    }


    @PostMapping("/povoleni-vjezdu-vozidla/zneplatnit-povoleni")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> zneplatnitPovoleni(@RequestBody List<@Valid PovoleniVjezduVozidlaDto> povoleni) {
        //TODO: dodělat logiku zneplatnění povolení vjezdu vozidla
        
        return ResponseEntity.ok(povoleni);
    }
    
}
