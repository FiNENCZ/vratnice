package cz.diamo.vratnice.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalRO;
import cz.diamo.share.base.Utils;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.avizace.AvizaceEmailRequestDto;
import cz.diamo.share.dto.avizace.AvizaceOznameniRequestDto;
import cz.diamo.share.dto.avizace.AvizacePrijemceRequestDto;
import cz.diamo.share.dto.avizace.AvizaceRequestDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.enums.TypOznameniEnum;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.share.services.OznameniServices;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.base.VratniceUtils;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.SpecialniKlicOznameniVypujcky;
import cz.diamo.vratnice.enums.HistorieVypujcekAkceEnum;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.SpecialniKlicOznameniVypujckyRepository;
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
    private OznameniServices oznameniServices;

    @Autowired
    private KlicService klicService;

    @Autowired
    private UzivatelServices uzivatelServices;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;


     public List<SpecialniKlicOznameniVypujcky> getList(Boolean aktivita, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
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

    @Transactional
    public SpecialniKlicOznameniVypujcky save(SpecialniKlicOznameniVypujcky specialniKlicOznameniVypujcky) throws NoSuchMessageException, BaseException {
        // Nový záznam - kontrola, zda již neexistuje nějaký záznam s daným klíčem
        if (specialniKlicOznameniVypujcky.getIdSpecialniKlicOznameniVypujcky() == null || 
                specialniKlicOznameniVypujcky.getIdSpecialniKlicOznameniVypujcky().isEmpty()) {
            if (specialniKlicOznameniVypujckyRepository.existsByIdKlic(specialniKlicOznameniVypujcky.getKlic().getIdKlic())) {
                throw new UniqueValueException(
                        messageSource.getMessage("specialni_klic_oznameni_vypujcky.klic.unique", null, LocaleContextHolder.getLocale()), null, true);
            }
        } 
        // Editace záznamu - kontrola, zda již neexistuje nějaký záznam s daným klíčem
        else { 
            SpecialniKlicOznameniVypujcky alreadySavedOznameniVypujcky = specialniKlicOznameniVypujckyRepository.getDetail(specialniKlicOznameniVypujcky.getIdSpecialniKlicOznameniVypujcky());
            if (!alreadySavedOznameniVypujcky.getKlic().getIdKlic().equals(specialniKlicOznameniVypujcky.getKlic().getIdKlic())) {
                logger.info("klíče jsou rozdílné");
                if (specialniKlicOznameniVypujckyRepository.existsByIdKlic(specialniKlicOznameniVypujcky.getKlic().getIdKlic())) {
                    throw new UniqueValueException(
                        messageSource.getMessage("specialni_klic_oznameni_vypujcky.klic.unique", null, LocaleContextHolder.getLocale()), null, true);
                }
            }
        }

        specialniKlicOznameniVypujcky.setCasZmn(Utils.getCasZmn());
        specialniKlicOznameniVypujcky.setZmenuProvedl(Utils.getZmenuProv());
        return specialniKlicOznameniVypujckyRepository.save(specialniKlicOznameniVypujcky);
    }

    public SpecialniKlicOznameniVypujcky getDetail(String idSpecialniKlicOznameniVypujcky) {
        return specialniKlicOznameniVypujckyRepository.getDetail(idSpecialniKlicOznameniVypujcky);
    }

    @TransactionalRO
    public void oznamitVypujcku(String idKlic, String idUzivatel, HistorieVypujcekAkceEnum akceEnum, HttpServletRequest request) throws NoSuchMessageException, BaseException {
        Klic klic = klicService.getDetail(idKlic);
        if (!klic.isSpecialni()) {
            return;
        }

        SpecialniKlicOznameniVypujcky oznameniVypujcky = specialniKlicOznameniVypujckyRepository.getByKlic(klic);
        if (oznameniVypujcky == null) {
            return;
        }

        Uzivatel uzivatelVypujcky = uzivatelServices.getDetail(idUzivatel);

        AvizaceRequestDto avizaceRequestDto = new AvizaceRequestDto();
        String predmet = messageSource.getMessage("avizace.specialni_klic_oznameni_vypujcky.predmet", null, LocaleContextHolder.getLocale());
        String oznameniText = vytvorObsahOznameni(klic, uzivatelVypujcky, akceEnum);
        String telo = String.format("Dobrý den, \n") + oznameniText;

        avizaceRequestDto.setEmail(new AvizaceEmailRequestDto(predmet, telo, null));
        avizaceRequestDto.setOznameni(new AvizaceOznameniRequestDto(TypOznameniEnum.DULEZITE_INFO, predmet, oznameniText, null));

        //Přidat příjemce
        for (Uzivatel uzivatelOznameni: oznameniVypujcky.getUzivatele()) {
            String email = uzivatelOznameni.getEmail();
            String sapId = uzivatelOznameni.getSapId();
            AvizacePrijemceRequestDto prijemnce = new AvizacePrijemceRequestDto();
            
            if (sapId != null) 
                prijemnce.setSapId(sapId);

            if (email != null)
                prijemnce.setEmail(email);
   
            avizaceRequestDto.pridatPrijemce(prijemnce);
        }

        oznameniServices.save(avizaceRequestDto, request);
    }

    private String vytvorObsahOznameni(Klic klic, Uzivatel uzivatelVypujcky, HistorieVypujcekAkceEnum akceEnum) throws NoSuchMessageException, BaseException {
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
                formattedNow
            );
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
                formattedNow
            );
        } else {   
            throw new BaseException(messageSource.getMessage("specialni_klic_oznameni_vypujcky.obsah_oznameni_error", 
                    null, LocaleContextHolder.getLocale()));
        }
    }
}
