package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.SpolecnostDto;
import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.service.SpolecnostService;
import jakarta.validation.Valid;




@RestController
public class SpolecnostController extends BaseController {

    final static Logger logger = LogManager.getLogger(SpolecnostController.class);

    @Autowired
    private SpolecnostService spolecnostService;

    @GetMapping("/spolecnost/list")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<List<SpolecnostDto>> list() {
        List<SpolecnostDto> result = new ArrayList<SpolecnostDto>();
        List<Spolecnost> list = spolecnostService.getList();

        if (list != null && list.size() > 0) {
            for (Spolecnost spolecnost : list) {
                result.add(new SpolecnostDto(spolecnost));
            }
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/spolecnost/save")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<SpolecnostDto> save(@RequestBody @Valid SpolecnostDto spolecnostDto) {
        Spolecnost spolecnost = spolecnostService.save(spolecnostDto.toEntity());
        return ResponseEntity.ok(new SpolecnostDto(spolecnost));
    }
}
