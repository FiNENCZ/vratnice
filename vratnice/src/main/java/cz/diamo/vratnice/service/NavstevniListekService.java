package cz.diamo.vratnice.service;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.AccessDeniedException;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.dto.NavstevaOsobaDto;
import cz.diamo.vratnice.dto.NavstevniListekUzivatelStavDto;
import cz.diamo.vratnice.dto.NavstevniListekDto;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListekUzivatelStav;
import cz.diamo.vratnice.entity.NavstevniListek;
import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.enums.NavstevniListekTypEnum;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.NavstevniListekRepository;
import cz.diamo.vratnice.repository.NavstevniListekTypRepository;
import cz.diamo.vratnice.repository.UzivatelNavstevniListekTypRepository;
import cz.diamo.vratnice.zadosti.services.ZadostiServices;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class NavstevniListekService {

    @Autowired
    private NavstevniListekRepository navstevniListekRepository;

    @Autowired
    private UzivatelNavstevniListekTypRepository uzivatelNavstevniListekTypRepository;

    @Autowired
    private NavstevniListekTypRepository navstevniListekTypRepository;

    @Autowired
    private NavstevniListekUzivatelStavService navstevniListekUzivatelStavService;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private NavstevaOsobaService navstevaOsobaService;

    @Autowired
    private ZadostiServices zadostiServices;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public NavstevniListek create(HttpServletRequest request, AppUserDto appUserDto,
                NavstevniListekDto navstevniListekDto, Vratnice vratnice) throws AccessDeniedException, NoSuchMessageException, BaseException {

        if (navstevniListekDto.getIdNavstevniListek() != null) 
            throw new AccessDeniedException(messageSource.getMessage("navstevni_listek.cannot_be_edited", null, LocaleContextHolder.getLocale()));

        // Vytvoření NavstevaOsoba jako entity (záznam v databázi)
        List<NavstevaOsoba> savedNavstevyOsoby = createNavstevyOsobyIfPresent(navstevniListekDto.getNavstevaOsoba());

        NavstevniListekTyp navstevniListekTyp = getNavstevniListekTypDleVsechUzivatelu(navstevniListekDto.getUzivateleStav());

        NavstevniListek navstevniListek = navstevniListekDto.toEntity();
        navstevniListek.setTyp(navstevniListekTyp);
        navstevniListek.setNavstevaOsoba(savedNavstevyOsoby);
        navstevniListek.setCasVytvoreni(new Timestamp(System.currentTimeMillis()));
        navstevniListek.setCasZmn(Utils.getCasZmn());
        navstevniListek.setZmenuProvedl(Utils.getZmenuProv());

        if (navstevniListek.getVratnice() == null)
            if (vratnice != null)
                navstevniListek.setVratnice(vratnice);

        NavstevniListek savedNavstevniListek = navstevniListekRepository.save(navstevniListek);


        List<NavstevniListekUzivatelStav> savedUzivateleStav = createNavstevniListekUzivatelStavy(savedNavstevniListek);
        savedNavstevniListek.setUzivateleStav(savedUzivateleStav);

        //TODO: -- ŽÁDOSTI -- napojit na žádosti
        //zadostiServices.saveNavstevniListek(request, appUserDto, new NavstevniListekDto(savedNavstevniListek));

        return savedNavstevniListek;
    }

    private List<NavstevaOsoba> createNavstevyOsobyIfPresent(List<NavstevaOsobaDto> navstevyOsobyDto) {
        if (navstevyOsobyDto == null || navstevyOsobyDto.isEmpty()) {
            return new ArrayList<NavstevaOsoba>();
        }

        List<NavstevaOsoba> navstevaOsobaEntities = navstevyOsobyDto.stream()
            .map(NavstevaOsobaDto::toEntity)
            .collect(Collectors.toList());

        List<NavstevaOsoba> savedNavstevaOsoby = navstevaOsobaEntities.stream()
        .map(navstevaOsoba -> {
            try {
                return navstevaOsobaService.create(navstevaOsoba);
            } catch (UniqueValueException | NoSuchMessageException e) {
                throw new RuntimeException(e);
            }
        })
        .collect(Collectors.toList());

        return savedNavstevaOsoby;

    }

    private List<NavstevniListekUzivatelStav> createNavstevniListekUzivatelStavy(NavstevniListek navstevniListek) {

        List<NavstevniListekUzivatelStav> savedUzivateleStavy = new ArrayList<NavstevniListekUzivatelStav>();;

        for (NavstevniListekUzivatelStav uzivatelStav : navstevniListek.getUzivateleStav()) {
            uzivatelStav.setNavstevniListek(new NavstevniListek(navstevniListek.getIdNavstevniListek()));
            NavstevniListekUzivatelStav savedUzivatelStav = navstevniListekUzivatelStavService.create(uzivatelStav);
            savedUzivateleStavy.add(savedUzivatelStav);
        }

        return savedUzivateleStavy;

    }

    private NavstevniListekTyp getNavstevniListekTypDleVsechUzivatelu(List<NavstevniListekUzivatelStavDto> uzivateleStavDto) {
        NavstevniListekTypEnum currentNavstevniListekTypEnum = NavstevniListekTypEnum.NAVSTEVNI_LISTEK_ELEKTRONICKY;

        for (NavstevniListekUzivatelStavDto uzivatelDto: uzivateleStavDto) {
            NavstevniListekTyp navstevniListekTyp = getNavstevniListekTypByUzivatel(uzivatelDto.getUzivatel().getId());
            if (navstevniListekTyp.getNavstevniListekTypEnum() == NavstevniListekTypEnum.NAVSTEVNI_LISTEK_PAPIROVY) {
                currentNavstevniListekTypEnum = NavstevniListekTypEnum.NAVSTEVNI_LISTEK_PAPIROVY;
            }

        }

        return new NavstevniListekTyp(currentNavstevniListekTypEnum);
    }

    public List<NavstevniListek> getList(Boolean aktivita, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM NavstevniListek s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        
        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.vratnice.idVratnice"));
        
        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        
        @SuppressWarnings("unchecked")
        List<NavstevniListek> list = vysledek.getResultList();

        if (list != null) {
            for (NavstevniListek navstevniListek : list) {
                navstevniListek.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), navstevniListek.getTyp().getNazevResx()));
                navstevniListek.setUzivateleStav(navstevniListekUzivatelStavService.getByNavstevniListek(navstevniListek.getIdNavstevniListek()));
            }
        }

        return list;
    }


    public NavstevniListek getDetail(String idNavstevniListek) throws RecordNotFoundException, NoSuchMessageException {
        NavstevniListek navstevniListek = navstevniListekRepository.getDetail(idNavstevniListek);
        navstevniListek.setUzivateleStav(navstevniListekUzivatelStavService.getByNavstevniListek(navstevniListek.getIdNavstevniListek()));
        return navstevniListek;
    }


    public NavstevniListekTyp getNavstevniListekTypByUzivatel(String idUzivatel) {
        NavstevniListekTyp navstevniListekTypUzivatele = uzivatelNavstevniListekTypRepository.findNavstevniListekTypByUzivatelId(idUzivatel);

        try {
            if (navstevniListekTypUzivatele == null) // pokud není nalezen, nastaví se PAPIROVY jako výchozí
                navstevniListekTypUzivatele = navstevniListekTypRepository.getDetail(NavstevniListekTypEnum.NAVSTEVNI_LISTEK_PAPIROVY.getValue());
            
                
            navstevniListekTypUzivatele.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), navstevniListekTypUzivatele.getNazevResx()));
            return navstevniListekTypUzivatele; 
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }


}
