package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.dto.RidicDto;
import cz.diamo.vratnice.dto.VjezdVozidlaDto;
import cz.diamo.vratnice.dto.VozidloTypDto;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.service.RidicService;
import cz.diamo.vratnice.service.UzivatelVratniceService;
import cz.diamo.vratnice.service.VjezdVozidlaService;
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

    @GetMapping("/vjezd-vozidla/list")
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
    public ResponseEntity<VjezdVozidlaDto> getDetail(@RequestParam String idVjezdVozidla) {
        VjezdVozidla vjezdVozidla = vjezdVozidlaService.getDetail(idVjezdVozidla);
        if (vjezdVozidla == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new VjezdVozidlaDto(vjezdVozidla));
    }

    @GetMapping("/vjezd-vozidla/list-nevyporadane-vjezdy")
    public ResponseEntity<List<VjezdVozidlaDto>> listNevyporadaneVjezdy(@RequestParam @Nullable Boolean aktivita) {
        List<VjezdVozidlaDto> vjezdyVozidel = vjezdVozidlaService.getNevyporadaneVjezdy(aktivita).stream()
            .map(VjezdVozidlaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(vjezdyVozidel);
    }

    @GetMapping("/vjezd-vozidla/typ")
    public ResponseEntity<VozidloTypDto> typ(@RequestParam String idVozidlo) {
        VozidloTyp vozidloTyp = vjezdVozidlaService.getVozidloTyp(idVozidlo);
        return ResponseEntity.ok(new VozidloTypDto(vozidloTyp));
    }
}
