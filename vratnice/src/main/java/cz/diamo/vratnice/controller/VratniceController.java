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
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.VratniceDto;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.service.VratniceService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class VratniceController extends BaseController {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloController.class);

    @Autowired
    private VratniceService vratniceService;

    @PostMapping("/vratnice/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRATNICE')")
    public ResponseEntity<VratniceDto> save(@RequestBody @Valid VratniceDto vratniceDto) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice vratnice = vratniceService.save(vratniceDto.toEntity());
        return ResponseEntity.ok(new VratniceDto(vratnice));
    }
    
    @GetMapping("/vratnice/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRATNICE')")
    public ResponseEntity<List<VratniceDto>> list(@RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idLokalita) throws RecordNotFoundException, NoSuchMessageException {
        List<VratniceDto> result = new ArrayList<VratniceDto>();
        List<Vratnice> list = vratniceService.getList(aktivni, idLokalita);

        if (list != null && list.size() > 0) {
            for (Vratnice vratnice : list) {
                result.add(new VratniceDto(vratnice));
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/vratnice/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_VRATNICE')")
    public ResponseEntity<VratniceDto> getDetail(@RequestParam String id) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice vratnice = vratniceService.getDetail(id);
        if (vratnice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new VratniceDto(vratnice));
    }
}
