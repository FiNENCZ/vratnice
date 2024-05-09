package cz.diamo.share.services;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import cz.diamo.share.dto.PracovniPoziceNodeDto;
import cz.diamo.share.entity.PracovniPozice;
import cz.diamo.share.entity.PracovniPozicePodrizene;
import cz.diamo.share.entity.PracovniPozicePrehled;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.repository.PracovniPozicePodrizeneRepository;
import cz.diamo.share.repository.PracovniPoziceRepository;
import cz.diamo.share.rest.dto.PracovniPoziceDohodaDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class PracovniPoziceServices {

    final static Logger logger = LogManager.getLogger(PracovniPoziceServices.class);

    @Autowired
    private PracovniPoziceRepository pracovniPoziceRepository;

    @Autowired
    private PracovniPozicePodrizeneRepository pracovniPozicePodrizeneRepository;

    @Autowired
    private PracovniPozicePodrizeneServices pracovniPozicePodrizeneServices;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    public PracovniPozice getDetail(String idPracovniPozice) throws RecordNotFoundException {

        PracovniPozice pracovniPozice;
        pracovniPozice = pracovniPoziceRepository.getDetail(idPracovniPozice);
        return pracovniPozice;
    }

    public List<PracovniPozice> getList(Boolean aktivita, Boolean dohoda) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from PracovniPozice s");
        queryString.append(" where 1 = 1");
        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
        if (dohoda != null)
            queryString.append(" and s.dohoda = :dohoda");

        queryString.append(" order by s.nazev ASC");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        if (dohoda != null)
            vysledek.setParameter("dohoda", dohoda);

        @SuppressWarnings("unchecked")
        List<PracovniPozice> list = vysledek.getResultList();

        return list;
    }

    public List<PracovniPozicePrehled> getListPrehled(String idZavod, Boolean aktivita, Boolean aktualni) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select ");
        queryString.append(" concat(p.id_pracovni_pozice, '#', uzivatel.id_uzivatel) as id, ");
        queryString.append(" p.id_pracovni_pozice, ");
        queryString.append(" p.sap_id, ");
        queryString.append(" p.sap_id_nadrizeny, ");
        queryString.append(" p.nazev, ");
        queryString.append(" p.zkratka, ");
        queryString.append(" uzivatel.sap_id as uzivatel_sap_id, ");
        queryString.append(" uzivatel.nazev as uzivatel_nazev, ");
        queryString.append(" uzivatel.prijmeni, ");
        queryString.append(" uzivatel.jmeno, ");
        queryString.append(" uzivatel.email, ");
        queryString.append(" uzivatel.tel, ");
        queryString.append(" zavod.nazev as zavod_nazev, ");
        queryString.append(" zavod.sap_id as zavod_sap_id");
        queryString.append(" from ");

        queryString.append(
                " " + Constants.SCHEMA + ".pracovni_pozice p left join " + Constants.SCHEMA
                        + ".uzivatel uzivatel on (uzivatel.id_pracovni_pozice = p.id_pracovni_pozice");

        if (!StringUtils.isBlank(idZavod))
            queryString.append("and uzivatel.id_zavod = :idZavod");
        if (aktualni != null && aktualni)
            queryString.append(
                    " and (uzivatel.datum_od is null or uzivatel.datum_od <= now()) and (uzivatel.datum_do is null or uzivatel.datum_do >= now())");

        queryString.append(") ");

        queryString.append(" left join " + Constants.SCHEMA + ".zavod zavod on (zavod.id_zavod = uzivatel.id_zavod)");

        queryString.append(" where 1 = 1");
        if (aktivita != null)
            queryString.append(" and p.aktivita = :aktivita");
        if (aktualni != null && aktualni)
            queryString.append(
                    " and (p.platnost_od is null or p.platnost_od <= now()) and (p.platnost_do is null or p.platnost_do >= now())");
        // queryString.append(" order by p.sap_id_nadrizeny ASC");
        queryString.append(" order by p.zkratka ASC, p.nazev ASC");

        Query vysledek = entityManager.createNativeQuery(queryString.toString(), PracovniPozicePrehled.class);

        if (!StringUtils.isBlank(idZavod))
            vysledek.setParameter("idZavod", idZavod);
        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<PracovniPozicePrehled> list = vysledek.getResultList();

        return list;
    }

    public List<PracovniPoziceNodeDto> getStromPracovniPozice(String idZavod, Boolean aktualni)
            throws NoSuchMessageException, BaseException {

        // seznam
        List<PracovniPozicePrehled> list = getListPrehled(idZavod, true, aktualni);

        List<PracovniPoziceNodeDto> roots = new ArrayList<PracovniPoziceNodeDto>();
        List<PracovniPoziceNodeDto> childs = new ArrayList<PracovniPoziceNodeDto>();

        if (list != null && list.size() > 0) {
            for (PracovniPozicePrehled pracovniPozicePrehled : list) {
                if (StringUtils.isBlank(pracovniPozicePrehled.getSapIdNadrizeny())
                        || pracovniPozicePrehled.getSapIdNadrizeny().equals(pracovniPozicePrehled.getSapId())) {
                    // if
                    // (!StringUtils.isBlank(pracovniPozicePrehled.getProfilSapId()))14.04.2023
                    // - odstraněno, kvůli odvolání ředitele PKU nebyla videt struktura
                    roots.add(new PracovniPoziceNodeDto(pracovniPozicePrehled));
                } else
                    childs.add(new PracovniPoziceNodeDto(pracovniPozicePrehled));

            }

            if (roots == null || roots.size() == 0)
                throw new BaseException(messageSource.getMessage("pracovni.pozice.strom.roots.not.found", null,
                        LocaleContextHolder.getLocale()));

            // přiřazení potomků
            for (PracovniPoziceNodeDto root : roots) {
                priraditPotomky(root, childs);
            }

        }

        return roots;
    }

    private void priraditPotomky(PracovniPoziceNodeDto root, List<PracovniPoziceNodeDto> childs) {

        for (PracovniPoziceNodeDto child : childs) {
            if (!root.getPracovniPozice().getId().equals(child.getPracovniPozice().getId())
                    && root.getPracovniPozice().getSapId().equals(child.getPracovniPozice().getSapIdNadrizeny())) {
                priraditPotomky(child, childs);
                root.getPodrizenePracovniPozice().add(child);
                // přidání fulltextu
                child.getPracovniPozice().vyplnitFullTextPotomka(root.getPracovniPozice());
            }
        }
    }

    @TransactionalWrite
    public PracovniPozice save(PracovniPozice pracovniPozice)
            throws UniqueValueException, NoSuchMessageException {

        Integer exist = pracovniPoziceRepository.existsBySapId(pracovniPozice.getSapId(),
                Utils.toString(pracovniPozice.getIdPracovniPozice()));

        if (exist > 0 && pracovniPozice.getAktivita())
            throw new UniqueValueException(
                    messageSource.getMessage("sap.id.unique", null, LocaleContextHolder.getLocale()),
                    pracovniPozice.getSapId(), true);

        pracovniPozice.setCasZmn(Utils.getCasZmn());
        pracovniPozice.setZmenuProvedl(Utils.getZmenuProv());
        pracovniPozice = pracovniPoziceRepository.save(pracovniPozice);
        return pracovniPozice;
    }

    @TransactionalWrite
    public void zpracovatPracovniPoziceDohodare(List<PracovniPoziceDohodaDto> pracovniPozice)
            throws NoSuchMessageException, BaseException {

        // načtu původní pracovní pozice
        List<PracovniPozice> listPuvodni = getList(null, true);
        // všechny pracovní pozice
        List<PracovniPozice> listAll = getList(true, null);

        Timestamp casZmn = Utils.getCasZmn();
        String zmenuProvedl = Utils.getZmenuProv();

        if (pracovniPozice != null) {
            for (PracovniPoziceDohodaDto pracovniPoziceDohodaDto : pracovniPozice) {
                Utils.validate(pracovniPoziceDohodaDto);

                PracovniPozice nova = pracovniPoziceDohodaDto.getPracovniPozice();
                if (listPuvodni != null && listPuvodni.size() > 0) {
                    for (PracovniPozice puvodni : listPuvodni) {
                        if (nova.getSapIdDohodar().equals(puvodni.getSapIdDohodar())) {
                            nova.setIdPracovniPozice(puvodni.getIdPracovniPozice());
                            listPuvodni.remove(puvodni);
                            break;
                        }
                    }
                }
                nova = save(nova);

                // smazání u nadřízených
                pracovniPozicePodrizeneRepository.odstranit(nova.getIdPracovniPozice(), casZmn, zmenuProvedl);

                // uložení pro nadřízené
                if (!StringUtils.isBlank(nova.getSapIdNadrizeny())) {

                    List<String> podrizeniProOdstraneni = new ArrayList<String>();
                    PracovniPozice nadrizeny = pracovniPoziceRepository.getDetailBySapId(nova.getSapIdNadrizeny());

                    if (nadrizeny != null) {
                        // přímé
                        PracovniPozicePodrizene pracovniPozicePodrizene = new PracovniPozicePodrizene();
                        pracovniPozicePodrizene.setPracovniPozice(nadrizeny);
                        pracovniPozicePodrizene.setPracovniPozicePodrizeny(nova);
                        pracovniPozicePodrizene.setPrimyPodrizeny(true);

                        // dohledání
                        PracovniPozicePodrizene pracovniPozicePodrizenePuv = pracovniPozicePodrizeneRepository
                                .getDetail(
                                        pracovniPozicePodrizene.getPracovniPozice().getIdPracovniPozice(),
                                        pracovniPozicePodrizene.getPracovniPozicePodrizeny().getIdPracovniPozice());
                        if (pracovniPozicePodrizenePuv != null) {
                            pracovniPozicePodrizene
                                    .setIdPracovniPozicePodrizene(
                                            pracovniPozicePodrizenePuv.getIdPracovniPozicePodrizene());
                            if (!pracovniPozicePodrizenePuv.getPrimyPodrizeny()
                                    || !pracovniPozicePodrizenePuv.getAktivita()) {
                                pracovniPozicePodrizene = pracovniPozicePodrizeneServices.save(pracovniPozicePodrizene);
                            }
                        } else {
                            pracovniPozicePodrizene = pracovniPozicePodrizeneServices.save(pracovniPozicePodrizene);
                        }

                        // uložení pro všechny nadřízené
                        if (!StringUtils.isBlank(nadrizeny.getSapIdNadrizeny()))
                            ulozitProNadrizene(nova, nadrizeny, listAll, podrizeniProOdstraneni, 1);
                    }

                }
            }
        }

        // odstranění původních
        if (listPuvodni != null && listPuvodni.size() > 0) {

            for (PracovniPozice puvodni : listPuvodni) {
                puvodni.setAktivita(false);
                pracovniPozicePodrizeneRepository.odstranit(puvodni.getIdPracovniPozice(), casZmn, zmenuProvedl);
                save(puvodni);
            }
        }

    }

    @TransactionalWrite
    public void zpracovatPracovniPozice(List<PracovniPozice> pracovniPozice)
            throws NoSuchMessageException, BaseException {

        List<String> poziceProOdstraneni = pracovniPoziceRepository.getListIdAktivni();
        List<String> podrizeniProOdstraneni = pracovniPozicePodrizeneRepository.getListIdAktivni();

        // pracovní pozice
        for (PracovniPozice pozice : pracovniPozice) {

            // dohledání
            boolean save = false;
            PracovniPozice pracovniPozicePuv = pracovniPoziceRepository
                    .getDetailBySapId(pozice.getSapId());
            if (pracovniPozicePuv != null) {
                pozice.setIdPracovniPozice(pracovniPozicePuv.getIdPracovniPozice());
                if (!pracovniPozicePuv.getAktivita())
                    save = true;
                if (!pozice.getZkratka().equals(pracovniPozicePuv.getZkratka()))
                    save = true;
                if (!pozice.getNazev().equals(pracovniPozicePuv.getNazev()))
                    save = true;
                String sapIdNadrizeny = pozice.getSapIdNadrizeny();
                if (sapIdNadrizeny == null)
                    sapIdNadrizeny = "";
                String sapIdNadrizenyPuv = pracovniPozicePuv.getSapIdNadrizeny();
                if (sapIdNadrizenyPuv == null)
                    sapIdNadrizenyPuv = "";

                if (!sapIdNadrizeny.equals(sapIdNadrizenyPuv))
                    save = true;
                if (!Utils.stejnyDen(pozice.getPlatnostOd(), pracovniPozicePuv.getPlatnostOd()))
                    save = true;
                if (!Utils.stejnyDen(pozice.getPlatnostDo(), pracovniPozicePuv.getPlatnostDo()))
                    save = true;

                poziceProOdstraneni.remove(pracovniPozicePuv.getIdPracovniPozice());
            } else
                save = true;

            if (save) {
                String idNadrizeny = pozice.getSapIdNadrizeny();
                pozice = save(pozice);
                pozice.setSapIdNadrizeny(idNadrizeny);
            }
        }

        // podřízení
        for (PracovniPozice pozice : pracovniPozice) {

            if (!StringUtils.isBlank(pozice.getSapIdNadrizeny())) {

                PracovniPozice nadrizeny = getPracovniPozice(pozice.getSapIdNadrizeny(),
                        pracovniPozice);

                // přímé
                PracovniPozicePodrizene pracovniPozicePodrizene = new PracovniPozicePodrizene();
                pracovniPozicePodrizene.setPracovniPozice(nadrizeny);
                pracovniPozicePodrizene.setPracovniPozicePodrizeny(pozice);
                pracovniPozicePodrizene.setPrimyPodrizeny(true);

                // dohledání
                PracovniPozicePodrizene pracovniPozicePodrizenePuv = pracovniPozicePodrizeneRepository.getDetail(
                        pracovniPozicePodrizene.getPracovniPozice().getIdPracovniPozice(),
                        pracovniPozicePodrizene.getPracovniPozicePodrizeny().getIdPracovniPozice());
                if (pracovniPozicePodrizenePuv != null) {
                    pracovniPozicePodrizene
                            .setIdPracovniPozicePodrizene(pracovniPozicePodrizenePuv.getIdPracovniPozicePodrizene());
                    podrizeniProOdstraneni.remove(pracovniPozicePodrizene.getIdPracovniPozicePodrizene());
                    if (!pracovniPozicePodrizenePuv.getPrimyPodrizeny()
                            || !pracovniPozicePodrizenePuv.getAktivita()) {
                        pracovniPozicePodrizene = pracovniPozicePodrizeneServices.save(pracovniPozicePodrizene);
                    }
                } else {
                    pracovniPozicePodrizene = pracovniPozicePodrizeneServices.save(pracovniPozicePodrizene);
                }

                // uložení pro všechny nadřízené
                if (!StringUtils.isBlank(nadrizeny.getSapIdNadrizeny()))
                    ulozitProNadrizene(pozice, nadrizeny, pracovniPozice, podrizeniProOdstraneni, 1);
            }
        }

        // odstranění nepoužitých
        String zmenuProvedl = Utils.getZmenuProv();
        Timestamp casZmn = Utils.getCasZmn();

        if (poziceProOdstraneni != null && poziceProOdstraneni.size() > 0) {
            for (String idPozice : poziceProOdstraneni) {
                pracovniPoziceRepository.odstranit(idPozice, casZmn, zmenuProvedl);
            }
        }

        if (podrizeniProOdstraneni != null && podrizeniProOdstraneni.size() > 0) {
            for (String idPodrizeny : podrizeniProOdstraneni) {
                pracovniPozicePodrizeneRepository.odstranit(idPodrizeny, casZmn, zmenuProvedl);
            }
        }
    }

    private PracovniPozice getPracovniPozice(String sapId, List<PracovniPozice> seznamPracovnichPozic)
            throws NoSuchMessageException, RecordNotFoundException {

        if (seznamPracovnichPozic != null && seznamPracovnichPozic.size() > 0) {
            for (PracovniPozice pracovniPozice : seznamPracovnichPozic) {
                if (pracovniPozice.getSapId().equals(sapId))
                    return pracovniPozice;
            }

        }
        throw new RecordNotFoundException(
                messageSource.getMessage("pracovni.pozice.not.found", null, LocaleContextHolder.getLocale()), sapId,
                true);

    }

    @TransactionalWrite
    private void ulozitProNadrizene(PracovniPozice podrizeny, PracovniPozice nadrizeny,
            List<PracovniPozice> pracovnipozice, List<String> podrizeniProOdstraneni, Integer vrstva)
            throws NoSuchMessageException, BaseException {

        if (vrstva > 10)
            throw new BaseException(String.format(
                    messageSource.getMessage("pracovni.pozice.chyba.zacykleni", null, LocaleContextHolder.getLocale()),
                    nadrizeny.getSapId(), nadrizeny.getSapIdNadrizeny()));

        PracovniPozice nadrizenyNew = getPracovniPozice(nadrizeny.getSapIdNadrizeny(), pracovnipozice);
        PracovniPozicePodrizene pracovniPozicePodrizene = new PracovniPozicePodrizene();
        pracovniPozicePodrizene.setPracovniPozice(nadrizenyNew);
        pracovniPozicePodrizene.setPracovniPozicePodrizeny(podrizeny);
        pracovniPozicePodrizene.setPrimyPodrizeny(false);

        // dohledání
        PracovniPozicePodrizene pracovniPozicePodrizenePuv = pracovniPozicePodrizeneRepository.getDetail(
                pracovniPozicePodrizene.getPracovniPozice().getIdPracovniPozice(),
                pracovniPozicePodrizene.getPracovniPozicePodrizeny().getIdPracovniPozice());
        if (pracovniPozicePodrizenePuv != null) {
            pracovniPozicePodrizene
                    .setIdPracovniPozicePodrizene(pracovniPozicePodrizenePuv.getIdPracovniPozicePodrizene());
            podrizeniProOdstraneni.remove(pracovniPozicePodrizene.getIdPracovniPozicePodrizene());
            if (pracovniPozicePodrizenePuv.getPrimyPodrizeny() || !pracovniPozicePodrizenePuv.getAktivita()) {
                pracovniPozicePodrizene = pracovniPozicePodrizeneServices.save(pracovniPozicePodrizene);
            }
        } else {
            pracovniPozicePodrizene = pracovniPozicePodrizeneServices.save(pracovniPozicePodrizene);
        }

        if (!StringUtils.isBlank(nadrizenyNew.getSapIdNadrizeny())) {
            vrstva++;
            ulozitProNadrizene(podrizeny, nadrizenyNew, pracovnipozice, podrizeniProOdstraneni, vrstva);
        }

    }

}
