package cz.dp.share.services;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.base.Utils;
import cz.dp.share.entity.Zakazka;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.share.exceptions.ValidationException;
import cz.dp.share.repository.ZakazkaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Zakázka
 */
@Service
@TransactionalROE
public class ZakazkaServices {

    final static Logger logger = LogManager.getLogger(ZakazkaServices.class);

    @Autowired
    private ZakazkaRepository zakazkaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    /**
     * Detail
     * 
     * @param idZakazka Identifikátor záznamu
     * @param idZavod   Identifikátor závodu
     * @return Detail záznamu
     */
    public Zakazka getDetail(String idZakazka, String idZavod) {

        Zakazka zakazka = null;
        if (!StringUtils.isBlank(idZavod))
            zakazka = zakazkaRepository.getDetail(idZakazka, idZavod);
        else
            zakazka = zakazkaRepository.getDetail(idZakazka);

        return zakazka;
    }

    /**
     * Seznam
     * 
     * @param aktivita Aktivita
     * @param idZavod  Identifikátor závodu
     * @return Seznam záznamů
     */
    public List<Zakazka> getList(Boolean aktivita, String idZavod) {
        return getList(aktivita, Arrays.asList(idZavod));
    }

    /**
     * Seznam
     * 
     * @param aktivita Aktivita
     * @param idZavod  Identifikátor závodu
     * @return Seznam záznamů
     */
    public List<Zakazka> getList(Boolean aktivita, List<String> idsZavod) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Zakazka s");
        queryString.append("  left join fetch s.zavod zav");
        queryString.append(" where 1 = 1");

        if (idsZavod != null && idsZavod.size() > 0)
            queryString.append(" and zav.idZavod in (:idsZavod)");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (idsZavod != null && idsZavod.size() > 0)
            vysledek.setParameter("idsZavod", idsZavod);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<Zakazka> list = vysledek.getResultList();
        return list;
    }

    /**
     * Uložení záznamu
     * 
     * @param zakazka
     * @param zmena
     * @return
     * @throws NoSuchMessageException
     * @throws MainUniqueValueException
     * @throws MainValidationException
     */
    @TransactionalWrite
    public Zakazka save(Zakazka zakazka) throws UniqueValueException, NoSuchMessageException, ValidationException {

        Integer existSap = zakazkaRepository.existsBySapId(zakazka.getSapId(), zakazka.getZavod().getIdZavod(),
                Utils.toString(zakazka.getIdZakazka()));
        if (existSap > 0)
            throw new UniqueValueException(
                    messageSource.getMessage("sap.id.unique", null, LocaleContextHolder.getLocale()),
                    zakazka.getSapId(), true);

        zakazka.setCasZmn(Utils.getCasZmn());
        zakazka.setZmenuProvedl(Utils.getZmenuProv());

        zakazka = zakazkaRepository.save(zakazka);

        return zakazka;
    }
}