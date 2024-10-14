package cz.diamo.vratnice.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.ZadostKlicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class ZadostKlicService {

    @Autowired
    private ZadostKlicRepository zadostiKlicRepository;

    @Autowired
    private MessageSource messageSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;



    public List<ZadostKlic> getList(Boolean aktivita, String idUzivatel, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        String idVratny = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM ZadostKlic s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");
        
        if (idUzivatel != null)
            queryString.append("AND s.uzivatel.idUzivatel = :idUzivatel ");

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.klic.vratnice.idVratnice"));


        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idVratny);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (idUzivatel != null)
            vysledek.setParameter("idUzivatel", idUzivatel);

        @SuppressWarnings("unchecked")
        List<ZadostKlic> list = vysledek.getResultList();

        if (list != null) {
            for (ZadostKlic zadostKlic : list) {
                zadostKlic.getZadostStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), zadostKlic.getZadostStav().getNazevResx()));
            }
        }


        return list;
    }

    @TransactionalWrite
    public ZadostKlic save(ZadostKlic zadostKlic) throws ValidationException, RecordNotFoundException, NoSuchMessageException {
        if (zadostKlic.getKlic().isSpecialni() && StringUtils.isBlank(zadostKlic.getDuvod()))
            throw new ValidationException(messageSource.getMessage("klic.duvod.required", null, LocaleContextHolder.getLocale()));

        zadostKlic.setCasZmn(Utils.getCasZmn());
        zadostKlic.setZmenuProvedl(Utils.getZmenuProv());

        ZadostKlic savedZadostKlic =  zadostiKlicRepository.save(zadostKlic);
        return translateZadostKlic(savedZadostKlic);
    }

    public ZadostKlic getDetail(String idZadostiKlic) throws RecordNotFoundException, NoSuchMessageException {
        ZadostKlic zadostKlic =  zadostiKlicRepository.getDetail(idZadostiKlic);

        if (zadostKlic != null)
            zadostKlic = translateZadostKlic(zadostKlic);
    
        return zadostKlic;
    }

    private ZadostKlic translateZadostKlic(ZadostKlic zadostKlic) throws RecordNotFoundException, NoSuchMessageException {
        if (zadostKlic.getZadostStav().getNazevResx() != null)
            zadostKlic.getZadostStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), zadostKlic.getZadostStav().getNazevResx()));
        
            return zadostKlic;
    }


    public List<ZadostKlic> findByKlic(Klic klic) {
        return zadostiKlicRepository.findByKlic(klic);
    }

    public long countByUzivatel(Uzivatel uzivatel) {
        return zadostiKlicRepository.countByUzivatel(uzivatel);
    }
}
