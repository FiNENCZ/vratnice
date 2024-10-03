package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.NavstevniListekStav;
import cz.diamo.vratnice.entity.NavstevniListekUzivatelStav;
import cz.diamo.vratnice.enums.NavstevniListekStavEnum;
import cz.diamo.vratnice.repository.NavstevniListekUzivatelStavRepository;
import cz.diamo.vratnice.zadosti.services.ZadostiServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class NavstevniListekUzivatelStavService {

    @Autowired
    private NavstevniListekUzivatelStavRepository navstevaUzivatelStavRepository;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ZadostiServices zadostiServices;


    @Transactional
    public NavstevniListekUzivatelStav create(NavstevniListekUzivatelStav navstevaUzivatelStav) {
        navstevaUzivatelStav.setCasZmn(Utils.getCasZmn());
        navstevaUzivatelStav.setZmenuProvedl(Utils.getZmenuProv());
        return navstevaUzivatelStavRepository.save(navstevaUzivatelStav);
    }

    public NavstevniListekUzivatelStav getDetail(String idNavstevniListekUzivatelStav) {
        return navstevaUzivatelStavRepository.getDetail(idNavstevniListekUzivatelStav);
    }

    @Transactional
    public NavstevniListekUzivatelStav pridatPoznamku(HttpServletRequest request, AppUserDto appUserDto, 
            String idNavstevniListekUzivatelStav, String poznamka) throws NoSuchMessageException, BaseException {
                
        NavstevniListekUzivatelStav navstevniListekUzivatelStav = getDetail(idNavstevniListekUzivatelStav);

        if (navstevniListekUzivatelStav == null) 
            throw new RecordNotFoundException(messageSource.getMessage("navstevni_listek_uzivatel_stav.not_found", null, LocaleContextHolder.getLocale()));
        
        navstevniListekUzivatelStav.setPoznamka(poznamka);
        NavstevniListekUzivatelStav savedNavstevniListekUzivatelStav = create(navstevniListekUzivatelStav);

        //TODO: -- ŽÁDOSTI -- napojit na žádosti
        //zadostiServices.navstevniListekPridatPoznamku(request, appUserDto, new NavstevniListekUzivatelStavDto(savedNavstevniListekUzivatelStav));

        return savedNavstevniListekUzivatelStav;
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

    public NavstevniListekUzivatelStav zmenitStav(String idNavstevniListek, String idUzivatel, NavstevniListekStavEnum novyStavEnum) throws RecordNotFoundException, NoSuchMessageException {
        NavstevniListekUzivatelStav uzivatelStav = navstevaUzivatelStavRepository.getByNavstevniListekAndUzivatel(idNavstevniListek, idUzivatel);

        if (uzivatelStav == null)
            throw new RecordNotFoundException(messageSource.getMessage("navstevni_listek_uzivatel_stav.not_found", null, LocaleContextHolder.getLocale()));

        uzivatelStav.setStav(new NavstevniListekStav(novyStavEnum));
        NavstevniListekUzivatelStav savedUzivatelStav = create(uzivatelStav);

        return savedUzivatelStav;
    }

}
