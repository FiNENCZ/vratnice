package cz.diamo.vratnice.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.dto.RidicDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.repository.PovoleniVjezduVozidlaRepository;
import cz.diamo.vratnice.repository.VjezdVozidlaRepository;
import jakarta.transaction.Transactional;

@Service
public class PovoleniVjezduVozidlaService {

    final static Logger logger = LogManager.getLogger(PovoleniVjezduVozidlaService.class);

    @Autowired
    private PovoleniVjezduVozidlaRepository povoleniVjezduVozidlaRepository;

    @Autowired
    private RidicService ridicService;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;


    @Autowired
    private VratniceService vratniceService;

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    public List<PovoleniVjezduVozidla> getAll() {
        return povoleniVjezduVozidlaRepository.findAll();
    }

    public PovoleniVjezduVozidla getDetail(String idPovoleniVjezduVozidla) {
        return povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
    }

    public List<PovoleniVjezduVozidla> getByStav(String stav) {
        return povoleniVjezduVozidlaRepository.getByStav(stav);
    }

    public List<PovoleniVjezduVozidla> getByRzVozidla(String rzVozidla) {
        return povoleniVjezduVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public Optional<PovoleniVjezduVozidla> jeRzVozidlaPovolena(String rzVozidla, String idVratnice) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice vratniceKamery = vratniceService.getDetail(idVratnice);

        if (vratniceKamery == null) 
            throw new RecordNotFoundException(
                String.format(messageSource.getMessage("vratnice.not_found", null, LocaleContextHolder.getLocale())));
        

        Zavod zavodKameryVratnice = vratniceKamery.getZavod();


        List<PovoleniVjezduVozidla> povoleniVjezduVozidlaList = getByRzVozidla(rzVozidla);
        
        // Pokud není žádné povolení, vrátí prázdný Optional
        if (povoleniVjezduVozidlaList.isEmpty()) {
            return Optional.empty();
        }
    
        LocalDate currentDate = LocalDate.now();
        logger.info(currentDate);
            
        return povoleniVjezduVozidlaList.stream()
            .filter(povoleni -> isPovoleniPlatne(povoleni, currentDate))
            .filter(povoleni -> obsahujeLokalitu(povoleni, zavodKameryVratnice.getIdZavod()))
            .findFirst();
    }

    private boolean isPovoleniPlatne(PovoleniVjezduVozidla povoleni, LocalDate currentDate) {
        //Porovnání pouze data, nikoliv času
        LocalDate datumOd = convertToLocalDate(povoleni.getDatumOd());
        LocalDate datumDo = convertToLocalDate(povoleni.getDatumDo());
        
        return (currentDate.isEqual(datumOd) || currentDate.isAfter(datumOd)) 
                && (currentDate.isEqual(datumDo) || currentDate.isBefore(datumDo));
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private boolean obsahujeLokalitu(PovoleniVjezduVozidla povoleni, String idZavodKameryVratnice) {
        return povoleni.getLokality() != null && povoleni.getLokality().stream()
            .anyMatch(lokalita -> idZavodKameryVratnice.equals(lokalita.getIdLokalita()));
    }

    @Transactional
    public PovoleniVjezduVozidla create(PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto) throws UniqueValueException, NoSuchMessageException {
        if (povoleniVjezduVozidlaDto.getRidic() != null) {
            Ridic savedRidic =  ridicService.create(povoleniVjezduVozidlaDto.getRidic().toEntity());
            povoleniVjezduVozidlaDto.setRidic(new RidicDto(savedRidic));
        }

        return povoleniVjezduVozidlaRepository.save(povoleniVjezduVozidlaDto.toEntity());
    }

    public Stat getZemeRegistraceVozidla(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
        try {
            if (povoleniVjezduVozidla == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            povoleniVjezduVozidla.getZemeRegistraceVozidla().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), povoleniVjezduVozidla.getZemeRegistraceVozidla().getNazevResx()));
            return povoleniVjezduVozidla.getZemeRegistraceVozidla();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public List<VozidloTyp> getTypyVozidel(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
        try {
            if (povoleniVjezduVozidla == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            List<VozidloTyp> typyVozidel = new ArrayList<VozidloTyp>();

            for (VozidloTyp vozidloTyp : povoleniVjezduVozidla.getTypVozidla()){
                vozidloTyp.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), vozidloTyp.getNazevResx()));
                typyVozidel.add(vozidloTyp);
            }

            return typyVozidel;

        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public Integer pocetVjezdu(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleni = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);

        if (povoleni == null || povoleni.getRzVozidla() == null || povoleni.getDatumOd() == null || povoleni.getDatumDo() == null) {
            throw new IllegalArgumentException("Povoleni nebo jeho klicove udaje nemohou byt null.");
        }

        // Pouziti pomoci metody s daty z povoleni
        return pocetVjezduProInterval(povoleni.getRzVozidla(), povoleni.getDatumOd(), povoleni.getDatumDo());
    }

    public Integer pocetVjezdu(String idPovoleniVjezduVozidla, Date datumOd, Date datumDo) {
        PovoleniVjezduVozidla povoleni = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);

        if (povoleni == null || povoleni.getRzVozidla() == null || datumOd == null || datumDo == null) {
            throw new IllegalArgumentException("Povoleni, jeho klicove udaje nebo datumy od a do nemohou byt null.");
        }

        // Pouziti pomoci metody s poskytnutymi daty
        return pocetVjezduProInterval(povoleni.getRzVozidla(), datumOd, datumDo);
    }

    private Integer pocetVjezduProInterval(List<String> rzVozidla, Date datumOd, Date datumDo) {
        ZonedDateTime datumOdZoned = ZonedDateTime.ofInstant(datumOd.toInstant(), ZoneId.systemDefault());
        ZonedDateTime datumDoZoned = ZonedDateTime.ofInstant(datumDo.toInstant(), ZoneId.systemDefault());

        logger.info("---------------------------");
        logger.info(datumOdZoned);
        logger.info(datumDoZoned);

        int pocetVjezdu = 0;

        for (String rz : rzVozidla) {
            // Ziskani vjezdu podle RZ vozidla v danem obdobi
            List<VjezdVozidla> vjezdy = vjezdVozidlaRepository.findByRzVozidlaAndDatumOdBetween(rz, datumOdZoned, datumDoZoned);

            pocetVjezdu += vjezdy.size();
        }

        return pocetVjezdu;
    }

}
