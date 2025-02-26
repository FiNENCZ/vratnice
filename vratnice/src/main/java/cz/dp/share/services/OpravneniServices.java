package cz.dp.share.services;

import java.util.ArrayList;
import java.util.Collections;
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
import cz.dp.share.comparator.RoleComparator;
import cz.dp.share.component.ResourcesComponent;
import cz.dp.share.constants.Constants;
import cz.dp.share.dto.opravneni.FilterOpravneniDto;
import cz.dp.share.dto.opravneni.OpravneniPrehledDto;
import cz.dp.share.entity.Budova;
import cz.dp.share.entity.Opravneni;
import cz.dp.share.entity.OpravneniBudova;
import cz.dp.share.entity.OpravneniPracovniPozice;
import cz.dp.share.entity.OpravneniPrehled;
import cz.dp.share.entity.OpravneniRole;
import cz.dp.share.entity.OpravneniTypPristupu;
import cz.dp.share.entity.OpravneniTypPristupuBudova;
import cz.dp.share.entity.OpravneniZavod;
import cz.dp.share.entity.PracovniPozice;
import cz.dp.share.entity.PracovniPozicePrehled;
import cz.dp.share.entity.Role;
import cz.dp.share.entity.Zavod;
import cz.dp.share.enums.OpravneniTypPristupuBudovaEnum;
import cz.dp.share.enums.OpravneniTypPristupuEnum;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.share.repository.OpravneniBudovaRepository;
import cz.dp.share.repository.OpravneniPracovniPoziceRepository;
import cz.dp.share.repository.OpravneniRepository;
import cz.dp.share.repository.OpravneniRoleRepository;
import cz.dp.share.repository.OpravneniZavodRepository;
import cz.dp.share.repository.PracovniPozicePrehledRepository;
import cz.dp.share.repository.TmpOpravneniVseRepository;
import cz.dp.share.repository.TmpOpravneniVyberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class OpravneniServices {

    final static Logger logger = LogManager.getLogger(OpravneniServices.class);

    @Autowired
    private OpravneniRepository opravneniRepository;

    @Autowired
    private OpravneniRoleRepository opravneniRoleRepository;

    @Autowired
    private OpravneniPracovniPoziceRepository opravneniPracovniPoziceRepository;

    @Autowired
    private PracovniPozicePrehledRepository pracovniPozicePrehledRepository;

    @Autowired
    private TmpOpravneniVseRepository tmpOpravneniVseRepository;

    @Autowired
    private TmpOpravneniVyberRepository tmpOpravneniVyberRepository;

    @Autowired
    private OpravneniZavodRepository opravneniZavodRepository;

    @Autowired
    private OpravneniBudovaRepository opravneniBudovaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private MessageSource messageSource;

    public Opravneni getDetail(String idOpravneni) throws RecordNotFoundException {
        Opravneni opravneni = opravneniRepository.getDetail(idOpravneni, null);
        return getDetail(opravneni);
    }

    public Opravneni getDetailByKod(String kod, String idZavod, boolean preklady) throws RecordNotFoundException {
        Opravneni opravneni = opravneniRepository.getDetailByKod(kod.toUpperCase(), idZavod, null);
        return getDetail(opravneni);
    }

    private Opravneni getDetail(Opravneni opravneni) throws RecordNotFoundException {
        if (opravneni == null)
            throw new RecordNotFoundException(messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

        // načtení authorit
        opravneni.setRole(opravneniRoleRepository.listRole(opravneni.getIdOpravneni()));

        // načtení pracovních pozic
        opravneni.setPracovniPozice(pracovniPozicePrehledRepository.listPrehled(opravneni.getIdOpravneni(), null));

        // načtení závodu
        opravneni.setZavody(opravneniZavodRepository.listZavod(opravneni.getIdOpravneni()));

        // načtení budov
        opravneni.setBudovy(opravneniBudovaRepository.listBudova(opravneni.getIdOpravneni()));

        // doplnění překladů
        opravneni.getOpravneniTypPristupu()
                .setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), opravneni.getOpravneniTypPristupu().getNazevResx()));

        opravneni.getOpravneniTypPristupuBudova()
                .setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), opravneni.getOpravneniTypPristupuBudova().getNazevResx()));

        if (opravneni.getRole() != null && opravneni.getRole().size() > 0) {

            for (Role role : opravneni.getRole()) {
                role.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), role.getNazevResx()));
            }

            // seřazení
            RoleComparator comparator = new RoleComparator();
            Collections.sort(opravneni.getRole(), comparator);
        }

        return opravneni;
    }

    public boolean jePodrizeny(FilterOpravneniDto opravneni, String idPodrizeny) {
        // oprávnění bez omezení
        Integer count = tmpOpravneniVseRepository.exists(opravneni.getIdVedouci(), opravneni.getRoleString());
        if (count != null && count > 0)
            return true;
        // oprávnění dle prac. pozic
        count = tmpOpravneniVyberRepository.exists(opravneni.getIdVedouci(), idPodrizeny, opravneni.getRoleString());
        return count != null && count > 0;
    }

    public boolean pristupKBudove(FilterOpravneniDto opravneni, String idBudova) {
        // oprávnění bez omezení
        boolean pristup = opravneniRepository.existsOprBudovaVse(opravneni.getIdVedouci(), opravneni.getRoleString());
        if (pristup)
            return pristup;

        // oprávnění dle závodu
        pristup = opravneniRepository.existsOprBudovaZavod(opravneni.getIdVedouci(), idBudova, opravneni.getRoleString());
        if (pristup)
            return pristup;

        // oprávnění dle výběru důlního objektu
        return opravneniRepository.existsOprBudovaVyber(opravneni.getIdVedouci(), idBudova, opravneni.getRoleString());
    }

    public List<Opravneni> getList(String idZavod, Boolean aktivita, Boolean zavody, Boolean preklady) throws RecordNotFoundException {

        List<Opravneni> list = opravneniRepository.getList(idZavod, aktivita);

        // doplnění překladů
        if (list != null) {
            for (Opravneni opravneni : list) {
                if (preklady) {
                    opravneni.getOpravneniTypPristupu()
                            .setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), opravneni.getOpravneniTypPristupu().getNazevResx()));
                    opravneni.getOpravneniTypPristupuBudova().setNazev(
                            resourcesComponent.getResources(LocaleContextHolder.getLocale(), opravneni.getOpravneniTypPristupuBudova().getNazevResx()));
                }

                if (zavody)
                    opravneni.setZavody(opravneniZavodRepository.listZavod(opravneni.getIdOpravneni()));
            }
        }
        return list;
    }

    public List<OpravneniPrehledDto> getListPrehled(String idZavod, Boolean aktivita) throws RecordNotFoundException {

        // select
        StringBuilder queryString = new StringBuilder();
        queryString.append("select distinct");
        queryString.append(" concat(uzivatel.id_uzivatel, '#', zavod.id_zavod,'#', opravneni.id_opravneni) as id,");
        queryString.append(" opravneni.id_opravneni,");
        queryString.append(" opravneni.kod,");
        queryString.append(" opravneni.nazev,");
        queryString.append(" opravneni.aktivita,");
        queryString.append(" opravneni_typ_pristupu.nazev_resx as typ_pristupu,");
        queryString.append(" zavod.id_zavod as zavod_id,");
        queryString.append(" zavod.sap_id as zavod_sap_id,");
        queryString.append(" zavod.nazev as zavod_nazev,");
        queryString.append(" uzivatel.id_uzivatel as zamestnanec_id,");
        queryString.append(" uzivatel.sap_id as zamestnanec_sap_id,");
        queryString.append(" uzivatel.prijmeni as zamestnanec_prijmeni,");
        queryString.append(" uzivatel.jmeno as zamestnanec_jmeno,");
        queryString.append(" uzivatel.nazev as zamestnanec_nazev");
        queryString.append(" from");
        queryString.append(" " + Constants.SCHEMA + ".opravneni opravneni");
        queryString.append(" join " + Constants.SCHEMA
                + ".opravneni_typ_pristupu opravneni_typ_pristupu on (opravneni_typ_pristupu.id_opravneni_typ_pristupu = opravneni.id_opravneni_typ_pristupu)");
        queryString.append(" left join " + Constants.SCHEMA
                + ".opravneni_pracovni_pozice opravneni_pracovni_pozice on (opravneni_pracovni_pozice.id_opravneni = opravneni.id_opravneni)");
        queryString.append(
                " left join " + Constants.SCHEMA + ".uzivatel uzivatel on (uzivatel.id_pracovni_pozice = opravneni_pracovni_pozice.id_pracovni_pozice)");
        queryString.append(" left join " + Constants.SCHEMA + ".opravneni_zavod opravneni_zavod on (opravneni_zavod.id_opravneni = opravneni.id_opravneni)");
        queryString.append(" left join " + Constants.SCHEMA + ".zavod zavod on (zavod.id_zavod = opravneni_zavod.id_zavod)");
        queryString.append(" where 1=1");
        if (aktivita != null)
            queryString.append(" and opravneni.aktivita = :aktivita");
        if (!StringUtils.isBlank(idZavod))
            queryString.append(
                    " and opravneni.id_opravneni in (select v.id_opravneni from " + Constants.SCHEMA + ".opravneni_zavod v where v.id_zavod = :idZavod)");
        queryString.append(" order by");
        queryString.append(" opravneni.nazev ASC,");
        queryString.append(" opravneni.id_opravneni ASC,");
        queryString.append(" zavod.nazev ASC,");
        queryString.append(" uzivatel.prijmeni ASC,");
        queryString.append(" uzivatel.jmeno ASC,");
        queryString.append(" uzivatel.sap_id ASC");

        Query vysledek = entityManager.createNativeQuery(queryString.toString(), OpravneniPrehled.class);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        if (!StringUtils.isBlank(idZavod))
            vysledek.setParameter("idZavod", idZavod);

        @SuppressWarnings("unchecked")
        List<OpravneniPrehled> list = vysledek.getResultList();

        // složení výsledku
        List<OpravneniPrehledDto> result = new ArrayList<OpravneniPrehledDto>();
        if (list != null && list.size() > 0) {
            OpravneniPrehledDto role = null;
            List<String> pridaniZamestnanci = new ArrayList<String>();
            List<String> pridaneZavody = new ArrayList<String>();

            for (OpravneniPrehled opravneniPrehled : list) {
                // další role
                if (role == null || !role.getId().equals(opravneniPrehled.getIdOpravneni())) {
                    pridaniZamestnanci = new ArrayList<String>();
                    pridaneZavody = new ArrayList<String>();

                    // překlad
                    opravneniPrehled.setTypPristupu(resourcesComponent.getResources(LocaleContextHolder.getLocale(), opravneniPrehled.getTypPristupu()));

                    role = new OpravneniPrehledDto(opravneniPrehled);
                    result.add(role);

                    if (!StringUtils.isBlank(opravneniPrehled.getZamestnanecId()))
                        pridaniZamestnanci.add(opravneniPrehled.getZamestnanecId());
                    if (!StringUtils.isBlank(opravneniPrehled.getZavodId()))
                        pridaneZavody.add(opravneniPrehled.getZavodId());
                } else {
                    if (!StringUtils.isBlank(opravneniPrehled.getZamestnanecId()) && !pridaniZamestnanci.contains(opravneniPrehled.getZamestnanecId())) {
                        role.pridatZamestnance(opravneniPrehled);
                        pridaniZamestnanci.add(opravneniPrehled.getZamestnanecId());
                    }
                    if (!StringUtils.isBlank(opravneniPrehled.getZavodId()) && !pridaneZavody.contains(opravneniPrehled.getZavodId())) {
                        role.pridatZavod(opravneniPrehled);
                        pridaneZavody.add(opravneniPrehled.getZavodId());
                    }
                }
            }
        }

        return result;

    }

    @TransactionalWrite
    public Opravneni save(Opravneni opravneni) throws UniqueValueException, NoSuchMessageException {

        Integer exist = opravneniRepository.exists(opravneni.getKod(), Utils.toString(opravneni.getIdOpravneni()));
        if (exist > 0)
            throw new UniqueValueException(messageSource.getMessage("opravneni.kod.unique", null, LocaleContextHolder.getLocale()));

        opravneni.setCasZmn(Utils.getCasZmn());
        opravneni.setZmenuProvedl(Utils.getZmenuProv());
        List<Role> authority = opravneni.getRole();
        List<PracovniPozicePrehled> pracovniPozice = opravneni.getPracovniPozice();
        List<Budova> budovy = opravneni.getBudovy();
        List<Zavod> zavody = opravneni.getZavody();

        if (opravneni.getOpravneniTypPristupuBudova() == null)
            opravneni.setOpravneniTypPristupuBudova(new OpravneniTypPristupuBudova(OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU));

        if ((pracovniPozice == null || pracovniPozice.size() == 0)
                && opravneni.getOpravneniTypPristupu().getOpravneniTypPristupuEnum() == OpravneniTypPristupuEnum.TYP_PRIST_OPR_VYBER) {
            opravneni.setOpravneniTypPristupu(new OpravneniTypPristupu(OpravneniTypPristupuEnum.TYP_PRIST_OPR_BEZ_PRISTUPU));
        }

        if (pracovniPozice != null && pracovniPozice.size() > 0
                && opravneni.getOpravneniTypPristupu().getOpravneniTypPristupuEnum() != OpravneniTypPristupuEnum.TYP_PRIST_OPR_VYBER) {
            pracovniPozice = null;
        }

        if ((budovy == null || budovy.size() == 0)
                && opravneni.getOpravneniTypPristupuBudova().getOpravneniTypPristupuBudovaEnum() == OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_VSE) {
            opravneni.setOpravneniTypPristupuBudova(new OpravneniTypPristupuBudova(OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU));
        }

        if (budovy != null && budovy.size() > 0
                && opravneni.getOpravneniTypPristupuBudova().getOpravneniTypPristupuBudovaEnum() != OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_VYBER) {
            budovy = null;
        }

        opravneni = opravneniRepository.save(opravneni);

        // uložení authorit
        List<Role> authorityPuvodni = opravneniRoleRepository.listRole(opravneni.getIdOpravneni());
        List<Role> authorityNove = new ArrayList<Role>();

        if (authority != null && authority.size() > 0) {
            for (Role nove : authority) {
                boolean dohledano = false;
                for (Role puvodni : authorityPuvodni) {
                    if (puvodni.getAuthority().equals(nove.getAuthority())) {
                        dohledano = true;
                        authorityPuvodni.remove(puvodni);
                        break;
                    }
                }
                if (!dohledano)
                    authorityNove.add(nove);
            }

        }

        // založení rolí
        if (authorityNove != null && authorityNove.size() > 0) {
            for (Role nove : authorityNove) {
                opravneniRoleRepository.save(new OpravneniRole(opravneni, nove));
            }
        }

        // odstranění rolí
        if (authorityPuvodni != null && authorityPuvodni.size() > 0) {
            for (Role puvodni : authorityPuvodni) {
                opravneniRoleRepository.delete(new OpravneniRole(opravneni, puvodni));
            }
        }

        // uložení pracovních pozic
        List<PracovniPozice> pracPozicePuvodni = opravneniPracovniPoziceRepository.listPracovniPozice(opravneni.getIdOpravneni());
        List<PracovniPozice> pracPoziceNove = new ArrayList<PracovniPozice>();

        if (pracovniPozice != null && pracovniPozice.size() > 0) {
            for (PracovniPozicePrehled nove : pracovniPozice) {
                boolean dohledano = false;
                for (PracovniPozice puvodni : pracPozicePuvodni) {
                    if (puvodni.getIdPracovniPozice().equals(nove.getIdPracovniPozice())) {
                        dohledano = true;
                        pracPozicePuvodni.remove(puvodni);
                        break;
                    }
                }
                if (!dohledano)
                    pracPoziceNove.add(nove.getPracovniPozice());
            }

        }

        // založení rolí
        if (pracPoziceNove != null && pracPoziceNove.size() > 0) {
            for (PracovniPozice nove : pracPoziceNove) {
                opravneniPracovniPoziceRepository.save(new OpravneniPracovniPozice(opravneni, nove));
            }
        }

        // odstranění rolí
        if (pracPozicePuvodni != null && pracPozicePuvodni.size() > 0) {
            for (PracovniPozice puvodni : pracPozicePuvodni) {
                opravneniPracovniPoziceRepository.delete(new OpravneniPracovniPozice(opravneni, puvodni));
            }
        }

        // uložení budov
        List<Budova> budovyPuvodni = opravneniBudovaRepository.listBudova(opravneni.getIdOpravneni());
        List<Budova> budovyNove = new ArrayList<Budova>();

        if (budovy != null && budovy.size() > 0) {
            for (Budova nove : budovy) {
                boolean dohledano = false;
                for (Budova puvodni : budovyPuvodni) {
                    if (puvodni.getIdBudova().equals(nove.getIdBudova())) {
                        dohledano = true;
                        budovyPuvodni.remove(puvodni);
                        break;
                    }
                }
                if (!dohledano)
                    budovyNove.add(nove);
            }

        }

        // založení budov
        if (budovyNove != null && budovyNove.size() > 0) {
            for (Budova nove : budovyNove) {
                opravneniBudovaRepository.save(new OpravneniBudova(opravneni, nove));
            }
        }

        // odstranění budov
        if (budovyPuvodni != null && budovyPuvodni.size() > 0) {
            for (Budova puvodni : budovyPuvodni) {
                opravneniBudovaRepository.delete(new OpravneniBudova(opravneni, puvodni));
            }
        }

        // uložení závodů
        List<Zavod> zavodyPuvodni = opravneniZavodRepository.listZavod(opravneni.getIdOpravneni());
        List<Zavod> zavodyNove = new ArrayList<Zavod>();

        if (zavody != null && zavody.size() > 0) {
            for (Zavod nove : zavody) {
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
                opravneniZavodRepository.save(new OpravneniZavod(opravneni, nove));
            }
        }

        // odstranění závodů
        if (zavodyPuvodni != null && zavodyPuvodni.size() > 0) {
            for (Zavod puvodni : zavodyPuvodni) {
                opravneniZavodRepository.delete(new OpravneniZavod(opravneni, puvodni));
            }
        }

        return opravneni;
    }

}