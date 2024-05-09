package cz.diamo.share.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import cz.diamo.share.constants.Constants;
import cz.diamo.share.dto.Ws02ZastupDto;
import cz.diamo.share.dto.opravneni.FilterOpravneniDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.Zastup;
import cz.diamo.share.entity.ZastupSimple;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.NullObjectException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.repository.ZastupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class ZastupServices {

    final static Logger logger = LogManager.getLogger(ZastupServices.class);

    @Autowired
    private ZastupRepository zastupRepository;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private Wso2Services wso2Services;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    public Zastup getDetail(String guid) {

        return zastupRepository.getDetail(guid);
    }

    public List<Zastup> getList(String idZavod, Date datumOd, Date datumDo, String idUzivatel,
            String idUzivatelZastupce, Boolean aktivita,
            FilterOpravneniDto opravneni)
            throws RecordNotFoundException, NullObjectException, NoSuchMessageException {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Zastup s ");
        queryString.append(" join fetch s.uzivatel uzivatel");
        queryString.append(" join fetch s.uzivatelZastupce zastupce");
        queryString.append(" join fetch uzivatel.zavod zavod");
        queryString.append(" where 1 = 1");
        if (!StringUtils.isBlank(idZavod))
            queryString.append(" and zavod.idZavod = :idZavod");
        if (!StringUtils.isBlank(idUzivatel))
            queryString.append(" and uzivatel.idUzivatel = :idUzivatel");
        if (!StringUtils.isBlank(idUzivatelZastupce))
            queryString.append(" and zastupce.idUzivatel = :idUzivatelZastupce");
        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
        if (datumDo != null)
            queryString.append(" and s.platnostOd <= :datumDo");
        if (datumOd != null)
            queryString.append(" and s.platnostDo >= :datumOd");
        if (opravneni != null) {
            queryString.append(" and " + opravneni.getHqlWhere("uzivatel.idUzivatel"));
            queryString.append(" and " + opravneni.getHqlWhere("zastupce.idUzivatel"));
        }
        queryString.append(" order by s.platnostOd DESC, s.platnostDo DESC, uzivatel.prijmeni ASC, uzivatel.jmeno ASC");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (!StringUtils.isBlank(idZavod))
            vysledek.setParameter("idZavod", idZavod);
        if (!StringUtils.isBlank(idUzivatel))
            vysledek.setParameter("idUzivatel", idUzivatel);
        if (!StringUtils.isBlank(idUzivatelZastupce))
            vysledek.setParameter("idUzivatelZastupce", idUzivatelZastupce);
        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        if (datumOd != null)
            vysledek.setParameter("datumOd", Utils.setMinTime(datumOd));
        if (datumDo != null)
            vysledek.setParameter("datumDo", Utils.setMaxTime(datumDo));
        if (opravneni != null)
            vysledek.setParameter("idVedouci", opravneni.getIdVedouci());
        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<Zastup> list = vysledek.getResultList();

        return list;
    }

    public List<ZastupSimple> getListDostupne(String idUzivatel, String idZastup)
            throws RecordNotFoundException, NullObjectException, NoSuchMessageException {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select distinct");
        queryString.append(" uzivatel.id_uzivatel,");
        queryString.append(" uzivatel.id_zavod,");
        queryString.append(" uzivatel.sap_id,");
        queryString.append(" uzivatel.nazev");
        queryString.append(" from");
        queryString.append(" " + Constants.SCHEMA + ".zastup zastup");
        queryString.append(
                " join " + Constants.SCHEMA + ".uzivatel uzivatel on (uzivatel.id_uzivatel = zastup.id_uzivatel)");
        queryString.append(" where");
        queryString.append(" (zastup.aktivita = true or zastup.id_uzivatel = :idZastup)");
        queryString.append(" and zastup.id_uzivatel_zastupce = :idUzivatel");
        queryString.append(" and zastup.platnost_od <= now()");
        queryString.append(" and zastup.platnost_do >= now()");
        queryString.append(" order by uzivatel.nazev ASC");

        Query vysledek = entityManager.createNativeQuery(queryString.toString(), ZastupSimple.class);

        vysledek.setParameter("idUzivatel", idUzivatel);
        vysledek.setParameter("idZastup", Utils.toString(idZastup));

        @SuppressWarnings("unchecked")
        List<ZastupSimple> list = vysledek.getResultList();

        return list;
    }

    @TransactionalWrite
    public Zastup save(Zastup zastup, boolean distribuce) throws BaseException {

        if (zastup.getUzivatel().getIdUzivatel()
                .equals(zastup.getUzivatelZastupce().getIdUzivatel()))
            throw new ValidationException(
                    messageSource.getMessage("nelze.zastupovat.sebe", null, LocaleContextHolder.getLocale()));
        zastup.setPlatnostOd(Utils.setMinTime(zastup.getPlatnostOd()));
        zastup.setPlatnostDo(Utils.setMaxTime(zastup.getPlatnostDo()));

        boolean exists = zastupRepository.exists(Utils.toString(zastup.getGuid()),
                zastup.getUzivatel().getIdUzivatel(),
                zastup.getUzivatelZastupce().getIdUzivatel(), zastup.getPlatnostOd(),
                zastup.getPlatnostDo());

        if (exists)
            throw new ValidationException(
                    messageSource.getMessage("zastup.exists", null, LocaleContextHolder.getLocale()));

        if (StringUtils.isBlank(zastup.getGuid()))
            zastup.setGuid(Utils.vratGuid());

        zastup.setPlatnostOd(Utils.setMinTime(zastup.getPlatnostOd()));
        zastup.setPlatnostDo(Utils.setMaxTime(zastup.getPlatnostDo()));

        zastup.setCasZmn(Utils.getCasZmn());
        zastup.setZmenuProvedl(Utils.getZmenuProv());
        zastup = zastupRepository.save(zastup);

        // propsání do ostatních systémů
        if (distribuce) {
            zastup
                    .setUzivatel(uzivatelRepository.getDetail(zastup.getUzivatel().getIdUzivatel()));
            zastup.setUzivatelZastupce(
                    uzivatelRepository.getDetail(zastup.getUzivatelZastupce().getIdUzivatel()));

            List<Ws02ZastupDto> zastupy = new ArrayList<>();
            zastupy.add(new Ws02ZastupDto(zastup));
            String chyba = wso2Services.zastupy(zastupy);
            if (StringUtils.isBlank(chyba))
                zastup.setDistribuovano(true);
            else {
                zastup.setDistribuovano(false);
                zastup.setChybaDistribuce(chyba);
            }
            zastup = zastupRepository.save(zastup);
        }

        return zastup;
    }

    @TransactionalWrite
    public String synchronizovatVse(String idZavod) throws Exception {
        // načtu všechny aktvní zástupy
        List<Zastup> zastupy = getList(idZavod, Calendar.getInstance().getTime(), Utils.getMaxDate(false), null,
                null, true, null);

        if (zastupy != null) {
            List<Ws02ZastupDto> zastupyWso2 = new ArrayList<Ws02ZastupDto>();
            for (Zastup zastup : zastupy) {
                zastupyWso2.add(new Ws02ZastupDto(zastup));
            }

            String chyba = wso2Services.zastupy(zastupyWso2);
            for (Zastup zastup : zastupy) {
                if (StringUtils.isBlank(chyba))
                    zastup.setDistribuovano(true);
                else {
                    zastup.setDistribuovano(false);
                    zastup.setChybaDistribuce(chyba);
                }
                zastup = zastupRepository.save(zastup);
            }

            return chyba;
        }

        return null;
    }

    @TransactionalWrite
    public void aktualizovat(Ws02ZastupDto ws02ZastupDto) {
        // dohledání zástupů
        Zastup zastup = zastupRepository.getDetail(ws02ZastupDto.getGuid());

        boolean zmena = false;
        if (zastup == null) {
            zastup = new Zastup();
            zastup.setGuid(ws02ZastupDto.getGuid());
        }

        if (zastup.getUzivatel() == null
                || !zastup.getUzivatel().getSapId().equals(ws02ZastupDto.getSapIdZastupovany())) {
            Uzivatel uzivatel = uzivatelRepository.getDetailBySapId(ws02ZastupDto.getSapIdZastupovany());
            if (uzivatel == null)
                return;
            zastup.setUzivatel(uzivatel);
            zmena = true;
        }

        if (zastup.getUzivatelZastupce() == null
                || !zastup.getUzivatelZastupce().getSapId().equals(ws02ZastupDto.getSapIdZastupce())) {
            Uzivatel uzivatel = uzivatelRepository.getDetailBySapId(ws02ZastupDto.getSapIdZastupce());
            if (uzivatel == null)
                return;
            zastup.setUzivatelZastupce(uzivatel);
            zmena = true;
        }

        if (zastup.getPlatnostOd() == null || !zastup.getPlatnostOd().equals(ws02ZastupDto.getPlatnostOd())) {
            zastup.setPlatnostOd(ws02ZastupDto.getPlatnostOd());
            zmena = true;
        }
        if (zastup.getPlatnostDo() == null || !zastup.getPlatnostDo().equals(ws02ZastupDto.getPlatnostDo())) {
            zastup.setPlatnostDo(ws02ZastupDto.getPlatnostDo());
            zmena = true;
        }
        if (!zastup.getAktivita().equals(ws02ZastupDto.getAktivita())) {
            zastup.setAktivita(ws02ZastupDto.getAktivita());
            zmena = true;
        }

        if (zmena) {
            try {
                zastup = save(zastup, false);
            } catch (Exception e) {
                logger.error(e);
            }

        }

    }

}