package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.service.VratniceBaseService;
import io.swagger.v3.oas.annotations.Parameter;


@RestController
public class VratniceBaseController extends BaseController {

    final static Logger logger = LogManager.getLogger(VratniceBaseController.class);

    @Autowired
    private VratniceBaseService vratniceBaseService;

    @GetMapping("/vratnice-base/get-uzivatele-dle-nastavene-vratnice")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_DOCHAZKA', 'ROLE_SPRAVA_VYPUJCEK_KLICU')")
    public ResponseEntity<List<UzivatelDto>> getUzivateleDleNastaveneVratnice(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        List<UzivatelDto> result = new ArrayList<UzivatelDto>();

        List<Uzivatel> list = vratniceBaseService.getUzivateleDleNastaveneVratnice(appUserDto);

        if (list != null && list.size() > 0) {
            for (Uzivatel uzivatel : list) {
                result.add(new UzivatelDto(uzivatel));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/vratnice-base/get-zavody-dle-pristupu")
    public ResponseEntity<List<ZavodDto>> getZavodyDlePristupu(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam @Nullable Boolean aktivita ) {
        List<ZavodDto> result = new ArrayList<ZavodDto>();
        List<Zavod> list = vratniceBaseService.getAllZavodyUzivateleByPristup(appUserDto.getIdUzivatel(), aktivita);

        if (list != null && list.size() > 0) {
            for (Zavod zavod : list) {
                result.add(new ZavodDto(zavod));
            }
        }

        return ResponseEntity.ok(result);
    }
}
