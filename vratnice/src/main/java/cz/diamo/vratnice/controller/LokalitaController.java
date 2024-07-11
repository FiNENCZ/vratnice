package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.LokalitaDto;
import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.service.LokalitaService;

@RestController
public class LokalitaController extends BaseController{

    @Autowired
    private LokalitaService lokalitaService;

    @GetMapping("/lokalita/list")
    public ResponseEntity<List<LokalitaDto>> list(@RequestParam @Nullable String idZavod) {
        List<LokalitaDto> result = new ArrayList<LokalitaDto>();
        List<Lokalita> list = lokalitaService.getList(idZavod);

        if (list != null && list.size() > 0) {
            for (Lokalita lokalita : list) {
                result.add(new LokalitaDto(lokalita));
            }
        }

        return ResponseEntity.ok(result);
    }
    

    @GetMapping("/lokalita/detail")
    public ResponseEntity<LokalitaDto> detail(@RequestParam String idLokalita) {
        Lokalita lokalita = lokalitaService.detail(idLokalita);
        return ResponseEntity.ok(new LokalitaDto(lokalita));
    }

}
