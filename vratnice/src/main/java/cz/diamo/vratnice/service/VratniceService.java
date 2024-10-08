package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.repository.VratniceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
@Service
public class VratniceService {

    @Autowired
    private VratniceRepository vratniceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;

    public List<Vratnice> getList(Boolean aktivita, String idLokalita) throws RecordNotFoundException, NoSuchMessageException {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Vratnice s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (idLokalita != null)
            queryString.append(" and s.lokalita.idLokalita = :idLokalita");
        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (idLokalita != null)
            vysledek.setParameter("idLokalita", idLokalita);
        
        
        @SuppressWarnings("unchecked")
        List<Vratnice> list = vysledek.getResultList();

        if (list != null) {
            for (Vratnice vratnice : list) {
                vratnice = translateVratnice(vratnice);
            }
        }

        return list;
    }

    @Transactional
    public Vratnice save(Vratnice vratnice) throws RecordNotFoundException, NoSuchMessageException {
        vratnice.setCasZmn(Utils.getCasZmn());
        vratnice.setZmenuProvedl(Utils.getZmenuProv());
        Vratnice savedVratnice =  vratniceRepository.save(vratnice);
        return translateVratnice(savedVratnice);
    }

    public Vratnice getDetail(String id) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice vratnice =  vratniceRepository.getDetail(id);

        if (vratnice != null) 
            vratnice = translateVratnice(vratnice);
        

        return vratnice;
    }

    private Vratnice translateVratnice(Vratnice vratnice) throws RecordNotFoundException, NoSuchMessageException {
        if (vratnice.getVstupniKartyTyp().getNazevResx() != null)
            vratnice.getVstupniKartyTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), vratnice.getVstupniKartyTyp().getNazevResx()));
        return vratnice;
    }
}
