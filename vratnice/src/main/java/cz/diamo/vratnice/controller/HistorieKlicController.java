package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.HistorieKlicDto;
import cz.diamo.vratnice.entity.HistorieKlic;
import cz.diamo.vratnice.service.HistorieKlicService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class HistorieKlicController extends BaseController {

    final static Logger logger = LogManager.getLogger(HistorieKlicController.class);

    @Autowired
    private HistorieKlicService historieKlicService;


    @GetMapping("/historie-klic/list-by-klic")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_KLICU')")
    public ResponseEntity<List<HistorieKlicDto>> listByKlic(@RequestParam String idKlic) throws NoSuchMessageException, Exception {
        List<HistorieKlicDto> historieSluzebniVozidloDtos = historieKlicService.findByKlic(idKlic).stream()
            .map(HistorieKlicDto::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(historieSluzebniVozidloDtos);
    }

    @PostMapping("/historie-klic/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_KLICU')")
    public ResponseEntity<HistorieKlicDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
        @RequestBody @Valid HistorieKlicDto historieKlicDto) throws RecordNotFoundException, NoSuchMessageException {

        HistorieKlic historieKlic = historieKlicService.createUzivatelem(historieKlicDto.toEntity(), appUserDto);
        return ResponseEntity.ok(new HistorieKlicDto(historieKlic));
    }
    

}
