package cz.diamo.vratnice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.SpecialniKlicOznameniVypujcky;
import cz.diamo.vratnice.entity.Vratnice;
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
    private UzivatelVsechnyVratniceService uzivatelVsechnyVratniceService;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @Autowired
    private OznameniServices oznameniServices;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;


     public List<SpecialniKlicOznameniVypujcky> getList(Boolean aktivita, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Boolean maVsechnyVratnice = uzivatelVsechnyVratniceService.jeNastavena(appUserDto);
        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from SpecialniKlicOznameniVypujcky s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
        
        if (!maVsechnyVratnice)
            if (nastavenaVratnice != null) 
                queryString.append(" and s.klic.vratnice = :vratnice");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (!maVsechnyVratnice)
            if (nastavenaVratnice != null)
                vysledek.setParameter("vratnice", nastavenaVratnice);
            else
                return null;
        
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
    public void oznamitVypujcku(Klic klic, HttpServletRequest request) throws NoSuchMessageException, BaseException {
        if (!klic.isSpecialni()) {
            return;
        }

        SpecialniKlicOznameniVypujcky oznameniVypujcky = specialniKlicOznameniVypujckyRepository.getByKlic(klic);

        if (oznameniVypujcky == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        String formattedNow = now.format(formatter);

        AvizaceRequestDto avizaceRequestDto = new AvizaceRequestDto();
        String predmet = messageSource.getMessage("avizace.specialni_klic_oznameni_vypujcky.predmet", null, LocaleContextHolder.getLocale());

        String oznameniText = String.format(
            "Nová výpůjčka speciálního klíče.\n" +
            "Klíč: <strong>%s</strong> - %s (%s)\n" +
            "Uživatel: <strong>TEST</strong>\n" +
            "Datum výpůjčky: <strong>%s</strong>",
            klic.getNazev(),
            klic.getLokalita().getNazev(), klic.getBudova().getNazev(),
            formattedNow
        );

        String telo = String.format("Dobrý den, \n") + oznameniText;

        TypOznameniEnum typOznameni = TypOznameniEnum.DULEZITE_INFO;

        avizaceRequestDto.setEmail(new AvizaceEmailRequestDto(predmet, telo, null));
        avizaceRequestDto.setOznameni(new AvizaceOznameniRequestDto(typOznameni, predmet, oznameniText, null));

        //TODO: dodělat kdo si klíč vypůjčil
        for (Uzivatel uzivatel: oznameniVypujcky.getUzivatele()) {
            String email = uzivatel.getEmail();
            String sapId = uzivatel.getSapId();
            AvizacePrijemceRequestDto prijemnce = new AvizacePrijemceRequestDto();
            
            if (sapId != null) 
                prijemnce.setSapId(sapId);

            if (email != null)
                prijemnce.setEmail(email);
   
            avizaceRequestDto.pridatPrijemce(prijemnce);
        }

        oznameniServices.save(avizaceRequestDto, request);
    }
}
