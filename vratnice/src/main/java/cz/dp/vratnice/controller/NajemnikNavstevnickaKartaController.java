package cz.dp.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.vratnice.dto.NajemnikNavstevnickaKartaDto;
import cz.dp.vratnice.entity.NajemnikNavstevnickaKarta;
import cz.dp.vratnice.service.NajemnikNavstevnickaKartaService;
import jakarta.validation.Valid;

@RestController
public class NajemnikNavstevnickaKartaController extends BaseController {

    final static Logger logger = LogManager.getLogger(NajemnikNavstevnickaKartaController.class);

    @Autowired
    private NajemnikNavstevnickaKartaService najemnikNavstevnickaKartaService;

     @PostMapping("/najemnik-navstevnicka-karta/save")
     @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAJEMNICI_KARTY')")
    public ResponseEntity<NajemnikNavstevnickaKartaDto> save(@RequestBody @Valid NajemnikNavstevnickaKartaDto najemnikNavstevnickaKartaDto) throws UniqueValueException, NoSuchMessageException {
        NajemnikNavstevnickaKarta najemnikNavstevnickaKarta = najemnikNavstevnickaKartaService.create(najemnikNavstevnickaKartaDto.toEntity());
        return ResponseEntity.ok(new NajemnikNavstevnickaKartaDto(najemnikNavstevnickaKarta));
    }

    @GetMapping("/najemnik-navstevnicka-karta/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAJEMNICI_KARTY')")
    public ResponseEntity<List<NajemnikNavstevnickaKartaDto>> list(@RequestParam @Nullable Boolean aktivni) {
        List<NajemnikNavstevnickaKartaDto> result = new ArrayList<NajemnikNavstevnickaKartaDto>();
        List<NajemnikNavstevnickaKarta> list = najemnikNavstevnickaKartaService.getList(aktivni);

        if (list != null && list.size() > 0) {
            for (NajemnikNavstevnickaKarta najemnikNavstevnickaKarta : list) {
                result.add(new NajemnikNavstevnickaKartaDto(najemnikNavstevnickaKarta));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/najemnik-navstevnicka-karta/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAJEMNICI_KARTY')")
    public ResponseEntity<NajemnikNavstevnickaKartaDto> getDetail(@RequestParam String idNajemnikNavstevnickaKarta) {
        NajemnikNavstevnickaKarta najemnikNavstevnickaKarta = najemnikNavstevnickaKartaService.getDetail(idNajemnikNavstevnickaKarta);
        if (najemnikNavstevnickaKarta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NajemnikNavstevnickaKartaDto(najemnikNavstevnickaKarta));
    }
}
