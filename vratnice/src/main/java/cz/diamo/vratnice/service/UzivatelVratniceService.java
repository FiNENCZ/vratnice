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
import cz.diamo.vratnice.enums.VozidloTypEnum;
import cz.diamo.vratnice.repository.UzivatelVratniceRepository;
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


    @PersistenceContext
    private EntityManager entityManager;

    public List<UzivatelVratnice> getList(Boolean aktivita, Boolean proUzivatele, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
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

    @Transactional
    public UzivatelVratnice save(UzivatelVratnice uzivatelVratnice) throws UniqueValueException, NoSuchMessageException{
        if (uzivatelVratnice.getIdUzivatelVratnice() == null || uzivatelVratnice.getIdUzivatelVratnice().isEmpty()){
            if(uzivatelVratniceRepository.existsByIdUzivatel(uzivatelVratnice.getUzivatel().getIdUzivatel())){
                throw new UniqueValueException(
                        messageSource.getMessage("uzivatel_vratnice.uzivatel.unique", null, LocaleContextHolder.getLocale()), null, true);
            }
        }

        if (uzivatelVratnice.getNastavenaVratnice() == null) {
            if (uzivatelVratnice.getVratnice() != null && !uzivatelVratnice.getVratnice().isEmpty()) {
                uzivatelVratnice.setNastavenaVratnice(uzivatelVratnice.getVratnice().get(0));
            }
        }

        uzivatelVratnice.setCasZmn(Utils.getCasZmn());
        uzivatelVratnice.setZmenuProvedl(Utils.getZmenuProv());
        return uzivatelVratniceRepository.save(uzivatelVratnice);
    }

    public UzivatelVratnice getDetail(String idUzivatelVratnice) {
        return uzivatelVratniceRepository.getDetail(idUzivatelVratnice);
    }

    public UzivatelVratnice getByUzivatel(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(appUserDto.getIdUzivatel(), false);
        return uzivatelVratniceRepository.getByUzivatel(uzivatel);
    }

    public Boolean jeVjezdova(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        try {
            UzivatelVratnice uzivatelVratnice = getByUzivatel(appUserDto);
            return uzivatelVratnice.getNastavenaVratnice().getVjezdova();
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean jeOsobni(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        try {
            UzivatelVratnice uzivatelVratnice = getByUzivatel(appUserDto);
            return uzivatelVratnice.getNastavenaVratnice().getOsobni();
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean jeNavstevni(AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        try {
            UzivatelVratnice uzivatelVratnice = getByUzivatel(appUserDto);
            return uzivatelVratnice.getNastavenaVratnice().getNavstevni();
        } catch (Exception e) {
            return false;
        }
    }

    

}
