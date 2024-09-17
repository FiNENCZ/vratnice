package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.HistorieKlicAkceDto;
import cz.diamo.vratnice.entity.HistorieKlicAkce;
import cz.diamo.vratnice.service.HistorieKlicAkceService;
import io.micrometer.common.lang.Nullable;

@RestController
public class HistorieKlicAkceController extends BaseController {


    final static Logger logger = LogManager.getLogger(HistorieKlicAkceController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private HistorieKlicAkceService historieKlicAkceService;

    @GetMapping("/historie-klic-akce/list")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<List<HistorieKlicAkceDto>> list(@RequestParam(defaultValue = "true") @Nullable Boolean uzivatelskeAkce) {
        List<HistorieKlicAkceDto> result = new ArrayList<HistorieKlicAkceDto>();
        List<HistorieKlicAkce> list = historieKlicAkceService.getList(uzivatelskeAkce);
        try {
            if (list != null && list.size() > 0) {
                for (HistorieKlicAkce historieKlicAkce : list) {
                    historieKlicAkce.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),historieKlicAkce.getNazevResx()));
                    result.add(new HistorieKlicAkceDto(historieKlicAkce));
                }
            } 
        }catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return ResponseEntity.ok(result);
    }
}
