package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.NavstevniListekStav;
import cz.diamo.vratnice.entity.NavstevniListekUzivatelStav;
import cz.diamo.vratnice.enums.NavstevniListekStavEnum;
import cz.diamo.vratnice.repository.NavstevniListekUzivatelStavRepository;
import jakarta.transaction.Transactional;

@Service
public class NavstevniListekUzivatelStavService {

    @Autowired
    private NavstevniListekUzivatelStavRepository navstevaUzivatelStavRepository;

    @Autowired
    private ResourcesComponent resourcesComponent;


    @Transactional
    public NavstevniListekUzivatelStav create(NavstevniListekUzivatelStav navstevaUzivatelStav) {
        return navstevaUzivatelStavRepository.save(navstevaUzivatelStav);
    }

    public List<NavstevniListekUzivatelStav> list() {
        return navstevaUzivatelStavRepository.findAll();
    }

    public  List<NavstevniListekUzivatelStav> getByNavstevniListek(String idNavstevniListe) throws RecordNotFoundException, NoSuchMessageException {
        List<NavstevniListekUzivatelStav> uzivateleStav =  navstevaUzivatelStavRepository.getByNavstevniListek(idNavstevniListe);

        if (uzivateleStav != null) {
            for (NavstevniListekUzivatelStav uzivatelStav: uzivateleStav) {
                uzivatelStav.getStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), uzivatelStav.getStav().getNazevResx()));
            }
        }

        return uzivateleStav;
    }

    public NavstevniListekUzivatelStav zmenitStav(String idNavstevniListek, String idUzivatel, NavstevniListekStavEnum novyStavEnum) {
        NavstevniListekUzivatelStav uzivatelStav = navstevaUzivatelStavRepository.getByNavstevniListekAndUzivatel(idNavstevniListek, idUzivatel);
        uzivatelStav.setStav(new NavstevniListekStav(novyStavEnum));
        NavstevniListekUzivatelStav savedUzivatelStav = create(uzivatelStav);

        return savedUzivatelStav;
    }

}
