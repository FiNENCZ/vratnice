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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.base.VratniceUtils;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.dto.StatDto;
import cz.diamo.vratnice.dto.VozidloTypDto;
import cz.diamo.vratnice.dto.ZadostStavDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.entity.ZadostStav;
import cz.diamo.vratnice.enums.ZadostStavEnum;
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
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
    public ResponseEntity<PovoleniVjezduVozidlaDto> save(@RequestBody @Valid PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto) throws UniqueValueException, NoSuchMessageException {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaService.create(povoleniVjezduVozidlaDto.toEntity());
        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla));
    }

    @GetMapping("/povoleni-vjezdu-vozidla/list")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> list(
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam @Nullable Date datumOd,
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam @Nullable Date datumDo,
                        @RequestParam @Nullable Boolean aktivita,
                        @RequestParam @Nullable ZadostStavEnum zadostStavEnum,
                        @RequestParam @Nullable Integer minimalniPocetVjezdu) {


        // Pokud jsou vyplněny všechny parametry vyplněny
        if (datumOd != null && datumDo != null && minimalniPocetVjezdu != null) {
            
            List<PovoleniVjezduVozidlaDto> result = new ArrayList<>();
            List<PovoleniVjezduVozidla> list = povoleniVjezduVozidlaService.getList(aktivita, zadostStavEnum);
    
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
            List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidel = povoleniVjezduVozidlaService.getList(aktivita, zadostStavEnum).stream()
                .map(PovoleniVjezduVozidlaDto::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(povoleniVjezduVozidel);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
			messageSource.getMessage("povoleni_vjezdu_vozidla.list_parametry_error", null, LocaleContextHolder.getLocale()));

    }

    @GetMapping("/povoleni-vjezdu-vozidla/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
    public ResponseEntity<PovoleniVjezduVozidlaDto> getDetail(@RequestParam String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaService.getDetail(idPovoleniVjezduVozidla);
        if (povoleniVjezduVozidla == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla));
    }
    

    @GetMapping("/povoleni-vjezdu-vozidla/get-by-stav")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> getByStav(@RequestParam String stav) {
        List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidel = povoleniVjezduVozidlaService.getByStav(stav).stream()
            .map(PovoleniVjezduVozidlaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(povoleniVjezduVozidel);
    }

    @GetMapping("/povoleni-vjezdu-vozidla/je-rz-vozidla-povolena")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<Optional<PovoleniVjezduVozidlaDto>> jeRzVozidlaPovolena(@RequestParam String rzVozidla, @RequestParam String idVratnice) throws RecordNotFoundException, NoSuchMessageException {
        Optional<PovoleniVjezduVozidla> povoleniVjezduVozidla = povoleniVjezduVozidlaService.jeRzVozidlaPovolena(rzVozidla, idVratnice);
        Optional<PovoleniVjezduVozidlaDto> optionalPovoleniVjezduVozidlaDto = povoleniVjezduVozidla.map(PovoleniVjezduVozidlaDto::new);
        return ResponseEntity.ok(optionalPovoleniVjezduVozidlaDto);
    }
    
    
    @GetMapping("/povoleni-vjezdu-vozidla/zeme-registrace-vozidla")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<StatDto> zemeRegistracePuvodu(@RequestParam String idPovoleniVjezduVozidla) {
        Stat stat = povoleniVjezduVozidlaService.getZemeRegistraceVozidla(idPovoleniVjezduVozidla);
        return ResponseEntity.ok(new StatDto(stat));
    }

    @GetMapping("povoleni-vjezdu-vozidla/typy-vozidel")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<List<VozidloTypDto>> typyVozidel(@RequestParam String idPovoleniVjezduVozidla) {
        List<VozidloTypDto> result = new ArrayList<VozidloTypDto>();
        List<VozidloTyp> typyVozidel = povoleniVjezduVozidlaService.getTypyVozidel(idPovoleniVjezduVozidla);

        if (typyVozidel != null && typyVozidel.size() > 0) {
            for (VozidloTyp vozidloTyp : typyVozidel) {
                result.add(new VozidloTypDto(vozidloTyp));
            }
        }

        return ResponseEntity.ok(result);

    }

    @GetMapping("/povoleni-vjezdu-vozidla/stav")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<ZadostStavDto> stav(@RequestParam String idZadostKlic) {
        ZadostStav stav = povoleniVjezduVozidlaService.getZadostStav(idZadostKlic);
        return ResponseEntity.ok(new ZadostStavDto(stav));
    }


    @PostMapping("/povoleni-vjezdu-vozidla/zneplatnit-povoleni")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> zneplatnitPovoleni(@RequestBody List<@Valid PovoleniVjezduVozidlaDto> povoleni) throws UniqueValueException, NoSuchMessageException {
        List<PovoleniVjezduVozidla> povoleniList = povoleniVjezduVozidlaService.zneplatnitPovoleni(povoleni);
        
        // Převod entit zpět na DTO
        List<PovoleniVjezduVozidlaDto> updatedPovoleniDtos = povoleniList.stream()
        .map(PovoleniVjezduVozidlaDto::new)
        .collect(Collectors.toList());

        return ResponseEntity.ok(updatedPovoleniDtos);
    }

    @PostMapping("/povoleni-vjezdu-vozidla/zmenit-stav-zadosti")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<PovoleniVjezduVozidlaDto> zmenitStavZadosti(@RequestParam PovoleniVjezduVozidla povoleniVjezduVozidla, 
                    @RequestParam ZadostStavEnum zadostStavEnum) throws UniqueValueException, NoSuchMessageException {

        PovoleniVjezduVozidla aktualizovanePovoleni = povoleniVjezduVozidlaService.zmenitStavZadosti(povoleniVjezduVozidla, zadostStavEnum);

        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(aktualizovanePovoleni));
    }
    
    @GetMapping("/povoleni-vjezdu-vozidla/ukazka-mailu")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<String> ukazkaMailu(@RequestParam String idPovoleniVjezduVozidla) throws NoSuchMessageException, BaseException {
        PovoleniVjezduVozidla povoleni = povoleniVjezduVozidlaService.getDetail(idPovoleniVjezduVozidla);
        povoleniVjezduVozidlaService.zaslatEmailVytvoreniZadosti(povoleni);
        return ResponseEntity.ok("");
    }

}
