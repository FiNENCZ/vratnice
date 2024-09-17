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
import cz.diamo.vratnice.dto.NavstevniListekTypDto;
import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.repository.NavstevniListekTypRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class NavstevniListekTypController  extends BaseController {

    final static Logger logger = LogManager.getLogger(StatController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private NavstevniListekTypRepository navstevniListekTypRepository;

    @GetMapping("/navstevni-listek-typ/list")
	@PreAuthorize("isFullyAuthenticated()")
    public List<NavstevniListekTypDto> list(HttpServletRequest request) {
        List<NavstevniListekTypDto> result = new ArrayList<NavstevniListekTypDto>();

        try {
			List<NavstevniListekTyp> list = navstevniListekTypRepository.findAll();
			if (list != null && list.size() > 0) {
				for (NavstevniListekTyp navstevniListekTyp : list) {
					navstevniListekTyp.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
							navstevniListekTyp.getNazevResx()));
					result.add(new NavstevniListekTypDto(navstevniListekTyp));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return result;
    }
}
