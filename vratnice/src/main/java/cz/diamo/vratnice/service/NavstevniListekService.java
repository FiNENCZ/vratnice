package cz.diamo.vratnice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.dto.NavstevaOsobaDto;
import cz.diamo.vratnice.dto.NavstevniListekDto;
import cz.diamo.vratnice.dto.NavstevniListekTypDto;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;
import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.enums.NavstevniListekTypEnum;
import cz.diamo.vratnice.repository.NavstevaOsobaRepository;
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

    @Autowired
    private NavstevaOsobaRepository navstevaOsobaRepository;



    @Transactional
    public NavstevniListek create(NavstevniListekDto navstevniListekDto) {
        // Vytvoření NavstevaOsoba jako entity (záznam v databázi)
        if (navstevniListekDto.getNavstevaOsoba() != null && !navstevniListekDto.getNavstevaOsoba().isEmpty()) {
    
            List<NavstevaOsoba> navstevaOsobaEntities = navstevniListekDto.getNavstevaOsoba().stream()
                .map(NavstevaOsobaDto::toEntity)
                .collect(Collectors.toList());
    
            List<NavstevaOsoba> savedNavstevaOsoby = navstevaOsobaEntities.stream()
                .map(navstevaOsobaRepository::save)
                .collect(Collectors.toList());
    
            navstevniListekDto.setNavstevaOsoba(savedNavstevaOsoby.stream()
                .map(NavstevaOsobaDto::new)
                .collect(Collectors.toList()));
        }

        // Rozhodnutí o NavstevniListekTyp. Pokud alespoň jedna z návštěv má definovaný papírový typ, tak návštěvní lístek bude papírový
        NavstevniListekTypEnum currentNavstevniListekTypEnum = NavstevniListekTypEnum.NAVSTEVNI_LISTEK_ELEKTRONICKY;

        for (UzivatelDto uzivatelDto: navstevniListekDto.getUzivatel()) {
            NavstevniListekTyp navstevniListekTyp = getNavstevniListekTypByUzivatel(uzivatelDto.getId());
            if (navstevniListekTyp.getNavstevniListekTypEnum() == NavstevniListekTypEnum.NAVSTEVNI_LISTEK_PAPIROVY) {
                currentNavstevniListekTypEnum = NavstevniListekTypEnum.NAVSTEVNI_LISTEK_PAPIROVY;
            }

        }

        navstevniListekDto.setTyp(new NavstevniListekTypDto(new NavstevniListekTyp(currentNavstevniListekTypEnum)));

        NavstevniListek navstevniListek = navstevniListekDto.toEntity();
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
