package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

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
import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.service.DochazkaService;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class DochazkaController extends BaseController{

    final static Logger logger = LogManager.getLogger(DochazkaController.class);

    @Autowired
    private DochazkaService dochazkaService;



    @GetMapping("/dochazka/get-uzivatele-dle-nastavene-vratnice")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_DOCHAZKA')")
    public ResponseEntity<List<UzivatelDto>> getUzivateleDleNastaveneVratnice(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
                List<UzivatelDto> result = new ArrayList<UzivatelDto>();

        List<Uzivatel> list = dochazkaService.getUzivateleDleNastaveneVratnice(appUserDto);

        if (list != null && list.size() > 0) {
            for (Uzivatel uzivatel : list) {
                result.add(new UzivatelDto(uzivatel));
            }
        }

        return ResponseEntity.ok(result);
    }
    

}
