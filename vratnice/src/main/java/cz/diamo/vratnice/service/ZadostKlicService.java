package cz.diamo.vratnice.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
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


    public List<ZadostKlic> getList(Boolean aktivita, String idUzivatel) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from ZadostKlic s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
        
        if (idUzivatel != null)
            queryString.append(" and s.uzivatel.idUzivatel = :idUzivatel");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (idUzivatel != null)
            vysledek.setParameter("idUzivatel", idUzivatel);

        @SuppressWarnings("unchecked")
        List<ZadostKlic> list = vysledek.getResultList();
        return list;
    }

    @TransactionalWrite
    public ZadostKlic save(ZadostKlic zadostKlic) throws ValidationException {
        if (zadostKlic.getKlic().isSpecialni() && StringUtils.isBlank(zadostKlic.getDuvod()))
            throw new ValidationException(messageSource.getMessage("klic.duvod.required", null, LocaleContextHolder.getLocale()));

        zadostKlic.setCasZmn(Utils.getCasZmn());
        zadostKlic.setZmenuProvedl(Utils.getZmenuProv());
        return zadostiKlicRepository.save(zadostKlic);
    }

    public ZadostKlic getDetail(String idZadostiKlic) {
        return zadostiKlicRepository.getDetail(idZadostiKlic);
    }

    public List<ZadostKlic> getZadostiByStav(Integer idZadostStav) {
        return zadostiKlicRepository.getZadostiByStav(idZadostStav, true);
    }

    public List<ZadostKlic> findByKlic(Klic klic) {
        return zadostiKlicRepository.findByKlic(klic);
    }

    public List<ZadostKlic> findByUzivatel(Uzivatel uzivatel) {
        return zadostiKlicRepository.findByUzivatel(uzivatel);
    }

    public long countByUzivatel(Uzivatel uzivatel) {
        return zadostiKlicRepository.countByUzivatel(uzivatel);
    }

}
