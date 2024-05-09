package cz.diamo.share.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import cz.diamo.share.entity.PracovniPozicePodrizene;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.repository.PracovniPozicePodrizeneRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class PracovniPozicePodrizeneServices {

    final static Logger logger = LogManager.getLogger(PracovniPozicePodrizeneServices.class);

    @Autowired
    private PracovniPozicePodrizeneRepository pracovniPozicePodrizeneRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    public PracovniPozicePodrizene getDetail(String idPracovniPozicePodrizene) {

        PracovniPozicePodrizene pracovniPozicePodrizene;

        pracovniPozicePodrizene = pracovniPozicePodrizeneRepository.getDetail(idPracovniPozicePodrizene);

        return pracovniPozicePodrizene;
    }

    public PracovniPozicePodrizene getDetail(String idPracovniPozice, String idPracovniPozicePodrizeny) {

        PracovniPozicePodrizene pracovniPozicePodrizene;

        pracovniPozicePodrizene = pracovniPozicePodrizeneRepository.getDetail(idPracovniPozice,
                idPracovniPozicePodrizeny);

        return pracovniPozicePodrizene;
    }

    public boolean jePodrizeny(String idUzivatel, String idUzivatelPodrizeny, boolean pouzePrimy) {
        Integer count = 0;
        if (pouzePrimy)
            count = pracovniPozicePodrizeneRepository.existsPodrizenostPrima(idUzivatel, idUzivatelPodrizeny);
        else
            count = pracovniPozicePodrizeneRepository.existsPodrizenost(idUzivatel, idUzivatelPodrizeny);

        return count != null && count > 0;
    }

    public List<PracovniPozicePodrizene> getList(String idZavod, String idPracovniPozice, Boolean primePodrizeni,
            Integer aktivita)
            throws RecordNotFoundException {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from PracovniPozicePodrizene s");
        queryString.append(" left join fetch s.pracovniPozice pozice");
        queryString.append(" left join fetch s.pracovniPozicePodrizeny pozicePodrizeny");
        queryString.append(" where 1 = 1");
        if (!StringUtils.isBlank(idPracovniPozice))
            queryString.append(" and pozice.idPracovniPozice = :idPracovniPozice");
        if (primePodrizeni != null)
            queryString.append(" and s.primyPodrizeny = :primePodrizeni");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        queryString.append(" order by s.idPracovniPozicePodrizene ASC");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (!StringUtils.isBlank(idPracovniPozice))
            vysledek.setParameter("idPracovniPozice", idPracovniPozice);
        if (primePodrizeni != null)
            vysledek.setParameter("primePodrizeni", primePodrizeni);
        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<PracovniPozicePodrizene> list = vysledek.getResultList();

        return list;
    }

    @TransactionalWrite
    public PracovniPozicePodrizene save(PracovniPozicePodrizene pracovniPozicePodrizene)
            throws UniqueValueException, NoSuchMessageException {

        Integer exist = pracovniPozicePodrizeneRepository.exists(
                pracovniPozicePodrizene.getPracovniPozice().getIdPracovniPozice(),
                pracovniPozicePodrizene.getPracovniPozicePodrizeny().getIdPracovniPozice(),
                Utils.toString(pracovniPozicePodrizene.getIdPracovniPozicePodrizene()));
        if (exist > 0)
            throw new UniqueValueException(
                    messageSource.getMessage("podrizena.pozice.exists", null, LocaleContextHolder.getLocale()),
                    pracovniPozicePodrizene.getPracovniPozicePodrizeny().getIdPracovniPozice(),
                    true);

        pracovniPozicePodrizene.setCasZmn(Utils.getCasZmn());
        pracovniPozicePodrizene.setZmenuProvedl(Utils.getZmenuProv());
        pracovniPozicePodrizene = pracovniPozicePodrizeneRepository.save(pracovniPozicePodrizene);
        return pracovniPozicePodrizene;
    }

}
