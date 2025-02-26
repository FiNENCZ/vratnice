package cz.dp.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.component.ResourcesComponent;
import cz.dp.share.controller.BaseController;
import cz.dp.vratnice.dto.SluzebniVozidloStavDto;
import cz.dp.vratnice.entity.SluzebniVozidloStav;
import cz.dp.vratnice.repository.SluzebniVozidloStavRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class SluzebniVozidloStavController extends BaseController {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloStavController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private SluzebniVozidloStavRepository sluzebniVozidloStavRepository;

    @GetMapping("/sluzebni-vozidlo-stav/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_SLUZEBNI_VOZIDLO')")
    public List<SluzebniVozidloStavDto> list(HttpServletRequest request) {
        List<SluzebniVozidloStavDto> result = new ArrayList<SluzebniVozidloStavDto>();

        try {
			List<SluzebniVozidloStav> list = sluzebniVozidloStavRepository.findAll();
			if (list != null && list.size() > 0) {
				for (SluzebniVozidloStav sluzebniVozidloStav : list) {
					sluzebniVozidloStav.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
							sluzebniVozidloStav.getNazevResx()));
					result.add(new SluzebniVozidloStavDto(sluzebniVozidloStav));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return result;
    }

}
