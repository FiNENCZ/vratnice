package cz.diamo.vratnice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.entity.HistorieVypujcek;
import cz.diamo.vratnice.entity.HistorieVypujcekAkce;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.enums.HistorieVypujcekAkceEnum;
import cz.diamo.vratnice.repository.HistorieVypujcekRepository;
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
    private ResourcesComponent resourcesComponent;

    @Transactional
    public HistorieVypujcek create(ZadostKlic zadostKlic, AppUserDto appUserDto, HistorieVypujcekAkceEnum akce, HttpServletRequest request)
                     throws NoSuchMessageException, BaseException {
        HistorieVypujcek historieVypujcek = new HistorieVypujcek();
        Uzivatel vratny = uzivatelServices.getDetail(appUserDto.getIdUzivatel());

        historieVypujcek.setZadostKlic(zadostKlic);
        historieVypujcek.setAkce(new HistorieVypujcekAkce(akce));
        historieVypujcek.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        historieVypujcek.setVratny(vratny);

        specialniKlicOznameniVypujckyService.oznamitVypujcku(zadostKlic.getKlic().getIdKlic(), akce, request);

        return historieVypujcekRepository.save(historieVypujcek);
    }

    @Transactional
    public HistorieVypujcek vratitKlicByRfid(String rfid, AppUserDto appUserDto, HttpServletRequest request) 
                        throws NoSuchMessageException, BaseException {

        HistorieVypujcek posledniVypujcka = historieVypujcekRepository.findLaskVypujckaByKodCipu(rfid);

        if (posledniVypujcka == null || posledniVypujcka.getAkce().getHistorieVypujcekAkceEnum() != HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VYPUJCEN)
            throw new RecordNotFoundException(
                String.format(messageSource.getMessage("historie_vypujcek.not_found_nelze_vratit", null, LocaleContextHolder.getLocale())));

        return create(posledniVypujcka.getZadostKlic(), appUserDto, HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN, request);
    }

    public List<HistorieVypujcek> getList(String idKlic, String idZadostKlic, AppUserDto appUserDto) {
                StringBuilder queryString = new StringBuilder();

        queryString.append("select s from HistorieVypujcek s");
        queryString.append(" where 1 = 1");

        if (idKlic != null)
            queryString.append(" and s.zadostKlic.klic.idKlic = :idKlic");

        if (idZadostKlic != null)
            queryString.append(" and s.zadostKlic.idZadostKlic = :idZadostKlic");

        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (idKlic != null)
            vysledek.setParameter("idKlic", idKlic);
        
        if (idZadostKlic != null)
            vysledek.setParameter("idZadostKlic", idZadostKlic);

        
        @SuppressWarnings("unchecked")
        List<HistorieVypujcek> list = vysledek.getResultList();
        return list;
    }

    public List<HistorieVypujcek> findByZadostKlic(ZadostKlic zadostKlic) {
        return historieVypujcekRepository.findByZadostKlic(zadostKlic);
    }

    public HistorieVypujcekAkce getHistorieVypujcekAkce(String idHistorieVypujcek) {
        HistorieVypujcek historieVypujcek = historieVypujcekRepository.getDetail(idHistorieVypujcek);
        try {
            if (historieVypujcek == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            historieVypujcek.getAkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), historieVypujcek.getAkce().getNazevResx()));
            return historieVypujcek.getAkce();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

}
