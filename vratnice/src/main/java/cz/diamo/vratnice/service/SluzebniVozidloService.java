package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.entity.SluzebniVozidloFunkce;
import cz.diamo.vratnice.entity.SluzebniVozidloKategorie;
import cz.diamo.vratnice.entity.SluzebniVozidloStav;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.repository.SluzebniVozidloRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class SluzebniVozidloService {

    @Autowired
    private SluzebniVozidloRepository sluzebniVozidloRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;


    public List<SluzebniVozidlo> getList(Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from SluzebniVozidlo s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        
        @SuppressWarnings("unchecked")
        List<SluzebniVozidlo> list = vysledek.getResultList();
        return list;
    }

    @Transactional
    public SluzebniVozidlo create(SluzebniVozidlo sluzebniVozidlo) throws UniqueValueException, NoSuchMessageException {
        if (sluzebniVozidlo.getIdSluzebniVozidlo() == null || sluzebniVozidlo.getIdSluzebniVozidlo().isEmpty()){
            if (sluzebniVozidloRepository.existsByRz(sluzebniVozidlo.getRz()))
                throw new UniqueValueException(
                        messageSource.getMessage("sluzebni_vozidlo.rz.unique", null, LocaleContextHolder.getLocale()));
        }
        sluzebniVozidlo.setCasZmn(Utils.getCasZmn());
        sluzebniVozidlo.setZmenuProvedl(Utils.getZmenuProv());
        return sluzebniVozidloRepository.save(sluzebniVozidlo);
    }

    public SluzebniVozidlo getDetail(String id) {
        return sluzebniVozidloRepository.getDetail(id);
    }

    public Boolean isSluzebniVozidlo(String rz) {
        return sluzebniVozidloRepository.existsByRz(rz);
    }

    public SluzebniVozidlo getByRz(String rz) {
        return sluzebniVozidloRepository.getByRz(rz);
    }

    public List<SluzebniVozidlo> getSluzebniVozidloByStav(String stav) {
        return sluzebniVozidloRepository.getSluzebniVozidloByStav(stav);
    }

    public List<SluzebniVozidlo> getSluzebniVozidloByAktivita(Boolean aktivita) {
        return sluzebniVozidloRepository.findByAktivita(aktivita);
    }

    public VozidloTyp getVozidloTyp(String idVozidlo) {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloRepository.getDetail(idVozidlo);
        try {
            if (sluzebniVozidlo == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            sluzebniVozidlo.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), sluzebniVozidlo.getTyp().getNazevResx()));
            return sluzebniVozidlo.getTyp();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public SluzebniVozidloKategorie getKategorie(String idVozidlo) {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloRepository.getDetail(idVozidlo);
        try {
            if (sluzebniVozidlo == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            sluzebniVozidlo.getKategorie().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), sluzebniVozidlo.getKategorie().getNazevResx()));
            return sluzebniVozidlo.getKategorie();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public SluzebniVozidloFunkce getFunkce(String idVozidlo) {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloRepository.getDetail(idVozidlo);
        try {
            if (sluzebniVozidlo == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            sluzebniVozidlo.getFunkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), sluzebniVozidlo.getFunkce().getNazevResx()));
            return sluzebniVozidlo.getFunkce();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public SluzebniVozidloStav getVozidloStav(String idVozidlo) {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloRepository.getDetail(idVozidlo);
        try {
            if (sluzebniVozidlo == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            sluzebniVozidlo.getStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), sluzebniVozidlo.getStav().getNazevResx()));
            return sluzebniVozidlo.getStav();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

}
