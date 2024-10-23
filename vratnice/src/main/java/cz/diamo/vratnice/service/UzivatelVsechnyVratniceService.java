package cz.diamo.vratnice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.entity.UzivatelVsechnyVratnice;
import cz.diamo.vratnice.repository.UzivatelVsechnyVratniceRepository;

@Service
public class UzivatelVsechnyVratniceService {

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private UzivatelVsechnyVratniceRepository uzivatelVsechnyVratniceRepository;

    /**
     * Nastavuje uživateli přístup ke všem vrátnicím a ukládá tuto informaci do
     * databáze.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o aktuálním
     *                   uživateli.
     * @return Objekt {@link UzivatelVsechnyVratnice} s informací o aktivaci
     *         přístupu ke všem vrátnicím.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam o uživateli.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public UzivatelVsechnyVratnice nastavVsechnyVratnice(AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(appUserDto.getIdUzivatel(), false);
        UzivatelVsechnyVratnice savedUzivatelVsechnyVratnice = uzivatelVsechnyVratniceRepository
                .save(new UzivatelVsechnyVratnice(uzivatel, true));

        return savedUzivatelVsechnyVratnice;
    }

    /**
     * Kontroluje, zda má aktuální uživatel nastavený přístup ke všem vrátnicím.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o aktuálním
     *                   uživateli.
     * @return Boolean hodnota určující, zda je přístup ke všem vrátnicím aktivní
     *         {@code true} nebo ne {@code false}.
     */
    public Boolean jeNastavena(AppUserDto appUserDto) {
        UzivatelVsechnyVratnice uzivatelVsechnyVratnice = uzivatelVsechnyVratniceRepository
                .getDetail(appUserDto.getIdUzivatel());

        if (uzivatelVsechnyVratnice != null) {
            return uzivatelVsechnyVratnice.getAktivniVsechnyVratnice();
        }

        return false;
    }

}
