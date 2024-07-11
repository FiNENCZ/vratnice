package cz.diamo.vratnice.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.repository.PovoleniVjezduVozidlaRepository;
import jakarta.transaction.Transactional;

@Service
public class PovoleniVjezduVozidlaService {

    @Autowired
    private PovoleniVjezduVozidlaRepository povoleniVjezduVozidlaRepository;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;

    public List<PovoleniVjezduVozidla> getAll() {
        return povoleniVjezduVozidlaRepository.findAll();
    }

    public PovoleniVjezduVozidla getDetail(String idPovoleniVjezduVozidla) {
        return povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
    }

    public List<PovoleniVjezduVozidla> getByStav(String stav) {
        return povoleniVjezduVozidlaRepository.getByStav(stav);
    }

    public List<PovoleniVjezduVozidla> getByRzVozidla(String rzVozidla) {
        return povoleniVjezduVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public Optional<PovoleniVjezduVozidla> jeRzVozidlaPovolena(String rzVozidla) {
        List<PovoleniVjezduVozidla> povoleniVjezduVozidlaList = getByRzVozidla(rzVozidla);

        if (povoleniVjezduVozidlaList.isEmpty()) {
            return Optional.empty();
        }

        Date currentDate = new Date();
        for (PovoleniVjezduVozidla povoleniVjezduVozidla : povoleniVjezduVozidlaList) {
            if (currentDate.compareTo(povoleniVjezduVozidla.getDatumOd()) >= 0 
                    && currentDate.compareTo(povoleniVjezduVozidla.getDatumDo()) <= 0) {
                return Optional.of(povoleniVjezduVozidla);
            }
        }

        return Optional.empty();
    }

    @Transactional
    public PovoleniVjezduVozidla create(PovoleniVjezduVozidla povoleniVjezduVozidla) {
        return povoleniVjezduVozidlaRepository.save(povoleniVjezduVozidla);
    }

    public Stat getZemeRegistraceVozidla(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
        try {
            if (povoleniVjezduVozidla == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            povoleniVjezduVozidla.getZemeRegistraceVozidla().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), povoleniVjezduVozidla.getZemeRegistraceVozidla().getNazevResx()));
            return povoleniVjezduVozidla.getZemeRegistraceVozidla();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }



}
