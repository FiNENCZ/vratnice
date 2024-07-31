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

    public UzivatelVsechnyVratnice nastavVsechnyVratnice(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(appUserDto.getIdUzivatel(), false);
        UzivatelVsechnyVratnice savedUzivatelVsechnyVratnice = uzivatelVsechnyVratniceRepository.save(new UzivatelVsechnyVratnice(uzivatel, true));

        return savedUzivatelVsechnyVratnice;
    }

    public Boolean jeNastavena(AppUserDto appUserDto) {
        UzivatelVsechnyVratnice uzivatelVsechnyVratnice = uzivatelVsechnyVratniceRepository.getDetail(appUserDto.getIdUzivatel());

        if (uzivatelVsechnyVratnice != null) {
            return uzivatelVsechnyVratnice.getAktivniVsechnyVratnice();
        }

        return false;
    }


}
