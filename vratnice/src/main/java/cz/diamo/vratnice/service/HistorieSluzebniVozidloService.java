package cz.diamo.vratnice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.HistorieSluzebniVozidlo;
import cz.diamo.vratnice.entity.HistorieSluzebniVozidloAkce;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.enums.HistorieSluzebniVozidloAkceEnum;
import cz.diamo.vratnice.enums.SluzebniVozidloStavEnum;
import cz.diamo.vratnice.repository.HistorieSluzebniVozidloRepository;
import jakarta.transaction.Transactional;

@Service
public class HistorieSluzebniVozidloService {

    final static Logger logger = LogManager.getLogger(HistorieSluzebniVozidloService.class);

    @Autowired
    private HistorieSluzebniVozidloRepository historieSluzebniVozidloRepository;


    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;


    @Transactional
    public HistorieSluzebniVozidlo create(SluzebniVozidlo newSluzebniVozidlo, SluzebniVozidlo oldSluzebniVozidlo, Uzivatel vratny) {
        HistorieSluzebniVozidlo historieSluzebniVozidlo = new HistorieSluzebniVozidlo();
        historieSluzebniVozidlo.setSluzebniVozidlo(newSluzebniVozidlo);

        //String idSluzebniVozidloKey = sluzebniVozidlo.getIdSluzebniVozidlo();
        logger.info(newSluzebniVozidlo.getStav().getSluzebniVozidloStavEnum());
        logger.info(oldSluzebniVozidlo.getStav().getSluzebniVozidloStavEnum());
        logger.info(newSluzebniVozidlo);
        logger.info("-------------");
        logger.info(oldSluzebniVozidlo);
        logger.info(newSluzebniVozidlo.getStav());
        logger.info(oldSluzebniVozidlo.getStav());
        logger.info(newSluzebniVozidlo.getStav().getNazevResx());
        logger.info(oldSluzebniVozidlo.getStav().getNazevResx());

        logger.info(oldSluzebniVozidlo.getIdSluzebniVozidlo());

        if (oldSluzebniVozidlo.getIdSluzebniVozidlo() != null) {


            historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_UPRAVENO));
            if(oldSluzebniVozidlo.getStav().getSluzebniVozidloStavEnum() == SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_BLOKOVANE
                && newSluzebniVozidlo.getStav().getSluzebniVozidloStavEnum() == SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_AKTIVNI){
                    historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO));
            }
            if(oldSluzebniVozidlo.getStav().getSluzebniVozidloStavEnum() == SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_AKTIVNI
                && newSluzebniVozidlo.getStav().getSluzebniVozidloStavEnum() == SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_BLOKOVANE){
                    historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_BLOKOVANO));
            }
            if(oldSluzebniVozidlo.getAktivita() && !newSluzebniVozidlo.getAktivita()){
                    historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_ODSTRANENO));
            }
            if(!oldSluzebniVozidlo.getAktivita() && newSluzebniVozidlo.getAktivita()) {
                historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO));
            }
        }
        else {
            historieSluzebniVozidlo.setAkce(new HistorieSluzebniVozidloAkce(HistorieSluzebniVozidloAkceEnum.HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO));
        }

        historieSluzebniVozidlo.setDatum(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        historieSluzebniVozidlo.setUzivatel(vratny);

        return historieSluzebniVozidloRepository.save(historieSluzebniVozidlo);
    }

    public List<HistorieSluzebniVozidlo> findBySluzebniVozidlo(SluzebniVozidlo sluzebniVozidlo) {
        return historieSluzebniVozidloRepository.findBySluzebniVozidlo(sluzebniVozidlo);
    }

    public HistorieSluzebniVozidloAkce getAkci(String idHistorieSluzebniVozidlo) {
        HistorieSluzebniVozidlo historieSluzebniVozidlo = historieSluzebniVozidloRepository.getDetail(idHistorieSluzebniVozidlo);
        try {
            if (historieSluzebniVozidlo == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            historieSluzebniVozidlo.getAkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), historieSluzebniVozidlo.getAkce().getNazevResx()));
            return historieSluzebniVozidlo.getAkce();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }


}
