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
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.VjezdVozidlaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class VjezdVozidlaService {

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    @Autowired
	private MessageSource messageSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;

    public List<VjezdVozidla> getList(Boolean aktivita, Boolean nevyporadaneVjezdy, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();
        
        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM VjezdVozidla s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        if (nevyporadaneVjezdy != null) {
            if (!nevyporadaneVjezdy) {
                queryString.append("AND (s.zmenuProvedl <> 'kamery' AND s.zmenuProvedl IS NOT NULL) ");
            } else {
                queryString.append("AND (s.zmenuProvedl = 'kamery' OR s.zmenuProvedl IS NULL) ");
            }
        }

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());
        vysledek.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        @SuppressWarnings("unchecked")
        List<VjezdVozidla> list = vysledek.getResultList();
        return list;
    }

    public VjezdVozidla getDetail(String idVjezdVozidla) {
        return vjezdVozidlaRepository.getDetail(idVjezdVozidla);
    }

    public List<VjezdVozidla> getByRzVozidla(String rzVozidla) {
        return vjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    @Transactional
    public VjezdVozidla create(VjezdVozidla vjezdVozidla, Vratnice vratnice) {
        if (vjezdVozidla.getZmenuProvedl() == null ) {        
            vjezdVozidla.setCasZmn(Utils.getCasZmn());
            vjezdVozidla.setZmenuProvedl(Utils.getZmenuProv());
        }

        if (vjezdVozidla.getVratnice() == null)
            if (vratnice != null)
                vjezdVozidla.setVratnice(vratnice);

        return vjezdVozidlaRepository.save(vjezdVozidla);
    }

    public VozidloTyp getVozidloTyp(String idVozidlo) {
        VjezdVozidla vjezdVozidla = vjezdVozidlaRepository.getDetail(idVozidlo);
        try {
            if (vjezdVozidla == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            vjezdVozidla.getTypVozidla().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), vjezdVozidla.getTypVozidla().getNazevResx()));
            return vjezdVozidla.getTypVozidla();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

}
