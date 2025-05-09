package cz.dp.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.dto.VyjezdVozidlaDto;
import cz.dp.vratnice.entity.Vratnice;
import cz.dp.vratnice.entity.VyjezdVozidla;
import cz.dp.vratnice.service.UzivatelVratniceService;
import cz.dp.vratnice.service.VyjezdVozidlaService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
public class VyjezdVozidlaController extends BaseController {

    @Autowired
    private VyjezdVozidlaService vyjezdVozidlaService;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @PostMapping("/vyjezd-vozidla/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYJEZD_VOZIDEL')")
    public ResponseEntity<VyjezdVozidlaDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                        @RequestBody @Valid VyjezdVozidlaDto vyjezdVozidlaDto) throws RecordNotFoundException, NoSuchMessageException {
        
        Vratnice vratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        VyjezdVozidla vyjezdVozidla = vyjezdVozidlaService.create(vyjezdVozidlaDto.toEntity(), vratnice);
        return ResponseEntity.ok(new VyjezdVozidlaDto(vyjezdVozidla));
    }

    @PostMapping("/vyjezd-vozidla/save-izs")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYJEZD_VOZIDEL')")
    public ResponseEntity<VyjezdVozidlaDto> saveIZS(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                        @RequestParam String rzVozidla) throws RecordNotFoundException, NoSuchMessageException {
        
        Vratnice vratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        VyjezdVozidla vyjezdVozidla = vyjezdVozidlaService.createIZSVyjezdVozidla(rzVozidla, vratnice);
        return ResponseEntity.ok(new VyjezdVozidlaDto(vyjezdVozidla));
    }

    @GetMapping("/vyjezd-vozidla/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYJEZD_VOZIDEL')")
    public ResponseEntity<List<VyjezdVozidlaDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable Boolean nevyporadaneVyjezdy) throws RecordNotFoundException, NoSuchMessageException {
        List<VyjezdVozidlaDto> result = new ArrayList<VyjezdVozidlaDto>();
        List<VyjezdVozidla> list = vyjezdVozidlaService.getList(aktivni, nevyporadaneVyjezdy,appUserDto);

        if (list != null && list.size() > 0) {
            for (VyjezdVozidla vyjezdVozidla : list) {
                result.add(new VyjezdVozidlaDto(vyjezdVozidla));
            }
        }

        return ResponseEntity.ok(result);
    }
    @GetMapping("/vyjezd-vozidla/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYJEZD_VOZIDEL')")
    public ResponseEntity<VyjezdVozidlaDto> getDetail(@RequestParam String idVjezdVozidla) {
        VyjezdVozidla vyjezdVozidla = vyjezdVozidlaService.getDetail(idVjezdVozidla);
        if (vyjezdVozidla == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new VyjezdVozidlaDto(vyjezdVozidla));
    }

    @GetMapping("/vyjezd-vozidla/je-mozne-vyjet")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VYJEZD_VOZIDEL')")
    public ResponseEntity<Optional<VyjezdVozidlaDto>> jeMozneVyjet(@RequestParam String rzVozidla) {
        Optional<VyjezdVozidla> vyjezdVozidel = vyjezdVozidlaService.jeMozneVyjet(rzVozidla);
        Optional<VyjezdVozidlaDto> vyjezdyVozidlaDto = vyjezdVozidel.map(VyjezdVozidlaDto::new);
        return ResponseEntity.ok(vyjezdyVozidlaDto);
    }

}
