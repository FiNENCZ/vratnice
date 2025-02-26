package cz.dp.vratnice.rest.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.entity.VjezdVozidla;
import cz.dp.vratnice.entity.Vratnice;
import cz.dp.vratnice.entity.VyjezdVozidla;
import cz.dp.vratnice.enums.RzDetectedMessageStatusEnum;
import cz.dp.vratnice.rest.dto.VjezdVyjezdVozidlaDto;
import cz.dp.vratnice.service.RzVozidlaDetektorService;
import cz.dp.vratnice.service.VjezdVozidlaService;
import cz.dp.vratnice.service.VratniceService;
import cz.dp.vratnice.service.VyjezdVozidlaService;
import jakarta.transaction.Transactional;

@Service
public class VratniceKameryRestService {

    @Autowired
    private VjezdVozidlaService vjezdVozidlaService;

    @Autowired
    private VyjezdVozidlaService vyjezdVozidlaService;

    @Autowired
    private RzVozidlaDetektorService rzVozidlaDetektorService;

    @Autowired
    private VratniceService vratniceService;

    @Autowired
    private MessageSource messageSource;

    @Transactional
    public void saveNevyporadaneZaznamy(List<VjezdVyjezdVozidlaDto> vjezdVyjezdVozidlaDtoList, String idVratnice) throws JSONException, RecordNotFoundException, NoSuchMessageException  {
        Vratnice vratniceKamery = vratniceService.getDetail(idVratnice);

        if (vratniceKamery == null)
            throw new RecordNotFoundException(
                    messageSource.getMessage("vratnice.not_found", null, LocaleContextHolder.getLocale()));
        
        for (VjezdVyjezdVozidlaDto dto : vjezdVyjezdVozidlaDtoList) {
            if(dto.getVjezd()) {
                VjezdVozidla vjezdVozidla = new VjezdVozidla();
                vjezdVozidla.setRzVozidla(dto.getRzVozidla());
                vjezdVozidla.setCasPrijezdu(dto.getCasPrijezdu().toInstant().atZone(ZoneId.systemDefault()));
                vjezdVozidla.setAktivita(true);
                vjezdVozidla.setCasZmn(Timestamp.from(Instant.now()));
                vjezdVozidla.setZmenuProvedl("kamery");
                vjezdVozidla.setVratnice(vratniceKamery);

                vjezdVozidlaService.create(vjezdVozidla, null);
            } else {
                VyjezdVozidla vyjezdVozidla = new VyjezdVozidla();
                vyjezdVozidla.setRzVozidla(dto.getRzVozidla());
                vyjezdVozidla.setCasOdjezdu(dto.getCasPrijezdu().toInstant().atZone(ZoneId.systemDefault()));
                vyjezdVozidla.setAktivita(true);
                vyjezdVozidla.setCasZmn(Timestamp.from(Instant.now()));
                vyjezdVozidla.setZmenuProvedl("kamery");
                vyjezdVozidla.setVratnice(vratniceKamery);

                vyjezdVozidlaService.create(vyjezdVozidla, null);
            }
        }


        rzVozidlaDetektorService.sendWebSocketMessage(idVratnice ,"all", RzDetectedMessageStatusEnum.SLUZEBNI_VOZIDLO, null);
    }
    

}
