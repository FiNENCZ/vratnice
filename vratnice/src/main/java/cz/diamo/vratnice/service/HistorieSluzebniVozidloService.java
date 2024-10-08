package cz.diamo.vratnice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
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
    private ResourcesComponent resourcesComponent;


    @Transactional
    public HistorieSluzebniVozidlo create(SluzebniVozidlo newSluzebniVozidlo, SluzebniVozidlo oldSluzebniVozidlo, Uzivatel vratny) {
        HistorieSluzebniVozidlo historieSluzebniVozidlo = new HistorieSluzebniVozidlo();
        historieSluzebniVozidlo.setSluzebniVozidlo(newSluzebniVozidlo);

        logger.info(newSluzebniVozidlo);
        logger.info(oldSluzebniVozidlo);

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

    public List<HistorieSluzebniVozidlo> findBySluzebniVozidlo(SluzebniVozidlo sluzebniVozidlo) throws RecordNotFoundException, NoSuchMessageException {
        List<HistorieSluzebniVozidlo> list =  historieSluzebniVozidloRepository.findBySluzebniVozidlo(sluzebniVozidlo);

        if (list != null) {
            for (HistorieSluzebniVozidlo historieSluzebniVozidlo : list) {
                historieSluzebniVozidlo.getAkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), historieSluzebniVozidlo.getAkce().getNazevResx()));
            }
        }

        return list;
    }
}
