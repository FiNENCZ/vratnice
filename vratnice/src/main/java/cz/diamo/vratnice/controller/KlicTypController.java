package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.KlicTypDto;
import cz.diamo.vratnice.entity.KlicTyp;
import cz.diamo.vratnice.repository.KlicTypRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class KlicTypController extends BaseController {

    final static Logger logger = LogManager.getLogger(KlicTypController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private KlicTypRepository klicTypRepository;

    @GetMapping("/klic-typ/list")
	@PreAuthorize("isFullyAuthenticated()")
    public List<KlicTypDto> list(HttpServletRequest request) {
        List<KlicTypDto> result = new ArrayList<KlicTypDto>();

        try {
			List<KlicTyp> list = klicTypRepository.findAll();
			if (list != null && list.size() > 0) {
				for (KlicTyp klicTyp : list) {
					klicTyp.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
							klicTyp.getNazevResx()));
					result.add(new KlicTypDto(klicTyp));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return result;
    }
    

}
