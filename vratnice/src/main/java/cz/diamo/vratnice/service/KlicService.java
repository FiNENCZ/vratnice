package cz.diamo.vratnice.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.KlicTyp;
import cz.diamo.vratnice.repository.KlicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class KlicService {

    @Autowired
    private KlicRepository klicRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;

    public List<Klic> getAllKeys() {
        return klicRepository.findAll();
    }

    @Transactional
    public Klic createKey(Klic klic) {
        klic.setCasZmn(Utils.getCasZmn());
        klic.setZmenuProvedl(Utils.getZmenuProv());
        return klicRepository.save(klic);
    }

    public List<Klic> getList(Boolean aktivita, Boolean specialni, String idLokalita, String idVratnice) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Klic s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (specialni != null)
            queryString.append(" and s.specialni = :specialni");

        if (idLokalita != null)
            queryString.append(" and s.lokalita.idLokalita = :idLokalita");
        
        if (idVratnice != null)
            queryString.append(" and s.vratnice.idVratnice = :idVratnice");
        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        if (specialni != null)
            vysledek.setParameter("specialni", specialni);

        if (idLokalita != null)
            vysledek.setParameter("idLokalita", idLokalita);

        if (idVratnice != null)
            vysledek.setParameter("idVratnice", idVratnice);
        
        @SuppressWarnings("unchecked")
        List<Klic> list = vysledek.getResultList();
        return list;

    }

    public Klic getDetail(String idKlic) {
        return klicRepository.getDetail(idKlic);
    }

    public Klic getDetailByChipCode(String kodCipu) {
        return klicRepository.getDetailByKodCipu(kodCipu);
    }

    public List<Klic> getBySpecialni(Boolean specialni) {
        return klicRepository.getBySpecialni(specialni);
    }


    public List<Klic> getKlicByAktivita(Boolean aktivita) {
        return klicRepository.findByAktivita(aktivita);
    }

    public KlicTyp getKlicTyp(String idKlic) {
        Klic klic = klicRepository.getDetail(idKlic);
        try {
            if (klic == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            klic.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), klic.getTyp().getNazevResx()));
            return klic.getTyp();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

}
