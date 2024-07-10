package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.VozidloTypDto;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.service.VozidloTypService;
import io.micrometer.common.lang.Nullable;


@RestController
public class VozidloTypController extends BaseController {


    final static Logger logger = LogManager.getLogger(VozidloTypController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;
    @Autowired
    private VozidloTypService vozidloTypService;


    @GetMapping("/vozidlo-typ/list")
    public ResponseEntity<List<VozidloTypDto>> list(@RequestParam @Nullable Boolean withIZS) {
        List<VozidloTypDto> result = new ArrayList<VozidloTypDto>();
        List<VozidloTyp> list = vozidloTypService.getList(withIZS);
        try {
            if (list != null && list.size() > 0) {
                for (VozidloTyp vozidloTyp : list) {
                    vozidloTyp.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),vozidloTyp.getNazevResx()));
                    result.add(new VozidloTypDto(vozidloTyp));
                }
            } 
        }catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return ResponseEntity.ok(result);
    }

    @GetMapping("/vozidlo-typ/get-typ-with-nazev")
    public ResponseEntity<VozidloTypDto> getTypWithNazev(@RequestParam Integer idVozidloTyp) {
        VozidloTyp vozidloTyp = vozidloTypService.detail(idVozidloTyp);

        try {
            vozidloTyp.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),vozidloTyp.getNazevResx()));
        } catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return ResponseEntity.ok(new VozidloTypDto(vozidloTyp));
        
    }
    

}

