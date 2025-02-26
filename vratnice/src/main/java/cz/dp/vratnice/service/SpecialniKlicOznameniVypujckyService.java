package cz.dp.vratnice.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.dp.share.annotation.TransactionalRO;
import cz.dp.share.base.Utils;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.enums.TypOznameniEnum;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.share.services.UzivatelServices;
import cz.dp.vratnice.base.VratniceUtils;
import cz.dp.vratnice.entity.Klic;
import cz.dp.vratnice.entity.SpecialniKlicOznameniVypujcky;
import cz.dp.vratnice.enums.HistorieVypujcekAkceEnum;
import cz.dp.vratnice.filter.FilterPristupuVratnice;
import cz.dp.vratnice.repository.SpecialniKlicOznameniVypujckyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class SpecialniKlicOznameniVypujckyService {

    final static Logger logger = LogManager.getLogger(SpecialniKlicOznameniVypujckyService.class);

    @Autowired
    private SpecialniKlicOznameniVypujckyRepository specialniKlicOznameniVypujckyRepository;

    @Autowired
    private KlicService klicService;

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private VratniceBaseService vratniceBaseService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    /**
     * Vrací seznam speciálních klíčů oznámení výpůjček na základě aktivity a
     * uživatelského účtu.
     *
     * @param aktivita   Boolean hodnota určující, zda se mají vrátit pouze aktivní
     *                   záznamy.
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o uživateli.
     * @return Seznam {@link SpecialniKlicOznameniVypujcky} odpovídajících
     *         kritériím.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné záznamy.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<SpecialniKlicOznameniVypujcky> getList(Boolean aktivita, AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM SpecialniKlicOznameniVypujcky s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.klic.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<SpecialniKlicOznameniVypujcky> list = vysledek.getResultList();
        return list;
    }

    /**
     * Ukládá speciální klíč oznámení výpůjčky do databáze.
     *
     * @param specialniKlicOznameniVypujcky Objekt
     *                                      {@link SpecialniKlicOznameniVypujcky},
     *                                      který se má uložit.
     * @return Uložený objekt {@link SpecialniKlicOznameniVypujcky} s
     *         aktualizovanými informacemi.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException          Pokud dojde k chybě při validaci nebo ukládání
     *                                objektu.
     */
    @Transactional
    public SpecialniKlicOznameniVypujcky save(SpecialniKlicOznameniVypujcky specialniKlicOznameniVypujcky)
            throws NoSuchMessageException, BaseException {
        // Nový záznam - kontrola, zda již neexistuje nějaký záznam s daným klíčem
        if (specialniKlicOznameniVypujcky.getIdSpecialniKlicOznameniVypujcky() == null ||
                specialniKlicOznameniVypujcky.getIdSpecialniKlicOznameniVypujcky().isEmpty()) {
            if (specialniKlicOznameniVypujckyRepository
                    .existsByIdKlic(specialniKlicOznameniVypujcky.getKlic().getIdKlic())) {
                throw new UniqueValueException(
                        messageSource.getMessage("specialni_klic_oznameni_vypujcky.klic.unique", null,
                                LocaleContextHolder.getLocale()),
                        null, true);
            }
        }
        // Editace záznamu - kontrola, zda již neexistuje nějaký záznam s daným klíčem
        else {
            SpecialniKlicOznameniVypujcky alreadySavedOznameniVypujcky = specialniKlicOznameniVypujckyRepository
                    .getDetail(specialniKlicOznameniVypujcky.getIdSpecialniKlicOznameniVypujcky());
            if (!alreadySavedOznameniVypujcky.getKlic().getIdKlic()
                    .equals(specialniKlicOznameniVypujcky.getKlic().getIdKlic())) {
                if (specialniKlicOznameniVypujckyRepository
                        .existsByIdKlic(specialniKlicOznameniVypujcky.getKlic().getIdKlic())) {
                    throw new UniqueValueException(
                            messageSource.getMessage("specialni_klic_oznameni_vypujcky.klic.unique", null,
                                    LocaleContextHolder.getLocale()),
                            null, true);
                }
            }
        }

        specialniKlicOznameniVypujcky.setCasZmn(Utils.getCasZmn());
        specialniKlicOznameniVypujcky.setZmenuProvedl(Utils.getZmenuProv());
        return specialniKlicOznameniVypujckyRepository.save(specialniKlicOznameniVypujcky);
    }

    /**
     * Vrací detail speciálního klíče oznámení výpůjčky na základě jeho ID.
     *
     * @param idSpecialniKlicOznameniVypujcky ID speciálního klíče oznámení
     *                                        výpůjčky.
     * @return Objekt {@link SpecialniKlicOznameniVypujcky} s detaily.
     */
    public SpecialniKlicOznameniVypujcky getDetail(String idSpecialniKlicOznameniVypujcky) {
        return specialniKlicOznameniVypujckyRepository.getDetail(idSpecialniKlicOznameniVypujcky);
    }

    /**
     * Oznámí výpůjčku speciálního klíče a zašle oznámení uživatelům.
     *
     * @param idKlic     ID speciálního klíče, který se půjčuje.
     * @param idUzivatel ID uživatele, který klíč půjčuje.
     * @param akceEnum   Enum akce výpůjčky (např. půjčení nebo vrácení).
     * @param request    HTTP požadavek pro zaslání oznámení.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException          Pokud dojde k chybě při zpracování.
     */
    @TransactionalRO
    public void oznamitVypujcku(String idKlic, String idUzivatel, HistorieVypujcekAkceEnum akceEnum,
            HttpServletRequest request) throws NoSuchMessageException, BaseException {
        Klic klic = klicService.getDetail(idKlic);
        if (!klic.isSpecialni()) {
            return;
        }

        SpecialniKlicOznameniVypujcky oznameniVypujcky = specialniKlicOznameniVypujckyRepository.getByKlic(klic, true);
        if (oznameniVypujcky == null) {
            return;
        }

        Uzivatel uzivatelVypujcky = uzivatelServices.getDetail(idUzivatel);

        String predmet = messageSource.getMessage("avizace.specialni_klic_oznameni_vypujcky.predmet", null,
                LocaleContextHolder.getLocale());
        String oznameniText = vytvorObsahOznameni(klic, uzivatelVypujcky, akceEnum);
        String telo = String.format("Dobrý den, \n") + oznameniText;
        List<Uzivatel> prijemci = oznameniVypujcky.getUzivatele();

        vratniceBaseService.zaslatOznameniUzivateli(predmet, oznameniText, telo, null, prijemci,
                TypOznameniEnum.DULEZITE_INFO, request);
    }

    /**
     * Vytváří obsah oznámení o výpůjčce nebo vrácení speciálního klíče.
     *
     * @param klic             Objekt {@link Klic} představující speciální klíč.
     * @param uzivatelVypujcky Objekt {@link Uzivatel} představující uživatele,
     *                         který klíč půjčuje.
     * @param akceEnum         Enum akce výpůjčky (např. půjčení nebo vrácení).
     * @return Formátovaný text oznámení.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException          Pokud dojde k chybě při zpracování.
     */
    private String vytvorObsahOznameni(Klic klic, Uzivatel uzivatelVypujcky, HistorieVypujcekAkceEnum akceEnum)
            throws NoSuchMessageException, BaseException {
        String formattedNow = VratniceUtils.getCurrentFormattedDateTime();
        if (akceEnum == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VYPUJCEN) {
            return String.format(
                    "Nová výpůjčka speciálního klíče.\n" +
                            "Klíč: <strong>%s</strong> - %s (%s)\n" +
                            "Uživatel: <strong> %s </strong> (%s)\n" +
                            "Datum výpůjčky: <strong>%s</strong>",
                    klic.getNazev(),
                    klic.getLokalita().getNazev(),
                    klic.getBudova().getNazev(),
                    uzivatelVypujcky.getNazev(),
                    uzivatelVypujcky.getSapId(),
                    formattedNow);
        } else if (akceEnum == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN) {
            return String.format(
                    "Speciální klíč byl vrácen.\n" +
                            "Klíč: <strong>%s</strong> - %s (%s)\n" +
                            "Uživatel: <strong> %s </strong> (%s)\n" +
                            "Datum vrácení: <strong>%s</strong>",
                    klic.getNazev(),
                    klic.getLokalita().getNazev(),
                    klic.getBudova().getNazev(),
                    uzivatelVypujcky.getNazev(),
                    uzivatelVypujcky.getSapId(),
                    formattedNow);
        } else {
            throw new BaseException(messageSource.getMessage("specialni_klic_oznameni_vypujcky.obsah_oznameni_error",
                    null, LocaleContextHolder.getLocale()));
        }
    }
}
