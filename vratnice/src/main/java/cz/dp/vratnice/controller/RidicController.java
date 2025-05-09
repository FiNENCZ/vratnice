package cz.dp.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.vratnice.dto.RidicDto;
import cz.dp.vratnice.entity.Ridic;
import cz.dp.vratnice.service.RidicService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class RidicController extends BaseController {

    final static Logger logger = LogManager.getLogger(RidicController.class);


    @Autowired
    private RidicService ridicService;


    @PostMapping("/ridic/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_RIDIC')")
    public ResponseEntity<RidicDto> save(@RequestBody @Valid RidicDto ridicDto) throws UniqueValueException, NoSuchMessageException {
        Ridic newRidic = ridicService.create(ridicDto.toEntity());
        return ResponseEntity.ok(new RidicDto(newRidic));
    }

    @GetMapping("/ridic/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_RIDIC')")
    public ResponseEntity<List<RidicDto>> list() {
        List<RidicDto> ridicDtos = ridicService.list().stream()
            .map(RidicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ridicDtos);
    }

    @GetMapping("/ridic/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_RIDIC')")
    public ResponseEntity<RidicDto> getDetail(@RequestParam String idRidic) {
        Ridic ridic = ridicService.getDetail(idRidic);
        if (ridic == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new RidicDto(ridic));
    }

    @GetMapping("/ridic/list-by-cislo-op")
    public ResponseEntity<RidicDto> getRidicByCisloOp(@RequestParam String cisloOp) {
        Ridic ridic = ridicService.getRidicByCisloOp(cisloOp);
        if (ridic == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new RidicDto(ridic));
    }
    
}
