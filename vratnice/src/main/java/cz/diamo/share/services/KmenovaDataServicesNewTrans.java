package cz.diamo.share.services;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.annotation.TransactionalWriteTrans;
import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.KmenovaData;
import cz.diamo.share.entity.PracovniPozice;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.Zakazka;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.repository.KmenovaDataRepository;
import cz.diamo.share.repository.PracovniPoziceRepository;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.repository.ZakazkaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@TransactionalWriteTrans
public class KmenovaDataServicesNewTrans {

    final static Logger logger = LogManager.getLogger(KmenovaDataServicesNewTrans.class);

    @Autowired
    private KmenovaDataRepository kmenovaDataRepository;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private ZakazkaRepository zakazkaRepository;

    @Autowired
    private PracovniPoziceRepository pracovniPoziceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    @TransactionalWriteTrans
    public void aktualizaceZamestnance(KmenovaData kmenovaData) throws Exception {

        // dohledání uživatele
        Uzivatel uzivatel = uzivatelRepository.getDetailBySapId(kmenovaData.getSapId());
        if (uzivatel != null)
            entityManager.detach(uzivatel);
        if (uzivatel == null)
            uzivatel = new Uzivatel();

        Date datumAktualizace = Utils.getMinDate();
        if (uzivatel.getPlatnostKeDni() != null && uzivatel.getPlatnostKeDni().compareTo(datumAktualizace) == 1)
            datumAktualizace = uzivatel.getPlatnostKeDni();

        if (StringUtils.isBlank(uzivatel.getIdUzivatel()) || StringUtils.isBlank(uzivatel.getIdUzivatel()))
            datumAktualizace = Utils.getMinDate();
        // uzivatel.setZavod(kmenovaData.getZavod()); 2024-08-26 přesun do aktualizace
        // udaju - změna ORG.STR, přesun na nový závod

        // aktualizace/založení uživatele
        if ((uzivatel.getPlatnostKeDni() == null || uzivatel.getPlatnostKeDni().compareTo(kmenovaData.getPlatnostKeDni()) == -1)) {
            if (aktualizaceUdaju(uzivatel, kmenovaData)) {

                uzivatel.setPlatnostKeDni(kmenovaData.getPlatnostKeDni());
                uzivatel.setKmenovaData(kmenovaData);
                uzivatel = uzivatelServices.save(uzivatel, false, false, false);
            } else {
                uzivatel.setPlatnostKeDni(kmenovaData.getPlatnostKeDni());
                uzivatel = uzivatelServices.save(uzivatel, false, false, false);
            }

        }

        // nastavení zpracování kmenových dat
        kmenovaDataRepository.oznacitZaZpracovane(kmenovaData.getIdKmenovaData(), Utils.getCasZmn(), Utils.getZmenuProv());

        entityManager.detach(kmenovaData);
        entityManager.detach(uzivatel);
    }

    @TransactionalWriteTrans
    public void zapsatChybu(KmenovaData kmenovaData, Exception e) {
        kmenovaDataRepository.zapsatChybu(kmenovaData.getIdKmenovaData(), e.toString(), Utils.getCasZmn(), Utils.getZmenuProv());
    }

    @TransactionalROE
    private boolean aktualizaceUdaju(Uzivatel uzivatel, KmenovaData kmenovaData) throws RecordNotFoundException, NoSuchMessageException {
        boolean zmena = false;
        boolean ukonceniPomeru = kmenovaData.getDatumUkonceniPracPomeru() != null;

        if (uzivatel.getZavod() == null || !StringUtils.equals(kmenovaData.getZavod().getIdZavod(), uzivatel.getZavod().getIdZavod())) {
            uzivatel.setZavod(kmenovaData.getZavod());
            zmena = true;
        }

        if (StringUtils.isBlank(uzivatel.getNazev())) {
            uzivatel.setNazev(kmenovaData.getJmeno() + " " + kmenovaData.getPrijmeni());
            if (!StringUtils.isBlank(kmenovaData.getTitulPred()))
                uzivatel.setNazev(kmenovaData.getTitulPred() + " " + uzivatel.getNazev());
            if (!StringUtils.isBlank(kmenovaData.getTitulZa()))
                uzivatel.setNazev(uzivatel.getNazev() + " " + kmenovaData.getTitulZa());
            zmena = true;
        }

        if (kmenovaData.getJmeno() != null && !kmenovaData.getJmeno().equals(uzivatel.getJmeno())) {
            uzivatel.setJmeno(kmenovaData.getJmeno());
            uzivatel.setNazev(kmenovaData.getJmeno() + " " + kmenovaData.getPrijmeni());
            if (!StringUtils.isBlank(kmenovaData.getTitulPred()))
                uzivatel.setNazev(kmenovaData.getTitulPred() + " " + uzivatel.getNazev());
            if (!StringUtils.isBlank(kmenovaData.getTitulZa()))
                uzivatel.setNazev(uzivatel.getNazev() + " " + kmenovaData.getTitulZa());
            zmena = true;
        }
        if (kmenovaData.getPrijmeni() != null && !kmenovaData.getPrijmeni().equals(uzivatel.getPrijmeni())) {
            uzivatel.setPrijmeni(kmenovaData.getPrijmeni());
            uzivatel.setNazev(kmenovaData.getJmeno() + " " + kmenovaData.getPrijmeni());
            if (!StringUtils.isBlank(kmenovaData.getTitulPred()))
                uzivatel.setNazev(kmenovaData.getTitulPred() + " " + uzivatel.getNazev());
            if (!StringUtils.isBlank(kmenovaData.getTitulZa()))
                uzivatel.setNazev(uzivatel.getNazev() + " " + kmenovaData.getTitulZa());
            zmena = true;
        }
        if (kmenovaData.getTitulPred() != null && !kmenovaData.getTitulPred().equals(uzivatel.getTitulPred())) {
            uzivatel.setTitulPred(kmenovaData.getTitulPred());
            uzivatel.setNazev(kmenovaData.getJmeno() + " " + kmenovaData.getPrijmeni());
            if (!StringUtils.isBlank(kmenovaData.getTitulPred()))
                uzivatel.setNazev(kmenovaData.getTitulPred() + " " + uzivatel.getNazev());
            if (!StringUtils.isBlank(kmenovaData.getTitulZa()))
                uzivatel.setNazev(uzivatel.getNazev() + " " + kmenovaData.getTitulZa());
            zmena = true;
        }
        if (kmenovaData.getTitulZa() != null && !kmenovaData.getTitulZa().equals(uzivatel.getTitulZa())) {
            uzivatel.setTitulZa(kmenovaData.getTitulZa());
            uzivatel.setNazev(kmenovaData.getJmeno() + " " + kmenovaData.getPrijmeni());
            if (!StringUtils.isBlank(kmenovaData.getTitulPred()))
                uzivatel.setNazev(kmenovaData.getTitulPred() + " " + uzivatel.getNazev());
            if (!StringUtils.isBlank(kmenovaData.getTitulZa()))
                uzivatel.setNazev(uzivatel.getNazev() + " " + kmenovaData.getTitulZa());
            zmena = true;
        }

        if (kmenovaData.getUlice() != null && !kmenovaData.getUlice().equals(uzivatel.getUlice())) {
            uzivatel.setUlice(kmenovaData.getUlice());
            zmena = true;
        }

        if (kmenovaData.getPsc() != null && !kmenovaData.getPsc().equals(uzivatel.getPsc())) {
            uzivatel.setPsc(kmenovaData.getPsc());
            zmena = true;
        }

        if (kmenovaData.getObec() != null && !kmenovaData.getObec().equals(uzivatel.getObec())) {
            uzivatel.setObec(kmenovaData.getObec());
            zmena = true;
        }

        if (kmenovaData.getCisloPopisne() != null && !kmenovaData.getCisloPopisne().equals(uzivatel.getCisloPopisne())) {
            uzivatel.setCisloPopisne(kmenovaData.getCisloPopisne());
            zmena = true;
        }

        if (kmenovaData.getDilciPersonalniOblastSapId() != null && !kmenovaData.getDilciPersonalniOblastSapId().equals(uzivatel.getDilciPersonalniOblast())) {
            uzivatel.setDilciPersonalniOblast(kmenovaData.getDilciPersonalniOblastSapId());
            zmena = true;
        }

        if (kmenovaData.getTel() != null && !kmenovaData.getTel().equals(uzivatel.getTel())) {
            uzivatel.setTel(kmenovaData.getTel());
            zmena = true;
        }

        if (kmenovaData.getEmail() != null && !kmenovaData.getEmail().equals(uzivatel.getEmail())) {
            uzivatel.setEmail(kmenovaData.getEmail());
            zmena = true;
        }

        if (kmenovaData.getSoukromyEmail() != null && !kmenovaData.getSoukromyEmail().equals(uzivatel.getSoukromyEmail())) {
            uzivatel.setSoukromyEmail(kmenovaData.getSoukromyEmail());
            zmena = true;
        }

        if (kmenovaData.getPruznaPracDoba() != null && !kmenovaData.getPruznaPracDoba().equals(uzivatel.getPruznaPracDoba())) {
            uzivatel.setPruznaPracDoba(kmenovaData.getPruznaPracDoba());
            zmena = true;
        }

        // zruším platnost, pokud je profil založen
        if (uzivatel.getDatumOd() == null)
            uzivatel.setDatumOd(kmenovaData.getPlatnostKeDni());

        if (StringUtils.isBlank(uzivatel.getSapId())) {
            uzivatel.setSapId(kmenovaData.getSapId());
            zmena = true;
        }

        if (kmenovaData.getDatumUkonceniPracPomeru() != null && !kmenovaData.getDatumUkonceniPracPomeru().equals(uzivatel.getDatumDo())) {
            uzivatel.setDatumDo(kmenovaData.getDatumUkonceniPracPomeru());
            zmena = true;
        }

        String zakazkaSapId = uzivatel.getZakazka() != null ? uzivatel.getZakazka().getSapId() : null;
        if (!StringUtils.equals(kmenovaData.getZakazkaSapId(), zakazkaSapId)) {
            Zakazka zakazka = null;

            if (StringUtils.isNotBlank(kmenovaData.getZakazkaSapId()))
                zakazka = zakazkaRepository.getDetailBySapId(kmenovaData.getZakazkaSapId(), uzivatel.getZavod().getIdZavod(), true);

            uzivatel.setZakazka(zakazka);
        }

        String sapIdPracovniPozicePuv = "";
        String sapIdPracovniPoziceNew = kmenovaData.getPlanovaneMistoSapId();
        if ((Utils.sapIdPracovniPoziceNull.equals(sapIdPracovniPoziceNew) || Utils.sapIdPracovniPoziceNull2.equals(sapIdPracovniPoziceNew))
                && kmenovaData.getDatumUkonceniPracPomeru() != null)
            sapIdPracovniPoziceNew = "";

        // pokusím se dohledat pracovní pozici pro dohodáře
        if (Utils.sapIdPracovniPoziceNull.equals(sapIdPracovniPoziceNew) || Utils.sapIdPracovniPoziceNull2.equals(sapIdPracovniPoziceNew)) {
            sapIdPracovniPoziceNew = pracovniPoziceRepository.getSapIdProDohodare(uzivatel.getSapId());
            if (StringUtils.isBlank(sapIdPracovniPoziceNew))
                sapIdPracovniPoziceNew = Utils.sapIdPracovniPoziceNull;
        }

        if (uzivatel.getPracovniPozice() != null)
            sapIdPracovniPozicePuv = uzivatel.getPracovniPozice().getSapId();
        if (!sapIdPracovniPozicePuv.equals(sapIdPracovniPoziceNew)) {
            if (StringUtils.isBlank(sapIdPracovniPoziceNew)) {
                uzivatel.setPracovniPozice(null);
            } else {
                // dohledání pracovní pozice
                PracovniPozice pracovniPozice = pracovniPoziceRepository.getDetailBySapId(sapIdPracovniPoziceNew);
                uzivatel.setPracovniPozice(pracovniPozice);
                if (uzivatel.getPracovniPozice() == null && !ukonceniPomeru)
                    throw new RecordNotFoundException(messageSource.getMessage("pracovni.pozice.zamestnance.not.found", null, LocaleContextHolder.getLocale()),
                            sapIdPracovniPoziceNew, true);
            }
            zmena = true;
        }

        return zmena;
    }

}
