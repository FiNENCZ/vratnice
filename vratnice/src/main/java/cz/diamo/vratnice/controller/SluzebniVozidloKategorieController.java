package cz.diamo.vratnice.controller;

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

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.SluzebniVozidloKategorieDto;
import cz.diamo.vratnice.entity.SluzebniVozidloKategorie;
import cz.diamo.vratnice.repository.SluzebniVozidloKategorieRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class SluzebniVozidloKategorieController extends BaseController {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloKategorieController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private SluzebniVozidloKategorieRepository sluzebniVozidloKategorieRepository;

    @GetMapping("/sluzebni-vozidlo-kategorie/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_SLUZEBNI_VOZIDLO')")
    public List<SluzebniVozidloKategorieDto> list(HttpServletRequest request) {
        List<SluzebniVozidloKategorieDto> result = new ArrayList<SluzebniVozidloKategorieDto>();

        try {
			List<SluzebniVozidloKategorie> list = sluzebniVozidloKategorieRepository.findAll();
			if (list != null && list.size() > 0) {
				for (SluzebniVozidloKategorie sluzebniVozidloKategorie : list) {
					sluzebniVozidloKategorie.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
							sluzebniVozidloKategorie.getNazevResx()));
					result.add(new SluzebniVozidloKategorieDto(sluzebniVozidloKategorie));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return result;
    }


}
