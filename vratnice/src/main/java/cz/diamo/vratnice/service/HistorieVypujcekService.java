package cz.diamo.vratnice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.HistorieVypujcekRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class HistorieVypujcekService {

    final static Logger logger = LogManager.getLogger(HistorieVypujcekService.class);
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

    @Transactional
    public HistorieVypujcek create(ZadostKlic zadostKlic, AppUserDto appUserDto, HistorieVypujcekAkceEnum akce, HttpServletRequest request)
                     throws NoSuchMessageException, BaseException {
        
        // Kontrola, zda je možné klíč vypůjčit
        Boolean jeKlicDostupny = klicService.jeDostupny(zadostKlic);
        zkontrolujDostupnostKlice(jeKlicDostupny, akce);

        if (jeKlicDostupny == null) 
            throw new BaseException(String.format(messageSource.getMessage("historie_vypujcek.klic_nelze_vypujcit.null", 
            null,LocaleContextHolder.getLocale())));

        if (jeKlicDostupny && akce == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN) //Je dostupný, ale uživatel ho chce vrátit
            throw new BaseException(String.format(messageSource.getMessage("historie_vypujcek.klic_nelze_vratit", 
                null,LocaleContextHolder.getLocale())));

        if (!jeKlicDostupny && akce == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VYPUJCEN) //Je nedostupný, ale uživatel ho chce půjčit
            throw new BaseException(String.format(messageSource.getMessage("historie_vypujcek.klic_nelze_vypujcit.false", 
            null,LocaleContextHolder.getLocale())));


        HistorieVypujcek historieVypujcek = new HistorieVypujcek();
        Uzivatel vratny = uzivatelServices.getDetail(appUserDto.getIdUzivatel());

        historieVypujcek.setZadostKlic(zadostKlic);
        historieVypujcek.setAkce(new HistorieVypujcekAkce(akce));
        historieVypujcek.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        historieVypujcek.setVratny(vratny);

        specialniKlicOznameniVypujckyService.oznamitVypujcku(zadostKlic.getKlic().getIdKlic(), akce, request);

        return historieVypujcekRepository.save(historieVypujcek);
    }

    private void zkontrolujDostupnostKlice(Boolean jeKlicDostupny, HistorieVypujcekAkceEnum akce) throws BaseException, NoSuchMessageException {
        Locale locale = LocaleContextHolder.getLocale();
        
        if (jeKlicDostupny == null) {
            throw new BaseException(messageSource.getMessage("historie_vypujcek.klic_nelze_vypujcit.null", null, locale));
        }
    
        if (jeKlicDostupny && akce == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN) {
            throw new BaseException(messageSource.getMessage("historie_vypujcek.klic_nelze_vratit", null, locale));
        }
    
        if (!jeKlicDostupny && akce == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VYPUJCEN) {
            throw new BaseException(messageSource.getMessage("historie_vypujcek.klic_nelze_vypujcit.false", null, locale));
        }
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

    public List<HistorieVypujcek> listNevraceneKlice(AppUserDto appUserDto) {
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
        return list;
    }

}
