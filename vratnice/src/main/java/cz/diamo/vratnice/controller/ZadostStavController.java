package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.ZadostStavDto;
import cz.diamo.vratnice.entity.ZadostStav;
import cz.diamo.vratnice.repository.ZadostStavRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ZadostStavController extends BaseController {

    final static Logger logger = LogManager.getLogger(ZadostStavController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private ZadostStavRepository zadostStavRepository;

    @GetMapping("/zadost-stav/list")
    public List<ZadostStavDto> list(HttpServletRequest request) {
        List<ZadostStavDto> result = new ArrayList<ZadostStavDto>();

        try {
			List<ZadostStav> list = zadostStavRepository.findAll();
			if (list != null && list.size() > 0) {
				for (ZadostStav zadostStav : list) {
					zadostStav.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        zadostStav.getNazevResx()));
					result.add(new ZadostStavDto(zadostStav));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return result;
    }

}
