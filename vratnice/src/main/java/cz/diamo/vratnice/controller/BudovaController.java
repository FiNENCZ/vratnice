package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.BudovaDto;
import cz.diamo.vratnice.dto.KlicDto;
import cz.diamo.vratnice.entity.Budova;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.service.BudovaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class BudovaController extends BaseController {

    final static Logger logger = LogManager.getLogger(BudovaController.class);

    @Autowired
    private BudovaService budovaService;

    @GetMapping("/budova/list")
    public ResponseEntity<List<BudovaDto>> list(@RequestParam @Nullable String idLokalita) {
        List<BudovaDto> result = new ArrayList<BudovaDto>();
        List<Budova> list = budovaService.getList(idLokalita);

        if (list != null && list.size() > 0) {
            for (Budova budova : list) {
                result.add(new BudovaDto(budova));
            }
        }

        return ResponseEntity.ok(result);
    }
    



}
