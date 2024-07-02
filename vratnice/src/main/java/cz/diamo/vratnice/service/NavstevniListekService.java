package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;
import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.repository.NavstevniListekRepository;
import cz.diamo.vratnice.repository.UzivatelNavstevniListekTypRepository;
import jakarta.transaction.Transactional;

@Service
public class NavstevniListekService {

    @Autowired
    private NavstevniListekRepository navstevniListekRepository;

    @Autowired
    private UzivatelNavstevniListekTypRepository uzivatelNavstevniListekTypRepository;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
	private MessageSource messageSource;

    @Transactional
    public NavstevniListek create(NavstevniListek navstevniListek) {
        return navstevniListekRepository.save(navstevniListek);
    }

    public List<NavstevniListek> getAll() {
        return navstevniListekRepository.findAll();
    }

    public NavstevniListek getDetail(String idNavstevniListek) {
        return navstevniListekRepository.getDetail(idNavstevniListek);
    }

    public List<NavstevniListek> getNavstevniListkyByUzivatel(Uzivatel uzivatel) {
        return navstevniListekRepository.findByUzivatel(uzivatel);
    }

    public List<NavstevniListek> getNavstevniListkyByNavstevaOsoba(NavstevaOsoba navstevaOsoba) {
        return navstevniListekRepository.findByNavstevaOsoba(navstevaOsoba);
    }

    public NavstevniListekTyp getNavstevniListekTyp(String idNavstevniListek){
        NavstevniListek navstevniListek = navstevniListekRepository.getDetail(idNavstevniListek);
        try {
            if (navstevniListek == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            navstevniListek.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), navstevniListek.getTyp().getNazevResx()));
            return navstevniListek.getTyp();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public NavstevniListekTyp getNavstevniListekTypByUzivatel(String idUzivatel) {
        NavstevniListekTyp navstevniListekTypUzivatele = uzivatelNavstevniListekTypRepository.findNavstevniListekTypByUzivatelId(idUzivatel);

        try {
            if (navstevniListekTypUzivatele == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
                
            navstevniListekTypUzivatele.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), navstevniListekTypUzivatele.getNazevResx()));
            return navstevniListekTypUzivatele; 
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }


}
