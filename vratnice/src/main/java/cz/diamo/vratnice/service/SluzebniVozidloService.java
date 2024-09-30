package cz.diamo.vratnice.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.controller.SluzebniVozidloController;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.enums.SluzebniVozidloKategorieEnum;
import cz.diamo.vratnice.repository.SluzebniVozidloRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class SluzebniVozidloService {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloController.class);
    
    @Autowired
    private SluzebniVozidloRepository sluzebniVozidloRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private VratniceService vratniceService;


    public List<SluzebniVozidlo> getList(Boolean aktivita) throws RecordNotFoundException, NoSuchMessageException {
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

        if (list != null) {
            for (SluzebniVozidlo sluzebniVozidlo : list) {
                sluzebniVozidlo = translateSluzebniVozidlo(sluzebniVozidlo);
            }
        }


        return list;
    }

    @Transactional
    public SluzebniVozidlo create(SluzebniVozidlo sluzebniVozidlo) throws UniqueValueException, NoSuchMessageException, RecordNotFoundException {
        if (sluzebniVozidlo.getIdSluzebniVozidlo() == null || sluzebniVozidlo.getIdSluzebniVozidlo().isEmpty()){
            if (sluzebniVozidloRepository.existsByRz(sluzebniVozidlo.getRz()))
                throw new UniqueValueException(
                        messageSource.getMessage("sluzebni_vozidlo.rz.unique", null, LocaleContextHolder.getLocale()));
        }
        sluzebniVozidlo.setCasZmn(Utils.getCasZmn());
        sluzebniVozidlo.setZmenuProvedl(Utils.getZmenuProv());

        SluzebniVozidlo savedSluzebniVozidlo = sluzebniVozidloRepository.save(sluzebniVozidlo);

        return translateSluzebniVozidlo(savedSluzebniVozidlo);
    }

    public SluzebniVozidlo getDetail(String id) throws RecordNotFoundException, NoSuchMessageException {
        SluzebniVozidlo sluzebniVozidlo =  sluzebniVozidloRepository.getDetail(id);
        
        if (sluzebniVozidlo != null) {
            sluzebniVozidlo = translateSluzebniVozidlo(sluzebniVozidlo);
        }

        return sluzebniVozidlo;
    }

    private SluzebniVozidlo translateSluzebniVozidlo(SluzebniVozidlo sluzebniVozidlo) throws RecordNotFoundException, NoSuchMessageException {
        sluzebniVozidlo.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), sluzebniVozidlo.getTyp().getNazevResx()));
        sluzebniVozidlo.getKategorie().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), sluzebniVozidlo.getKategorie().getNazevResx()));
        
        if (sluzebniVozidlo.getFunkce() != null )
            sluzebniVozidlo.getFunkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), sluzebniVozidlo.getFunkce().getNazevResx()));
        
        sluzebniVozidlo.getStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), sluzebniVozidlo.getStav().getNazevResx()));

        return sluzebniVozidlo;
    }

    public Boolean muzeSluzebniVozidloProjetVratnici(String rz, String idVratnice) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice vratniceKamery = vratniceService.getDetail(idVratnice);

        if (vratniceKamery == null) 
            throw new RecordNotFoundException(
                String.format(messageSource.getMessage("vratnice.not_found", null, LocaleContextHolder.getLocale())));
        

        Lokalita zavodKameryLokalita = vratniceKamery.getLokalita();

        
        SluzebniVozidlo sluzebniVozidlo = getByRz(rz);

        if (sluzebniVozidlo == null) 
            return false;
        
        if (sluzebniVozidlo.getKategorie().getSluzebniVozidloKategorieEnum() == SluzebniVozidloKategorieEnum.SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE)
            return true;
        
        if (sluzebniVozidlo.getLokality() != null) {
            for (Lokalita lokalita : sluzebniVozidlo.getLokality()) {
                if (lokalita.getIdLokalita().equals(zavodKameryLokalita.getIdLokalita()))
                    return true;
            }
        }


        return false;
    }

    public SluzebniVozidlo getByRz(String rz) {
        return sluzebniVozidloRepository.getByRz(rz);
    }
}
