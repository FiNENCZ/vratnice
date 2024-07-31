package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.VyjezdVozidlaDto;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.entity.VyjezdVozidla;
import cz.diamo.vratnice.service.UzivatelVratniceService;
import cz.diamo.vratnice.service.VyjezdVozidlaService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
public class VyjezdVozidlaController extends BaseController {

    @Autowired
    private VyjezdVozidlaService vyjezdVozidlaService;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @PostMapping("/vyjezd-vozidla/save")
    public ResponseEntity<VyjezdVozidlaDto> saveVjezdVozidla(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                        @RequestBody @Valid VyjezdVozidlaDto vyjezdVozidlaDto) throws RecordNotFoundException, NoSuchMessageException {
        
        Vratnice vratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        VyjezdVozidla vyjezdVozidla = vyjezdVozidlaService.create(vyjezdVozidlaDto.toEntity(), vratnice);
        return ResponseEntity.ok(new VyjezdVozidlaDto(vyjezdVozidla));
    }

    @GetMapping("/vyjezd-vozidla/list")
    public ResponseEntity<List<VyjezdVozidlaDto>> list(@RequestParam @Nullable Boolean aktivni) {
        List<VyjezdVozidlaDto> result = new ArrayList<VyjezdVozidlaDto>();
        List<VyjezdVozidla> list = vyjezdVozidlaService.getList(aktivni);

        if (list != null && list.size() > 0) {
            for (VyjezdVozidla vyjezdVozidla : list) {
                result.add(new VyjezdVozidlaDto(vyjezdVozidla));
            }
        }

        return ResponseEntity.ok(result);
    }
    @GetMapping("/vyjezd-vozidla/detail")
    public ResponseEntity<VyjezdVozidlaDto> getDetail(@RequestParam String idVjezdVozidla) {
        VyjezdVozidla vyjezdVozidla = vyjezdVozidlaService.getDetail(idVjezdVozidla);
        if (vyjezdVozidla == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new VyjezdVozidlaDto(vyjezdVozidla));
    }

    @GetMapping("/vyjezd-vozidla/list-by-rz-vozidla")
    public ResponseEntity<List<VyjezdVozidlaDto>> listByRzVozidla(@RequestParam String rzVozidla) {
        List<VyjezdVozidlaDto> vyjezdyVozidel = vyjezdVozidlaService.getByRzVozidla(rzVozidla).stream()
            .map(VyjezdVozidlaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(vyjezdyVozidel);
    }

    @GetMapping("/vyjezd-vozidla/list-nevyporadane-vyjezdy")
    public ResponseEntity<List<VyjezdVozidlaDto>> listNevyporadaneVyjezdy(@RequestParam @Nullable Boolean aktivita) {
        List<VyjezdVozidlaDto> vyjezdyVozidel = vyjezdVozidlaService.getNevyporadaneVyjezdy(aktivita).stream()
            .map(VyjezdVozidlaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(vyjezdyVozidel);
    }

    @GetMapping("/vyjezd-vozidla/je-mozne-vyjet")
    public ResponseEntity<Optional<VyjezdVozidlaDto>> jeMozneVyjet(@RequestParam String rzVozidla) {
        Optional<VyjezdVozidla> vyjezdVozidel = vyjezdVozidlaService.jeMozneVyjet(rzVozidla);
        Optional<VyjezdVozidlaDto> vyjezdyVozidlaDto = vyjezdVozidel.map(VyjezdVozidlaDto::new);
        return ResponseEntity.ok(vyjezdyVozidlaDto);
    }

}
