package cz.dp.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.AccessDeniedException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.dto.HistorieSluzebniVozidloDto;
import cz.dp.vratnice.entity.SluzebniVozidlo;
import cz.dp.vratnice.service.HistorieSluzebniVozidloService;
import cz.dp.vratnice.service.SluzebniVozidloService;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class HistorieSluzebniVozidloController extends BaseController {

    final static Logger logger = LogManager.getLogger(HistorieSluzebniVozidloController.class);

    @Autowired
    private HistorieSluzebniVozidloService historieSluzebniVozidloService;

    @Autowired
    private SluzebniVozidloService sluzebniVozidloService;
    

    @GetMapping("/historie-sluzebni-vozidlo/list-by-sluzebni-vozidlo")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_SLUZEBNI_VOZIDLO')")
    public ResponseEntity<List<HistorieSluzebniVozidloDto>> listBySluzebniVozidlo(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                @RequestParam String idSluzebniVozidlo) throws RecordNotFoundException, NoSuchMessageException, AccessDeniedException {
                    
        SluzebniVozidlo sluzebniVozidloEntity = sluzebniVozidloService.getDetail(appUserDto, idSluzebniVozidlo);
        List<HistorieSluzebniVozidloDto> historieSluzebniVozidloDtos = historieSluzebniVozidloService.findBySluzebniVozidlo(sluzebniVozidloEntity).stream()
            .map(HistorieSluzebniVozidloDto::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(historieSluzebniVozidloDtos);
    }
}
