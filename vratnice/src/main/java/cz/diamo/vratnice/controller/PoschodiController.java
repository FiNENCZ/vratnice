package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.PoschodiDto;
import cz.diamo.vratnice.entity.Poschodi;
import cz.diamo.vratnice.service.PoschodiService;

@RestController
public class PoschodiController extends BaseController {

    final static Logger logger = LogManager.getLogger(PoschodiController.class);

    @Autowired
    private PoschodiService poschodiService;

    @GetMapping("/poschodi/list")
    public ResponseEntity<List<PoschodiDto>> list(@RequestParam @Nullable String idBudova) {
        List<PoschodiDto> result = new ArrayList<PoschodiDto>();
        List<Poschodi> list = poschodiService.getList(idBudova);

        if (list != null && list.size() > 0) {
            for (Poschodi poschodi : list) {
                result.add(new PoschodiDto(poschodi));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/poschodi/detail")
    public ResponseEntity<PoschodiDto> detail(@RequestParam String idPoschodi) {
        Poschodi poschodi = poschodiService.detail(idPoschodi);
        return ResponseEntity.ok(new PoschodiDto(poschodi));
    }
    

}
