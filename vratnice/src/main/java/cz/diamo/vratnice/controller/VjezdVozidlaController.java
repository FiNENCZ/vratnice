package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.RidicDto;
import cz.diamo.vratnice.dto.VjezdVozidlaDto;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.service.RidicService;
import cz.diamo.vratnice.service.VjezdVozidlaService;
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

    @PostMapping("/vjezd-vozidla/save")
    public ResponseEntity<VjezdVozidlaDto> saveVjezdVozidla(@RequestBody @Valid VjezdVozidlaDto vjezdVozidladDto) {
        if (vjezdVozidladDto.getRidic() != null) {
            Ridic savedRidic =  ridicService.create(vjezdVozidladDto.getRidic().toEntity());
            vjezdVozidladDto.setRidic(new RidicDto(savedRidic));
        }
        VjezdVozidla vjezdVozidla = vjezdVozidlaService.create(vjezdVozidladDto.toEntity());
        return ResponseEntity.ok(new VjezdVozidlaDto(vjezdVozidla));
    }

    @GetMapping("/vjezd-vozidla/list")
    public ResponseEntity<List<VjezdVozidlaDto>> list(@RequestParam @Nullable Boolean aktivni) {
        List<VjezdVozidlaDto> result = new ArrayList<VjezdVozidlaDto>();
        List<VjezdVozidla> list = vjezdVozidlaService.getList(aktivni);

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

    @GetMapping("/vjez-vozidla/list-by-rz-vozidla")
    public ResponseEntity<List<VjezdVozidlaDto>> listByRzVozidla(@RequestParam String rzVozidla) {
        List<VjezdVozidlaDto> vjezdyVozidel = vjezdVozidlaService.getByRzVozidla(rzVozidla).stream()
            .map(VjezdVozidlaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(vjezdyVozidel);
    }

    @GetMapping("/vjezd-vozidla/list-by-ridic")
    public ResponseEntity<List<VjezdVozidlaDto>> listByRidic(@RequestParam String idRidic) throws RecordNotFoundException, NoSuchMessageException {
        Ridic ridic = ridicService.getDetail(idRidic);

        if (ridic == null) {
            return ResponseEntity.notFound().build();
        }

        List<VjezdVozidlaDto> vjezdVozidel = vjezdVozidlaService.getByRidic(ridic).stream()
            .map(VjezdVozidlaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(vjezdVozidel);
    }
}
