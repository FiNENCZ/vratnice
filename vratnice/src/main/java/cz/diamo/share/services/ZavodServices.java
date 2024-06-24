package cz.diamo.share.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.repository.ZavodRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class ZavodServices {

    final static Logger logger = LogManager.getLogger(ZavodServices.class);

    @Autowired
    private ZavodRepository zavodRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    public Zavod getDetail(String idZavod) {

        return zavodRepository.getDetail(idZavod);
    }

    public Zavod getDetailBySapId(String sapId) {

        return zavodRepository.getDetailBySapId(sapId);
    }

    /**
     * Seznam závodů
     * @param idZavodu - závod vyjmutý ze seznamu
     * @param aktivita
     * @return
     */
    public List<Zavod> getList(String idZavodu, Boolean aktivita) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Zavod s");
        queryString.append(" where 1 = 1");

        if (idZavodu != null)
            queryString.append(" and s.idZavod != :idZavodu");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        queryString.append(" order by s.nazev ASC");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (idZavodu != null)
            vysledek.setParameter("idZavodu", idZavodu);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<Zavod> list = vysledek.getResultList();
        return list;
    }

    public Zavod getByNazev (String nazev) {
        return zavodRepository.getByNazev(nazev);
    }

    @TransactionalWrite
    public Zavod save(Zavod zavod) throws UniqueValueException, NoSuchMessageException {

        Integer exist = zavodRepository.existsBySapId(zavod.getSapId(), Utils.toString(zavod.getIdZavod()));
        if (exist > 0)
            throw new UniqueValueException(
                    messageSource.getMessage("sapid.unique", null, LocaleContextHolder.getLocale()), zavod.getSapId(),
                    true);

        zavod.setCasZmn(Utils.getCasZmn());
        zavod.setZmenuProvedl(Utils.getZmenuProv());
        zavod = zavodRepository.save(zavod);
        return zavod;
    }

}
