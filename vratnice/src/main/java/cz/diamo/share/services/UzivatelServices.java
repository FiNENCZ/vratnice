package cz.diamo.share.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.opravneni.FilterOpravneniDto;
import cz.diamo.share.entity.Opravneni;
import cz.diamo.share.entity.Role;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.UzivatelModul;
import cz.diamo.share.entity.UzivatelOpravneni;
import cz.diamo.share.entity.UzivatelZavod;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.repository.UzivatelModulRepository;
import cz.diamo.share.repository.UzivatelOpravneniRepository;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.repository.UzivatelZavodRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class UzivatelServices {

    final static Logger logger = LogManager.getLogger(UzivatelServices.class);

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private UzivatelOpravneniRepository uzivatelOpravneniRepository;

    @Autowired
    private UzivatelZavodRepository uzivatelZavodRepository;

    @Autowired
    private UzivatelModulRepository uzivatelModulRepository;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    @TransactionalROE
    public Uzivatel getDetail(String idUzivatel) throws RecordNotFoundException, NoSuchMessageException {
        return getDetail(idUzivatel, true);
    }

    @TransactionalROE
    public Uzivatel getDetail(String idUzivatel, boolean preklady)
            throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelRepository.getDetail(idUzivatel);

        if (uzivatel != null) {
            // načtení rolí
            uzivatel.setOpravneni(uzivatelOpravneniRepository.listOpravneni(uzivatel.getIdUzivatel(), true));
            uzivatel.setRole(uzivatelOpravneniRepository.listRole(uzivatel.getIdUzivatel(), true));

            // načtení ostatních závodů
            uzivatel.setOstatniZavody(uzivatelZavodRepository.listZavod(uzivatel.getIdUzivatel()));

            if (preklady && uzivatel.getRole() != null && uzivatel.getRole().size() > 0) {
                for (Role role : uzivatel.getRole()) {
                    role.setNazev(
                            resourcesComponent.getResources(LocaleContextHolder.getLocale(), role.getNazevResx()));
                }
            }
        }
        return uzivatel;
    }

    public List<Uzivatel> getList(String idZavod, FilterOpravneniDto opravneni, Boolean aktivita)
            throws RecordNotFoundException {
        return getList(idZavod, opravneni, Calendar.getInstance().getTime(), aktivita);
    }

    public List<Uzivatel> getList(String idZavod, FilterOpravneniDto opravneni) throws RecordNotFoundException {
        return getList(idZavod, opravneni, Calendar.getInstance().getTime());
    }

    public List<Uzivatel> getList(String idZavod, FilterOpravneniDto opravneni, Date platnostKeDni)
            throws RecordNotFoundException {
        return getList(idZavod, opravneni, Calendar.getInstance().getTime(), null);
    }

    public List<Uzivatel> getList(String idZavod, FilterOpravneniDto opravneni, Date platnostKeDni, Boolean aktivita)
            throws RecordNotFoundException {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Uzivatel s");
        queryString.append(" left join fetch s.zavod zavod");
        queryString.append(" left join fetch s.zakazka zakazka");
        queryString.append(" left join fetch s.pracovniPozice pracovniPozice");
        queryString.append(" where idUzivatel != 'XXUZ0000000001'");

        if (idZavod != null)
            queryString.append(" and zavod.idZavod = :idZavod");
        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
        if (opravneni != null)
            queryString.append(" and " + opravneni.getHqlWhere("s.idUzivatel"));
        if (platnostKeDni != null)
            queryString.append(
                    " and (s.datumOd is null or s.datumOd < :platnostKeDni) and (s.datumDo is null or s.datumDo > :platnostKeDni)");
        queryString.append(" and s.aktivita = true");

        queryString.append(" order by s.prijmeni ASC, s.jmeno ASC");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (idZavod != null)
            vysledek.setParameter("idZavod", idZavod);
        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        if (opravneni != null)
            vysledek.setParameter("idVedouci", opravneni.getIdVedouci());
        if (platnostKeDni != null)
            vysledek.setParameter("platnostKeDni", platnostKeDni);

        @SuppressWarnings("unchecked")
        List<Uzivatel> list = vysledek.getResultList();

        return list;
    }

    @TransactionalWrite
    public Uzivatel save(Uzivatel uzivatel, boolean ukladatRole, boolean ukladatZavody, boolean ukladatModuly)
            throws BaseException {

        // kontrola jedinečnosti sapId
        Integer existSapId = uzivatelRepository.existsBySapId(uzivatel.getSapId(),
                Utils.toString(uzivatel.getIdUzivatel()));
        if (existSapId > 0)
            throw new UniqueValueException(
                    messageSource.getMessage("sapid.unique", null, LocaleContextHolder.getLocale()),
                    uzivatel.getSapId(), true);

        uzivatel.setCasZmn(Utils.getCasZmn());
        uzivatel.setZmenuProvedl(Utils.getZmenuProv());

        List<Opravneni> listOpravneni = uzivatel.getOpravneni();
        List<Zavod> listZavod = uzivatel.getOstatniZavody();
        List<String> listModuly = uzivatel.getModuly();

        uzivatel = uzivatelRepository.save(uzivatel);

        // uložení rolí
        if (ukladatRole) {
            List<Opravneni> opravneniPuvodni = uzivatelOpravneniRepository.listOpravneni(uzivatel.getIdUzivatel(),
                    true);
            List<Opravneni> opravneniNove = new ArrayList<Opravneni>();

            if (listOpravneni != null && listOpravneni.size() > 0) {
                for (Opravneni nove : listOpravneni) {
                    boolean dohledano = false;
                    for (Opravneni puvodni : opravneniPuvodni) {
                        if (puvodni.getIdOpravneni().equals(nove.getIdOpravneni())) {
                            dohledano = true;
                            opravneniPuvodni.remove(puvodni);
                            break;
                        }
                    }
                    if (!dohledano)
                        opravneniNove.add(nove);
                }

            }

            // založení rolí
            if (opravneniNove != null && opravneniNove.size() > 0) {
                for (Opravneni nove : opravneniNove) {
                    uzivatelOpravneniRepository.save(new UzivatelOpravneni(uzivatel, nove));
                }
            }

            // odstranění rolí
            if (opravneniPuvodni != null && opravneniPuvodni.size() > 0) {
                for (Opravneni puvodni : opravneniPuvodni) {
                    uzivatelOpravneniRepository.delete(new UzivatelOpravneni(uzivatel, puvodni));
                }
            }
        }

        // uložení závodů
        if (ukladatZavody) {
            List<Zavod> zavodyPuvodni = uzivatelZavodRepository.listZavod(uzivatel.getIdUzivatel());
            List<Zavod> zavodyNove = new ArrayList<Zavod>();

            if (listZavod != null && listZavod.size() > 0) {
                for (Zavod nove : listZavod) {
                    boolean dohledano = false;
                    for (Zavod puvodni : zavodyPuvodni) {
                        if (puvodni.getIdZavod().equals(nove.getIdZavod())) {
                            dohledano = true;
                            zavodyPuvodni.remove(puvodni);
                            break;
                        }
                    }
                    if (!dohledano)
                        zavodyNove.add(nove);
                }

            }

            // založení závodů
            if (zavodyNove != null && zavodyNove.size() > 0) {
                for (Zavod nove : zavodyNove) {
                    uzivatelZavodRepository.save(new UzivatelZavod(uzivatel, nove));
                }
            }

            // odstranění rolí
            if (zavodyPuvodni != null && zavodyPuvodni.size() > 0) {
                for (Zavod puvodni : zavodyPuvodni) {
                    uzivatelZavodRepository.delete(new UzivatelZavod(uzivatel, puvodni));
                }
            }
        }

        // uložení modulu
        if (ukladatModuly) {
            List<String> modulyPuvodni = uzivatelModulRepository.listModul(uzivatel.getIdUzivatel());
            List<String> modulyNove = new ArrayList<String>();

            if (listModuly != null && listModuly.size() > 0) {
                for (String nove : listModuly) {
                    boolean dohledano = false;
                    for (String puvodni : modulyPuvodni) {
                        if (puvodni.equals(nove)) {
                            dohledano = true;
                            modulyPuvodni.remove(puvodni);
                            break;
                        }
                    }
                    if (!dohledano)
                        modulyNove.add(nove);
                }

            }

            // založení modulů
            if (modulyNove != null && modulyNove.size() > 0) {
                for (String nove : modulyNove) {
                    uzivatelModulRepository.save(new UzivatelModul(uzivatel, nove));
                }
            }

            // odstranění modulů
            if (modulyPuvodni != null && modulyPuvodni.size() > 0) {
                for (String puvodni : modulyPuvodni) {
                    uzivatelModulRepository.delete(new UzivatelModul(uzivatel, puvodni));
                }
            }
        }

        // ukončení
        if (uzivatel.getDatumDo() != null && !uzivatel.getDatumDo().equals(Utils.getMaxDate(true))
                && !uzivatel.getUkonceno()) {

            // příznak ukončení
            uzivatel.setUkonceno(true);
            uzivatel.setCasZmn(Utils.getCasZmn());
            uzivatel.setZmenuProvedl(Utils.getZmenuProv());
            uzivatel = uzivatelRepository.save(uzivatel);

        }

        return uzivatel;
    }

}
