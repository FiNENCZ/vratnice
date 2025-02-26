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
import cz.dp.vratnice.dto.SluzebniVozidloFunkceDto;
import cz.dp.vratnice.entity.SluzebniVozidloFunkce;
import cz.dp.vratnice.repository.SluzebniVozidloFunkceRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class SluzebniVozidloFunkceController extends BaseController {


    final static Logger logger = LogManager.getLogger(SluzebniVozidloFunkceController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private SluzebniVozidloFunkceRepository sluzebniVozidloFunkceRepository;

    @GetMapping("/sluzebni-vozidlo-funkce/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_SLUZEBNI_VOZIDLO')")
    public List<SluzebniVozidloFunkceDto> list(HttpServletRequest request) {
        List<SluzebniVozidloFunkceDto> result = new ArrayList<SluzebniVozidloFunkceDto>();

        try {
			List<SluzebniVozidloFunkce> list = sluzebniVozidloFunkceRepository.findAll();
			if (list != null && list.size() > 0) {
				for (SluzebniVozidloFunkce sluzebniVozidloFunkce : list) {
					sluzebniVozidloFunkce.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
							sluzebniVozidloFunkce.getNazevResx()));
					result.add(new SluzebniVozidloFunkceDto(sluzebniVozidloFunkce));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return result;
    }
}
