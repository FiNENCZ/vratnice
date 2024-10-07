package cz.diamo.vratnice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.dto.JmenoKorekturaDto;
import cz.diamo.vratnice.entity.JmenoKorektura;
import cz.diamo.vratnice.service.JmenoKorekturaService;
import jakarta.validation.Valid;

@RestController
public class JmenoKorekturaController extends BaseController {

    final static Logger logger = LogManager.getLogger(JmenoKorekturaController.class);

    @Autowired
    private JmenoKorekturaService jmenoKorekturaService;

    @PostMapping("/jmeno-korektura/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_JMENO_KOREKTURA')")
    public ResponseEntity<JmenoKorekturaDto> save(@RequestBody @Valid JmenoKorekturaDto jmenoKorekturaDto) throws UniqueValueException, NoSuchMessageException {
        JmenoKorektura newJmenoKorektura = jmenoKorekturaService.create(jmenoKorekturaDto.toEntity());
        return ResponseEntity.ok(new JmenoKorekturaDto(newJmenoKorektura));
    }

    @GetMapping("/jmeno-korektura/list")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<List<JmenoKorekturaDto>> list() {
        List<JmenoKorekturaDto> jmenoKorekturaDtos = jmenoKorekturaService.list().stream()
            .map(JmenoKorekturaDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(jmenoKorekturaDtos);
    }

    @GetMapping("/jmeno-korektura/get-by-jmeno-vstupu")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<JmenoKorekturaDto> getByJmenoVstupu(@RequestParam String jmenoVstupu) {
        JmenoKorektura jmenoKorektura = jmenoKorekturaService.getByJmenoVstup(jmenoVstupu);
        if (jmenoKorektura == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new JmenoKorekturaDto(jmenoKorektura));
    }

}
