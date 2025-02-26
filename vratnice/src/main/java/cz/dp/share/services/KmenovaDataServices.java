package cz.dp.share.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cz.dp.share.annotation.TransactionalROE;
import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.base.Utils;
import cz.dp.share.entity.KmenovaData;
import cz.dp.share.repository.KmenovaDataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class KmenovaDataServices {

    final static Logger logger = LogManager.getLogger(KmenovaDataServices.class);

    @Autowired
    private KmenovaDataRepository kmenovaDataRepository;

    @Autowired
    private KmenovaDataServicesNewTrans kmenovaDataServicesNewTransaction;

    @PersistenceContext
    private EntityManager entityManager;

    public KmenovaData getDetail(String idKmenovaData, String idZavod) {

        if (!StringUtils.isBlank(idZavod))
            return kmenovaDataRepository.getDetail(idKmenovaData, idZavod);
        else
            return kmenovaDataRepository.getDetail(idKmenovaData);
    }

    public List<KmenovaData> getList(Boolean aktivita, Boolean zpracovano, String idZavod, String guidDavky) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from KmenovaData s");
        queryString.append(" left join fetch s.zavod zav");

        queryString.append(" where 1 = 1");
        if (!StringUtils.isBlank(idZavod))
            queryString.append(" and zav.idZavod = :idZavod");
        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
        if (zpracovano != null)
            queryString.append(" and s.zpracovano = :zpracovano");
        if (guidDavky != null)
            queryString.append(" and s.guidDavky = :guidDavky");

        queryString.append(" order by s.idKmenovaData ASC");
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (!StringUtils.isBlank(idZavod))
            vysledek.setParameter("idZavod", idZavod);
        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        if (zpracovano != null)
            vysledek.setParameter("zpracovano", zpracovano);
        if (guidDavky != null)
            vysledek.setParameter("guidDavky", guidDavky);

        @SuppressWarnings("unchecked")
        List<KmenovaData> list = vysledek.getResultList();
        return list;
    }

    @TransactionalWrite
    public KmenovaData save(KmenovaData kmenovaData) {

        kmenovaData.setCasZmn(Utils.getCasZmn());
        kmenovaData.setZmenuProvedl(Utils.getZmenuProv());
        kmenovaData = kmenovaDataRepository.save(kmenovaData);
        return kmenovaData;
    }

    @Async
    @TransactionalWrite
    public void aktualizaceZamestnancuAllAsync(String idZavod) {
        // načtení dat
        List<KmenovaData> seznam = getList(true, false, idZavod, null);
        aktualizaceZamestnancu(seznam);
    }

    @Async
    public void aktualizaceZamestnancuAsync(String guidDavky) {
        // načtení dat
        List<KmenovaData> seznam = getList(true, false, null, guidDavky);
        aktualizaceZamestnancu(seznam);
    }

    private void aktualizaceZamestnancu(List<KmenovaData> kmenovaData) {
        if (kmenovaData != null && kmenovaData.size() > 0) {
            for (KmenovaData kmenova : kmenovaData) {
                try {
                    kmenovaDataServicesNewTransaction.aktualizaceZamestnance(kmenova);
                } catch (Exception ex) {
                    logger.error(ex);
                    kmenovaDataServicesNewTransaction.zapsatChybu(kmenova, ex);
                }
            }
        }
    }

}
