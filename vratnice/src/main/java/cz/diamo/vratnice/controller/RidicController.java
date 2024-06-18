package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.RidicDto;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.service.RidicService;
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
    public ResponseEntity<RidicDto> save(@RequestBody @Valid RidicDto ridicDto) {
        Ridic newRidic = ridicService.create(ridicDto.toEntity());
        return ResponseEntity.ok(new RidicDto(newRidic));
    }

    @GetMapping("/ridic/list")
    public ResponseEntity<List<RidicDto>> list() {
        List<RidicDto> ridicDtos = ridicService.list().stream()
            .map(RidicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ridicDtos);
    }

    @GetMapping("/ridic/detail")
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
