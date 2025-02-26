package cz.dp.vratnice.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.dto.DochazkaDto;
import cz.dp.vratnice.edos.dto.RucniZaznamSnimaceDto;
import cz.dp.vratnice.edos.dto.SnimacAkceVratniceDto;
import cz.dp.vratnice.edos.dto.SnimacVratniceDto;
import cz.dp.vratnice.edos.service.EdosService;
import cz.dp.vratnice.entity.Vratnice;
import cz.dp.vratnice.service.UzivatelVratniceService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
public class DochazkaController extends BaseController{

    final static Logger logger = LogManager.getLogger(DochazkaController.class);

    @Autowired
    private EdosService edosService;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @Autowired
    private MessageSource messageSource;


    @GetMapping("/dochazka/snimac/list")
    @PreAuthorize("hasAnyAuthority('EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE', 'EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE_OKRUH_2')")
    public ResponseEntity<List<SnimacVratniceDto>> listSnimace(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
             HttpServletRequest request) throws BaseException {
        return edosService.listSnimac(appUserDto, request);
    }

    @GetMapping("/dochazka/snimac-akce/list")
    @PreAuthorize("hasAnyAuthority('EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE', 'EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE_OKRUH_2')")
    public ResponseEntity<List<SnimacAkceVratniceDto>> listSnimacAkce(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
             HttpServletRequest request) throws BaseException {
        return edosService.listSnimacAkce(appUserDto, request);
    }

    @PostMapping("/dochazka/zaznam-snimac/save")
    @PreAuthorize("hasAnyAuthority('EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE', 'EDOS_SPRAVA_RUCNI_ZAZNAMY_SNIMACE_OKRUH_2')")
    public ResponseEntity<Void> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
            HttpServletRequest request, @RequestBody @Valid DochazkaDto dochazkaDto) throws BaseException, JSONException {

        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);
        if (nastavenaVratnice == null) 
            throw new BaseException(
                String.format(messageSource.getMessage("vratnice.not_found", null, LocaleContextHolder.getLocale())));

        if (nastavenaVratnice.getIdSnimac() == null) 
            throw new RecordNotFoundException(
                String.format(messageSource.getMessage("vratnice.snimac.not_found", null, LocaleContextHolder.getLocale())));

        RucniZaznamSnimaceDto rucniZaznamSnimaceDto = new RucniZaznamSnimaceDto(nastavenaVratnice.getIdSnimac(), 
            dochazkaDto.getUzivatel().getSapId(), dochazkaDto.getSnimacAkce().getId());

        edosService.zaznamSnimaceSave(appUserDto, request, rucniZaznamSnimaceDto);

        return null;
    }
    



    
    

}
// 