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
import cz.dp.vratnice.dto.ZadostStavDto;
import cz.dp.vratnice.entity.ZadostStav;
import cz.dp.vratnice.repository.ZadostStavRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ZadostStavController extends BaseController {

    final static Logger logger = LogManager.getLogger(ZadostStavController.class);

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private ZadostStavRepository zadostStavRepository;

    @GetMapping("/zadost-stav/list")
    @PreAuthorize("isFullyAuthenticated()")
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
