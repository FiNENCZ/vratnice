package cz.dp.vratnice.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.AccessDeniedException;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.base.VratniceUtils;
import cz.dp.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.dp.vratnice.entity.PovoleniVjezduVozidla;
import cz.dp.vratnice.enums.ZadostStavEnum;
import cz.dp.vratnice.service.PovoleniVjezduVozidlaService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<PovoleniVjezduVozidlaDto> save(
                @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
                HttpServletRequest request, 
                @RequestBody @Valid PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto) throws NoSuchMessageException, BaseException {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaService.create(appUserDto, povoleniVjezduVozidlaDto.toEntity(), request);
        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla));
    }

    @GetMapping("/povoleni-vjezdu-vozidla/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> list(
                        @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam @Nullable Date datumOd,
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam @Nullable Date datumDo,
                        @RequestParam @Nullable Boolean aktivita,
                        @RequestParam @Nullable ZadostStavEnum zadostStavEnum,
                        @RequestParam @Nullable Integer minimalniPocetVjezdu) throws RecordNotFoundException, NoSuchMessageException {


        // Pokud jsou vyplněny všechny parametry vyplněny
        if (datumOd != null && datumDo != null && minimalniPocetVjezdu != null) {
            
            List<PovoleniVjezduVozidlaDto> result = new ArrayList<>();
            List<PovoleniVjezduVozidla> list = povoleniVjezduVozidlaService.getList(aktivita, zadostStavEnum, appUserDto);
    
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
            List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidel = povoleniVjezduVozidlaService.getList(aktivita, zadostStavEnum, appUserDto).stream()
                .map(PovoleniVjezduVozidlaDto::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(povoleniVjezduVozidel);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
			messageSource.getMessage("povoleni_vjezdu_vozidla.list_parametry_error", null, LocaleContextHolder.getLocale()));

    }

    @GetMapping("/povoleni-vjezdu-vozidla/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
    public ResponseEntity<PovoleniVjezduVozidlaDto> getDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam String idPovoleniVjezduVozidla) throws RecordNotFoundException, NoSuchMessageException, AccessDeniedException {

        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaService.getDetail(idPovoleniVjezduVozidla, appUserDto);
        if (povoleniVjezduVozidla == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(povoleniVjezduVozidla));
    }
    
    @GetMapping("/povoleni-vjezdu-vozidla/je-rz-vozidla-povolena")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<Optional<PovoleniVjezduVozidlaDto>> jeRzVozidlaPovolena(@RequestParam String rzVozidla, @RequestParam String idVratnice) throws RecordNotFoundException, NoSuchMessageException {
        Optional<PovoleniVjezduVozidla> povoleniVjezduVozidla = povoleniVjezduVozidlaService.jeRzVozidlaPovolena(rzVozidla, idVratnice);
        Optional<PovoleniVjezduVozidlaDto> optionalPovoleniVjezduVozidlaDto = povoleniVjezduVozidla.map(PovoleniVjezduVozidlaDto::new);
        return ResponseEntity.ok(optionalPovoleniVjezduVozidlaDto);
    }

    @PostMapping("/povoleni-vjezdu-vozidla/zneplatnit-povoleni")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
    public ResponseEntity<List<PovoleniVjezduVozidlaDto>> zneplatnitPovoleni(
                @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
                HttpServletRequest request, 
                @RequestBody List<@Valid PovoleniVjezduVozidlaDto> povoleni) throws NoSuchMessageException, BaseException {

        List<PovoleniVjezduVozidla> povoleniList = povoleniVjezduVozidlaService.zneplatnitPovoleni(appUserDto, request, povoleni);
        
        // Převod entit zpět na DTO
        List<PovoleniVjezduVozidlaDto> updatedPovoleniDtos = povoleniList.stream()
        .map(PovoleniVjezduVozidlaDto::new)
        .collect(Collectors.toList());

        return ResponseEntity.ok(updatedPovoleniDtos);
    }

    @PostMapping("/povoleni-vjezdu-vozidla/zmenit-stav-zadosti")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
    public ResponseEntity<PovoleniVjezduVozidlaDto> zmenitStavZadosti(
                    @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
                    HttpServletRequest request,
                    @RequestBody PovoleniVjezduVozidlaDto povoleniVjezduVozidla, 
                    @RequestParam ZadostStavEnum zadostStavEnum) throws NoSuchMessageException, BaseException {

        PovoleniVjezduVozidla aktualizovanePovoleni = povoleniVjezduVozidlaService.zmenitStavZadosti(appUserDto, request, povoleniVjezduVozidla.toEntity(), zadostStavEnum);

        return ResponseEntity.ok(new PovoleniVjezduVozidlaDto(aktualizovanePovoleni));
    }
}
