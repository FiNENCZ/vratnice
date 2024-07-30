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
import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.repository.VratniceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
@Service
public class VratniceService {

    @Autowired
    private VratniceRepository vratniceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;

    public List<Vratnice> getList(Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Vratnice s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        
        @SuppressWarnings("unchecked")
        List<Vratnice> list = vysledek.getResultList();
        return list;
    }

    public Vratnice save(Vratnice vratnice) {
        vratnice.setCasZmn(Utils.getCasZmn());
        vratnice.setZmenuProvedl(Utils.getZmenuProv());
        return vratniceRepository.save(vratnice);
    }

    public Vratnice getDetail(String id) {
        return vratniceRepository.getDetail(id);
    }

    public NavstevniListekTyp getVstupniKartyTyp(String idVratnice) {
        Vratnice vratnice = vratniceRepository.getDetail(idVratnice);
        try {
            if (vratnice == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            vratnice.getVstupniKartyTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), vratnice.getVstupniKartyTyp().getNazevResx()));
            return vratnice.getVstupniKartyTyp();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }


}
