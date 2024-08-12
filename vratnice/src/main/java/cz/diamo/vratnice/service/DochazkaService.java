package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.entity.Vratnice;

@Service
public class DochazkaService {

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    public List<Uzivatel> getUzivateleDleNastaveneVratnice(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);
        return uzivatelServices.getList(nastavenaVratnice.getZavod().getIdZavod(), null, true);
    }

}
