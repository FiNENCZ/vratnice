package cz.dp.vratnice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.dp.share.component.ResourcesComponent;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.services.UzivatelServices;
import cz.dp.vratnice.entity.HistorieVypujcek;
import cz.dp.vratnice.entity.HistorieVypujcekAkce;
import cz.dp.vratnice.entity.ZadostKlic;
import cz.dp.vratnice.enums.HistorieVypujcekAkceEnum;
import cz.dp.vratnice.filter.FilterPristupuVratnice;
import cz.dp.vratnice.repository.HistorieVypujcekRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class HistorieVypujcekService {

    @Autowired
    private HistorieVypujcekRepository historieVypujcekRepository;

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private MessageSource messageSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SpecialniKlicOznameniVypujckyService specialniKlicOznameniVypujckyService;

    @Autowired
    private KlicService klicService;

    @Autowired
    private ResourcesComponent resourcesComponent;

    /**
     * Vytváří a ukládá záznam historie vypůjček na základě žádosti o klíč a
     * uživatelského DTO.
     *
     * @param zadostKlic Žádost o klíč, která se má uložit do historie.
     * @param appUserDto DTO uživatele, který provádí akci.
     * @param akce       Typ akce, která se provádí (např. vypůjčení).
     * @param request    HTTP požadavek
     * @return Uložený {@link HistorieVypujcek} objekt.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException          Pokud dojde k chybě při kontrole dostupnosti
     *                                klíče.
     */
    @Transactional
    public HistorieVypujcek create(ZadostKlic zadostKlic, AppUserDto appUserDto, HistorieVypujcekAkceEnum akce,
            HttpServletRequest request)
            throws NoSuchMessageException, BaseException {

        // Kontrola, zda je možné klíč vypůjčit
        Boolean jeKlicDostupny = klicService.jeDostupny(zadostKlic);
        zkontrolujDostupnostKlice(jeKlicDostupny, akce);

        HistorieVypujcek historieVypujcek = new HistorieVypujcek();
        Uzivatel vratny = uzivatelServices.getDetail(appUserDto.getIdUzivatel());

        historieVypujcek.setZadostKlic(zadostKlic);
        historieVypujcek.setAkce(new HistorieVypujcekAkce(akce));
        historieVypujcek.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        historieVypujcek.setVratny(vratny);

        specialniKlicOznameniVypujckyService.oznamitVypujcku(zadostKlic.getKlic().getIdKlic(),
                zadostKlic.getUzivatel().getIdUzivatel(), akce, request);

        return historieVypujcekRepository.save(historieVypujcek);
    }

    /**
     * Kontroluje dostupnost klíče.
     *
     * @param jeKlicDostupny Indikátor, zda je klíč dostupný (true) nebo nedostupný
     *                       (false).
     * @param akce           Typ akce, která se provádí (např. vypůjčení nebo
     *                       vrácení klíče).
     * @throws BaseException          Pokud je klíč v nesprávném stavu pro
     *                                prováděnou akci.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     */
    private void zkontrolujDostupnostKlice(Boolean jeKlicDostupny, HistorieVypujcekAkceEnum akce)
            throws BaseException, NoSuchMessageException {
        Locale locale = LocaleContextHolder.getLocale();

        if (jeKlicDostupny == null) {
            throw new BaseException(
                    messageSource.getMessage("historie_vypujcek.klic_nelze_vypujcit.null", null, locale));
        }

        if (jeKlicDostupny && akce == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN) { // Je dostupný, ale uživatel
                                                                                           // ho chce vrátit
            throw new BaseException(messageSource.getMessage("historie_vypujcek.klic_nelze_vratit", null, locale));
        }

        if (!jeKlicDostupny && akce == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VYPUJCEN) { // Je nedostupný, ale
                                                                                              // uživatel ho chce půjčit
            throw new BaseException(
                    messageSource.getMessage("historie_vypujcek.klic_nelze_vypujcit.false", null, locale));
        }
    }

    /**
     * Vrací historii výpůjček na základě RFID kódu a uživatelského DTO.
     *
     * @param rfid       RFID kód klíče, který se má vrátit.
     * @param appUserDto DTO uživatele, který provádí akci.
     * @param request    HTTP požadavek pro získání informací o uživateli.
     * @return Uložený {@link HistorieVypujcek} objekt, který reprezentuje vrácení
     *         klíče.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException           Pokud dojde k chybě při vracení klíče.
     * @throws RecordNotFoundException Pokud není nalezena poslední vypůjčka pro
     *                                 daný RFID kód nebo pokud klíč není ve stavu,
     *                                 který umožňuje vrácení.
     */
    @Transactional
    public HistorieVypujcek vratitKlicByRfid(String rfid, AppUserDto appUserDto, HttpServletRequest request)
            throws NoSuchMessageException, BaseException {

        HistorieVypujcek posledniVypujcka = historieVypujcekRepository.findLaskVypujckaByKodCipu(rfid);

        if (posledniVypujcka == null || posledniVypujcka.getAkce()
                .getHistorieVypujcekAkceEnum() != HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VYPUJCEN)
            throw new RecordNotFoundException(
                    String.format(messageSource.getMessage("historie_vypujcek.not_found_nelze_vratit", null,
                            LocaleContextHolder.getLocale())));

        return create(posledniVypujcka.getZadostKlic(), appUserDto, HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN,
                request);
    }

    /**
     * Vrací seznam historických výpůjček na základě zadaných klíčů a uživatelského
     * DTO.
     *
     * @param idKlic       Klíč žádosti, podle kterého se filtrují historické
     *                     výpůjčky.
     * @param idZadostKlic Klíč žádosti, podle kterého se filtrují historické
     *                     výpůjčky.
     * @param appUserDto   DTO uživatele, který provádí akci.
     * @return Seznam {@link HistorieVypujcek} objektů, které reprezentují
     *         historické výpůjčky.
     * @throws RecordNotFoundException Pokud není nalezena žádná historická výpůjčka
     *                                 odpovídající zadaným klíčům.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<HistorieVypujcek> getList(String idKlic, String idZadostKlic, AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM HistorieVypujcek s ");
        queryString.append("WHERE 1 = 1 ");

        if (idKlic != null)
            queryString.append("AND s.zadostKlic.klic.idKlic = :idKlic ");

        if (idZadostKlic != null)
            queryString.append("AND s.zadostKlic.idZadostKlic = :idZadostKlic ");

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.zadostKlic.klic.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idUzivatel);

        if (idKlic != null)
            vysledek.setParameter("idKlic", idKlic);

        if (idZadostKlic != null)
            vysledek.setParameter("idZadostKlic", idZadostKlic);

        @SuppressWarnings("unchecked")
        List<HistorieVypujcek> list = vysledek.getResultList();

        if (list != null) {
            for (HistorieVypujcek historieVypujcek : list) {
                historieVypujcek.getAkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        historieVypujcek.getAkce().getNazevResx()));
            }
        }

        return list;
    }

    /**
     * Vrací seznam historických výpůjček na základě zadaného klíče žádosti.
     *
     * @param zadostKlic Klíč žádosti, podle kterého se filtrují historické
     *                   výpůjčky.
     * @return Seznam {@link HistorieVypujcek} objektů, které reprezentují
     *         historické výpůjčky.
     * @throws RecordNotFoundException Pokud není nalezena žádná historická výpůjčka
     *                                 odpovídající zadanému klíči žádosti.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<HistorieVypujcek> findByZadostKlic(ZadostKlic zadostKlic)
            throws RecordNotFoundException, NoSuchMessageException {
        List<HistorieVypujcek> list = historieVypujcekRepository.findByZadostKlic(zadostKlic);

        if (list != null) {
            for (HistorieVypujcek historieVypujcek : list) {
                historieVypujcek.getAkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        historieVypujcek.getAkce().getNazevResx()));
            }
        }

        return list;
    }

    /**
     * Vrací seznam historických výpůjček, které nebyly vráceny, na základě
     * uživatelského DTO.
     *
     * @param appUserDto DTO uživatele, který provádí akci.
     * @return Seznam {@link HistorieVypujcek} objektů, které reprezentují
     *         historické výpůjčky
     *         odpovídající uživateli a které nebyly vráceny.
     * @throws RecordNotFoundException Pokud není nalezena žádná historická výpůjčka
     *                                 odpovídající zadanému uživatelskému DTO.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<HistorieVypujcek> listNevraceneKlice(AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM HistorieVypujcek s ");
        queryString.append("WHERE s.akce.idHistorieVypujcekAkce = 1 ");
        queryString.append("AND s.datum = (SELECT MAX(hv2.datum) ");
        queryString.append("FROM HistorieVypujcek hv2 ");
        queryString.append("WHERE hv2.zadostKlic.klic.idKlic = s.zadostKlic.klic.idKlic) ");

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.zadostKlic.klic.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idUzivatel);

        @SuppressWarnings("unchecked")
        List<HistorieVypujcek> list = vysledek.getResultList();

        if (list != null) {
            for (HistorieVypujcek historieVypujcek : list) {
                historieVypujcek.getAkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        historieVypujcek.getAkce().getNazevResx()));
            }
        }

        return list;
    }

}
