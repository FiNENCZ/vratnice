package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.entity.UzivatelVratnice;
import cz.diamo.vratnice.entity.UzivatelVsechnyVratnice;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.repository.UzivatelVratniceRepository;
import cz.diamo.vratnice.repository.UzivatelVsechnyVratniceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class UzivatelVratniceService {

    @Autowired
    private UzivatelVratniceRepository uzivatelVratniceRepository;

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UzivatelVsechnyVratniceRepository uzivatelVsechnyVratniceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Vrací seznam uživatelů vratnic na základě aktivity a zda jsou určeni pro
     * konkrétního uživatele.
     *
     * @param aktivita     Boolean hodnota.
     * @param proUzivatele Boolean hodnota.
     * @param appUserDto   Objekt {@link AppUserDto} obsahující informace o
     *                     aktuálním uživateli.
     * @return Seznam {@link UzivatelVratnice} odpovídajících kritériím.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné záznamy.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<UzivatelVratnice> getList(Boolean aktivita, Boolean proUzivatele, AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(appUserDto.getIdUzivatel(), false);

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from UzivatelVratnice s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (proUzivatele != null)
            if (proUzivatele)
                queryString.append(" and s.uzivatel = :uzivatel");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (proUzivatele != null)
            if (proUzivatele)
                vysledek.setParameter("uzivatel", uzivatel);

        @SuppressWarnings("unchecked")
        List<UzivatelVratnice> list = vysledek.getResultList();
        return list;
    }

    /**
     * Ukládá uživatele vratnice do databáze. Kontroluje, zda již existuje záznam s
     * daným uživatelem
     * a nastavuje výchozí vrátnici, pokud není nastavena.
     *
     * @param uzivatelVratnice Objekt {@link UzivatelVratnice}, který se má uložit.
     * @return Uložený objekt {@link UzivatelVratnice} s aktualizovanými
     *         informacemi.
     * @throws UniqueValueException   Pokud již existuje záznam s daným uživatelem.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     */
    @Transactional
    public UzivatelVratnice save(UzivatelVratnice uzivatelVratnice)
            throws UniqueValueException, NoSuchMessageException {
        // Nový záznam - kontrola, zda již neexistuje nějaký záznam s daným uživatelem
        if (uzivatelVratnice.getIdUzivatelVratnice() == null || uzivatelVratnice.getIdUzivatelVratnice().isEmpty()) {
            if (uzivatelVratniceRepository.existsByIdUzivatel(uzivatelVratnice.getUzivatel().getIdUzivatel())) {
                throw new UniqueValueException(
                        messageSource.getMessage("uzivatel_vratnice.uzivatel.unique", null,
                                LocaleContextHolder.getLocale()),
                        null, true);
            }
        }
        // Editace záznamu - kontrola, zda již neexistuje nějaký záznam s daným
        // uživatelem
        else {
            UzivatelVratnice alreadySavedUzivatelVratnice = uzivatelVratniceRepository
                    .getDetail(uzivatelVratnice.getIdUzivatelVratnice());
            if (!alreadySavedUzivatelVratnice.getUzivatel().getIdUzivatel()
                    .equals(uzivatelVratnice.getUzivatel().getIdUzivatel())) {
                if (uzivatelVratniceRepository.existsByIdUzivatel(uzivatelVratnice.getUzivatel().getIdUzivatel())) {
                    throw new UniqueValueException(
                            messageSource.getMessage("uzivatel_vratnice.uzivatel.unique", null,
                                    LocaleContextHolder.getLocale()),
                            null, true);
                }
            }
        }

        // pokud není nastavená výchozí vrátnice pro daného uživatele, vybere se ta
        // první
        if (uzivatelVratnice.getNastavenaVratnice() == null) {
            if (uzivatelVratnice.getVratnice() != null && !uzivatelVratnice.getVratnice().isEmpty()) {
                uzivatelVratnice.setNastavenaVratnice(uzivatelVratnice.getVratnice().get(0));
            }
        } else { // pokud je nastavena výchozí, tak zkontrolovat, jestli vůbec odpovídá nějaké
                 // přidělené
            boolean shodaNastaveneVratnice = false;
            for (Vratnice vratnice : uzivatelVratnice.getVratnice()) {
                if (vratnice.getIdVratnice().equals(uzivatelVratnice.getNastavenaVratnice().getIdVratnice()))
                    shodaNastaveneVratnice = true;

            }
            // pokud neodpovídá, tak se vybere první přidělená
            if (!shodaNastaveneVratnice)
                uzivatelVratnice.setNastavenaVratnice(uzivatelVratnice.getVratnice().get(0));
        }

        // Pokud má nastavený přístup do všech vrátnic, tak při vytvoření napojení
        // vrátnic (UzivatelVratnice) nastaví v dropdownu "nastavenou vrátnici"
        UzivatelVsechnyVratnice uzivatelVsechnyVratnice = uzivatelVsechnyVratniceRepository
                .getDetail(uzivatelVratnice.getUzivatel().getIdUzivatel());
        if (uzivatelVsechnyVratnice != null) {
            uzivatelVsechnyVratnice.setAktivniVsechnyVratnice(false);
            uzivatelVsechnyVratniceRepository.save(uzivatelVsechnyVratnice);
        }

        uzivatelVratnice.setCasZmn(Utils.getCasZmn());
        uzivatelVratnice.setZmenuProvedl(Utils.getZmenuProv());
        return uzivatelVratniceRepository.save(uzivatelVratnice);
    }

    /**
     * Vrací detail uživatele vratnice na základě jeho ID.
     *
     * @param idUzivatelVratnice ID uživatele vratnice, jehož detail se má vrátit.
     * @return Objekt {@link UzivatelVratnice} s detaily.
     */
    public UzivatelVratnice getDetail(String idUzivatelVratnice) {
        return uzivatelVratniceRepository.getDetail(idUzivatelVratnice);
    }

    /**
     * Vrací uživatele vratnice na základě informací o aktuálním uživateli.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o aktuálním
     *                   uživateli.
     * @return Objekt {@link UzivatelVratnice} odpovídající aktuálnímu uživateli.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam o uživateli
     *                                 vratnice.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public UzivatelVratnice getByUzivatel(AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(appUserDto.getIdUzivatel(), false);
        return uzivatelVratniceRepository.getByUzivatel(uzivatel);
    }

    /**
     * Vrací nastavenou vrátnici pro aktuálního uživatele.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o aktuálním
     *                   uživateli.
     * @return Objekt {@link Vratnice} představující nastavenou vrátnici, nebo null,
     *         pokud nebyla nalezena.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam o uživateli
     *                                 vratnice.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public Vratnice getNastavenaVratniceByUzivatel(AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        UzivatelVratnice uzivatelVratnice = getByUzivatel(appUserDto);

        if (uzivatelVratnice == null) {
            return null;
        }

        Vratnice vratnice = uzivatelVratnice.getNastavenaVratnice();
        return vratnice;
    }

    /**
     * Kontroluje, zda je nastavená vrátnice pro aktuálního uživatele vjezdová.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o aktuálním
     *                   uživateli.
     * @return Boolean hodnota určující, zda je vrátnice vjezdová {@code true} nebo
     *         ne {@code false}.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam o uživateli
     *                                 vratnice.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public Boolean jeVjezdova(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        try {
            UzivatelVratnice uzivatelVratnice = getByUzivatel(appUserDto);
            return uzivatelVratnice.getNastavenaVratnice().getVjezdova();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kontroluje, zda je nastavená vrátnice pro aktuálního uživatele osobní.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o aktuálním
     *                   uživateli.
     * @return Boolean hodnota určující, zda je vrátnice osobní {@code true} nebo ne
     *         {@code false}.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam o
     *                                 uživateli vratnice.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public Boolean jeOsobni(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        try {
            UzivatelVratnice uzivatelVratnice = getByUzivatel(appUserDto);
            return uzivatelVratnice.getNastavenaVratnice().getOsobni();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kontroluje, zda je nastavená vrátnice pro aktuálního uživatele návštěvní.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o aktuálním
     *                   uživateli.
     * @return Boolean hodnota určující, zda je vrátnice návštěvní {@code true} nebo
     *         ne {@code false}.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam o uživateli
     *                                 vratnice.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public Boolean jeNavstevni(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        try {
            UzivatelVratnice uzivatelVratnice = getByUzivatel(appUserDto);
            return uzivatelVratnice.getNastavenaVratnice().getNavstevni();
        } catch (Exception e) {
            return false;
        }
    }

}
