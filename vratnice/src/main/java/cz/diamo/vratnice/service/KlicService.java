package cz.diamo.vratnice.service;

import java.util.List;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.AccessDeniedException;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.HistorieVypujcekAkce;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.enums.HistorieVypujcekAkceEnum;
import cz.diamo.vratnice.enums.ZadostStavEnum;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.HistorieVypujcekRepository;
import cz.diamo.vratnice.repository.KlicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class KlicService {

    @Autowired
    private KlicRepository klicRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private HistorieVypujcekRepository historieVypujcekRepository;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @Autowired
    private UzivatelVsechnyVratniceService uzivatelVsechnyVratniceService;

    @Autowired
    private BudovaVratniceService budovaVratniceService;

    /**
     * Vytváří nový objekt {@link Klic} a ukládá ho do databáze.
     *
     * @param klic       Objekt Klic.
     * @param appUserDto DTO uživatele, který provádí akci.
     * @return Uložený objekt {@link Klic}.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException          Pokud dojde k chybě při vytváření klíče.
     * @throws AccessDeniedException  Pokud uživatel nemá přístup k budově spojené s
     *                                klíčem.
     */
    @Transactional
    public Klic createKey(Klic klic, AppUserDto appUserDto) throws NoSuchMessageException, BaseException {
        maUzivatelPristupKeKlici(klic, appUserDto);

        Boolean maUzivatelPristupKBudove = budovaVratniceService.maUzivatelPristupKBudove(klic.getBudova(), appUserDto);
        if (!maUzivatelPristupKBudove)
            throw new AccessDeniedException(
                    messageSource.getMessage("klic.save.no_access_budova", null, LocaleContextHolder.getLocale()));

        klic.setCasZmn(Utils.getCasZmn());
        klic.setZmenuProvedl(Utils.getZmenuProv());

        Klic savedKlic = klicRepository.save(klic);

        return translateKlic(savedKlic);
    }

    /**
     * Kontroluje, zda má uživatel přístup ke klíči na základě jeho nastavení a
     * přístupových práv.
     *
     * @param klic       Objekt Klic, ke kterému se kontroluje přístup.
     * @param appUserDto DTO uživatele, který provádí akci.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException          Pokud dojde k chybě při kontrole přístupu.
     * @throws AccessDeniedException  Pokud uživatel nemá přístup k vratnici spojené
     *                                s klíčem.
     */
    public void maUzivatelPristupKeKlici(Klic klic, AppUserDto appUserDto)
            throws NoSuchMessageException, BaseException {
        Boolean maVsechnyVratnice = uzivatelVsechnyVratniceService.jeNastavena(appUserDto);
        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        if (!maVsechnyVratnice && (nastavenaVratnice == null
                || !klic.getVratnice().getIdVratnice().equals(nastavenaVratnice.getIdVratnice()))) {
            throw new AccessDeniedException(
                    messageSource.getMessage("klic.save.no_access_vratnice", null, LocaleContextHolder.getLocale()));
        }
    }

    /**
     * Vrací seznam klíčů na základě zadaných filtrů.
     *
     * @param aktivita
     * @param specialni
     * @param appUserDto DTO uživatele, který provádí akci.
     * @return Seznam objektů {@link Klic} odpovídajících zadaným filtrům.
     * @throws RecordNotFoundException Pokud není nalezen žádný klíč odpovídající
     *                                 zadaným filtrům.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<Klic> getList(Boolean aktivita, Boolean specialni, AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT s FROM Klic s ")
                .append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        if (specialni != null)
            queryString.append("AND s.specialni = :specialni ");

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (specialni != null)
            vysledek.setParameter("specialni", specialni);

        @SuppressWarnings("unchecked")
        List<Klic> list = vysledek.getResultList();

        if (list != null) {
            for (Klic klic : list) {
                klic = translateKlic(klic);
            }
        }

        return list;

    }

    /**
     * Vrací seznam klíčů na základě zadaných filtrů pro lokalitu, budovu, poschodí,
     * aktivitu a speciálnost.
     *
     * @param idLokalita ID lokality, podle které se filtrují klíče.
     * @param idBudova   ID budovy, podle které se filtrují klíče.
     * @param idPoschodi ID poschodí, podle které se filtrují klíče.
     * @param aktivita
     * @param specialni
     * @return Seznam objektů {@link Klic} odpovídajících zadaným filtrům.
     * @throws RecordNotFoundException Pokud není nalezen žádný klíč odpovídající
     *                                 zadaným filtrům.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<Klic> getList(String idLokalita, String idBudova, String idPoschodi, Boolean aktivita,
            Boolean specialni) throws RecordNotFoundException, NoSuchMessageException {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Klic s");
        queryString.append(" left join fetch s.lokalita lok");
        queryString.append(" left join fetch s.budova bud");
        queryString.append(" left join fetch s.poschodi pos");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (specialni != null)
            queryString.append(" and s.specialni = :specialni");

        if (StringUtils.isNotBlank(idLokalita))
            queryString.append(" and lok.idLokalita = :idLokalita");

        if (StringUtils.isNotBlank(idBudova))
            queryString.append(" and bud.idBudova = :idBudova");

        if (StringUtils.isNotBlank(idPoschodi))
            queryString.append(" and pos.idPoschodi = :idPoschodi");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (specialni != null)
            vysledek.setParameter("specialni", specialni);

        if (StringUtils.isNotBlank(idLokalita))
            vysledek.setParameter("idLokalita", idLokalita);

        if (StringUtils.isNotBlank(idBudova))
            vysledek.setParameter("idBudova", idBudova);

        if (StringUtils.isNotBlank(idPoschodi))
            vysledek.setParameter("idPoschodi", idPoschodi);

        @SuppressWarnings("unchecked")
        List<Klic> list = vysledek.getResultList();

        if (list != null) {
            for (Klic klic : list) {
                klic = translateKlic(klic);
            }
        }

        return list;

    }

    /**
     * Překládá typ klíče {@link Klic}
     *
     * @param klic Objekt {@link Klic}.
     * @return Přeložený objekt {@link Klic}.
     * @throws RecordNotFoundException Pokud dojde k chybě při hledání záznamu.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    private Klic translateKlic(Klic klic) throws RecordNotFoundException, NoSuchMessageException {
        if (klic.getTyp().getNazevResx() != null)
            klic.getTyp().setNazev(
                    resourcesComponent.getResources(LocaleContextHolder.getLocale(), klic.getTyp().getNazevResx()));
        return klic;
    }

    /**
     * Vrací detailní informace o klíči {@link Klic} na základě jeho ID.
     *
     * @param idKlic ID klíče, jehož detaily se mají vrátit.
     * @return Objekt {@link Klic} odpovídající zadanému ID.
     * @throws RecordNotFoundException Pokud není nalezen klíč odpovídající zadanému
     *                                 ID.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public Klic getDetail(String idKlic) throws RecordNotFoundException, NoSuchMessageException {
        Klic klic = klicRepository.getDetail(idKlic);

        if (klic != null) {
            klic = translateKlic(klic);
        }

        return klic;
    }

    /**
     * Kontroluje dostupnost klíče na základě žádosti a vrací stav dostupnosti.
     *
     * @param zadost Objekt {@link ZadostKlic}, který obsahuje informace o
     *               žádosti.
     * @return false - klíč je možné vrátit (poslední akce byla vypůjčení),
     *         true - klíč je možné vypůjčit,
     *         null - klíč není možné vypůjčit ani vrátit (neaktivita, platnost
     *         vypršela, atd.).
     */
    public Boolean jeDostupny(ZadostKlic zadost) {
        /*
         * Return STATEMENTS
         * false - je možné vrátit klíč (poslední akce byla vypůjčení)
         * true - je možné klíč vypůjčit
         * null - není možné klíč vypůjčit, ani vrátit (neaktivita, platnost vypršela,
         * atd...)
         */

        HistorieVypujcekAkce vypujckaAkce = historieVypujcekRepository
                .findLastAkceByIdKlic(zadost.getKlic().getIdKlic());

        // Pokud byl klíč vypůjčen je ho možné vrátit
        if (vypujckaAkce != null) {
            if (vypujckaAkce.getHistorieVypujcekAkceEnum() == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VYPUJCEN) {
                return false;
            }
        }

        // Zkontroluj, zda je žádost schválená
        if (zadost.getZadostStav().getZadostStavEnum() != ZadostStavEnum.SCHVALENO) {
            return null;
        }

        // Zkontroluj, zda žádost a klíč jsou aktivní
        if (!zadost.getAktivita() || !zadost.getKlic().getAktivita()) {
            return null;
        }

        // Pokud není žádost trvalá, zkontroluj platnost výpůjčky
        if (!zadost.getTrvala()) {
            Date currentDate = new Date();
            if (zadost.getDatumOd() != null && zadost.getDatumDo() != null &&
                    (currentDate.before(zadost.getDatumOd()) || currentDate.after(zadost.getDatumDo()))) {
                return null;
            }
        }

        // Zkontroluj, zda byl klíč vrácen nebo je stále vypůjčen
        if (vypujckaAkce == null
                || vypujckaAkce.getHistorieVypujcekAkceEnum() == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN) {
            return true;
        }

        return null;
    }

}
