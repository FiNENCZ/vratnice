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
import cz.diamo.vratnice.dto.StatDto;
import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.repository.StatRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class StatController extends BaseController {

    final static Logger logger = LogManager.getLogger(StatController.class);

    
    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private StatRepository statRepository;

    @GetMapping("/stat/list")
    public List<StatDto> list(HttpServletRequest request) {
        List<StatDto> result = new ArrayList<StatDto>();

        try {
			List<Stat> list = statRepository.findAll();
			if (list != null && list.size() > 0) {
				for (Stat stat : list) {
					stat.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
							stat.getNazevResx()));
					result.add(new StatDto(stat));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

        return result;
    }

}
