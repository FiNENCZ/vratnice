package cz.dp.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.vratnice.dto.RidicDto;
import cz.dp.vratnice.dto.VjezdVozidlaDto;
import cz.dp.vratnice.entity.Ridic;
import cz.dp.vratnice.entity.VjezdVozidla;
import cz.dp.vratnice.entity.Vratnice;
import cz.dp.vratnice.service.RidicService;
import cz.dp.vratnice.service.UzivatelVratniceService;
import cz.dp.vratnice.service.VjezdVozidlaService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class VjezdVozidlaController extends BaseController{

    @Autowired
    private VjezdVozidlaService vjezdVozidlaService;

    @Autowired
    private RidicService ridicService;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @PostMapping("/vjezd-vozidla/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VJEZD_VOZIDEL')")
    public ResponseEntity<VjezdVozidlaDto> saveVjezdVozidla(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                @RequestBody @Valid VjezdVozidlaDto vjezdVozidladDto) throws RecordNotFoundException, NoSuchMessageException, UniqueValueException {

        if (vjezdVozidladDto.getRidic() != null) {
            Ridic savedRidic =  ridicService.create(vjezdVozidladDto.getRidic().toEntity());
            vjezdVozidladDto.setRidic(new RidicDto(savedRidic));
        }

        Vratnice vratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        VjezdVozidla vjezdVozidla = vjezdVozidlaService.create(vjezdVozidladDto.toEntity(), vratnice);
        return ResponseEntity.ok(new VjezdVozidlaDto(vjezdVozidla));
    }

    @PostMapping("/vjezd-vozidla/save-izs")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VJEZD_VOZIDEL')")
    public ResponseEntity<VjezdVozidlaDto> saveIZS(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                @RequestParam String rzVozidla) throws RecordNotFoundException, NoSuchMessageException, UniqueValueException {

        Vratnice vratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        VjezdVozidla vjezdVozidla = vjezdVozidlaService.createIZSVjezdVozidla(rzVozidla, vratnice);
        return ResponseEntity.ok(new VjezdVozidlaDto(vjezdVozidla));
    }

    @GetMapping("/vjezd-vozidla/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VJEZD_VOZIDEL')")
    public ResponseEntity<List<VjezdVozidlaDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
                @RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable Boolean nevyporadaneVjezdy) throws RecordNotFoundException, NoSuchMessageException {
        List<VjezdVozidlaDto> result = new ArrayList<VjezdVozidlaDto>();
        List<VjezdVozidla> list = vjezdVozidlaService.getList(aktivni, nevyporadaneVjezdy, appUserDto);

        if (list != null && list.size() > 0) {
            for (VjezdVozidla vjezdVozidla : list) {
                result.add(new VjezdVozidlaDto(vjezdVozidla));
            }
        }

        return ResponseEntity.ok(result);
    }
    

    @GetMapping("/vjezd-vozidla/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VJEZD_VOZIDEL')")
    public ResponseEntity<VjezdVozidlaDto> getDetail(@RequestParam String idVjezdVozidla) throws RecordNotFoundException, NoSuchMessageException {
        VjezdVozidla vjezdVozidla = vjezdVozidlaService.getDetail(idVjezdVozidla);
        if (vjezdVozidla == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new VjezdVozidlaDto(vjezdVozidla));
    }
}
