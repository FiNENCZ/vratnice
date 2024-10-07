package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaHistorieDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidlaZmenaStavu;
import cz.diamo.vratnice.repository.PovoleniVjezduVozidlaZmenaStavuRepository;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class PovoleniVjezduVozidlaHistorieController extends BaseController {

    final static Logger logger = LogManager.getLogger(PovoleniVjezduVozidlaHistorieController.class);

	@Autowired
	private PovoleniVjezduVozidlaZmenaStavuRepository povoleniVjezduVozidlaZmenaStavuRepository;

	@Autowired
	private ResourcesComponent resourcesComponent;

    @GetMapping("/povoleni-vjezdu-vozidla-historie/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA')")
	public List<PovoleniVjezduVozidlaHistorieDto> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String idPovoleniVjezduVozidla) {
		try {


			List<PovoleniVjezduVozidlaHistorieDto> result = new ArrayList<PovoleniVjezduVozidlaHistorieDto>();
			List<PovoleniVjezduVozidlaZmenaStavu> list = povoleniVjezduVozidlaZmenaStavuRepository.getList(idPovoleniVjezduVozidla);

			if (list != null) {
				for (PovoleniVjezduVozidlaZmenaStavu zadostZmenaStavu : list) {

					zadostZmenaStavu.getStavNovy()
							.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
									zadostZmenaStavu.getStavNovy().getNazevResx()));
					zadostZmenaStavu.getStavPuvodni()
							.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
									zadostZmenaStavu.getStavPuvodni().getNazevResx()));
					result.add(new PovoleniVjezduVozidlaHistorieDto(zadostZmenaStavu));
				}
			}

			return result;

		} catch (ResponseStatusException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

}
