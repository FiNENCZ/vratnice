package cz.diamo.vratnice.rest.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.VyjezdVozidla;
import cz.diamo.vratnice.enums.RzDetectedMessageStatusEnum;
import cz.diamo.vratnice.rest.dto.VjezdVyjezdVozidlaDto;
import cz.diamo.vratnice.service.RzVozidlaDetektorService;
import cz.diamo.vratnice.service.VjezdVozidlaService;
import cz.diamo.vratnice.service.VyjezdVozidlaService;
import jakarta.transaction.Transactional;

@Service
public class VratniceKameryRestService {

    @Autowired
    private VjezdVozidlaService vjezdVozidlaService;

    @Autowired
    private VyjezdVozidlaService vyjezdVozidlaService;

    @Autowired
    private RzVozidlaDetektorService rzVozidlaDetektorService;

    @Transactional
    public String saveNevyporadaneZaznamy(List<VjezdVyjezdVozidlaDto> vjezdVyjezdVozidlaDtoList) throws JSONException, RecordNotFoundException, NoSuchMessageException  {
        for (VjezdVyjezdVozidlaDto dto : vjezdVyjezdVozidlaDtoList) {
            if(dto.getVjezd()) {
                VjezdVozidla vjezdVozidla = new VjezdVozidla();
                vjezdVozidla.setRzVozidla(dto.getRzVozidla());
                vjezdVozidla.setCasPrijezdu(dto.getCasPrijezdu().toInstant().atZone(ZoneId.systemDefault()));
                vjezdVozidla.setAktivita(true);
                vjezdVozidla.setCasZmn(Timestamp.from(Instant.now()));
                vjezdVozidla.setZmenuProvedl("kamery");

                vjezdVozidlaService.create(vjezdVozidla, null);
            } else {
                VyjezdVozidla vyjezdVozidla = new VyjezdVozidla();
                vyjezdVozidla.setRzVozidla(dto.getRzVozidla());
                vyjezdVozidla.setCasOdjezdu(dto.getCasPrijezdu().toInstant().atZone(ZoneId.systemDefault()));
                vyjezdVozidla.setAktivita(true);
                vyjezdVozidla.setCasZmn(Timestamp.from(Instant.now()));
                vyjezdVozidla.setZmenuProvedl("kamery");

                vyjezdVozidlaService.create(vyjezdVozidla, null);
            }
        }


        rzVozidlaDetektorService.sendWebSocketMessage("all", RzDetectedMessageStatusEnum.SLUZEBNI_VOZIDLO, null);
        return "";
    }

}
