package cz.diamo.vratnice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.RzDetectedMessageDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.VyjezdVozidla;
import cz.diamo.vratnice.enums.RzDetectedMessageStatusEnum;

@Service
public class RzVozidlaDetektorService {

    @Autowired
    private PovoleniVjezduVozidlaService povoleniVjezduVozidlaService;

    @Autowired
    private SluzebniVozidloService sluzebniVozidloService;

    @Autowired
    private VyjezdVozidlaService vyjezdVozidlaService;
    
    @Autowired
    WebSocketService webSocketService;

    public RzDetectedMessageDto checkIfRzVozidlaIsAllowedAndSendWS(String idVratnice, String rzVozidla, Boolean vjezd) throws JSONException, RecordNotFoundException, NoSuchMessageException {
        Optional<PovoleniVjezduVozidla> result = povoleniVjezduVozidlaService.jeRzVozidlaPovolena(rzVozidla, idVratnice);

        if (result.isPresent()) {
            return sendWebSocketMessage(idVratnice, rzVozidla, RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO, vjezd);
        } else {
            if (sluzebniVozidloService.isSluzebniVozidlo(rzVozidla)) {
                return sendWebSocketMessage(idVratnice, rzVozidla, RzDetectedMessageStatusEnum.SLUZEBNI_VOZIDLO, vjezd);
            } else {
                return sendWebSocketMessage(idVratnice, rzVozidla, RzDetectedMessageStatusEnum.NEPOVOLENE_VOZIDLO, vjezd);
            }
        }
    }

    public RzDetectedMessageDto checkIfRzVozidlaCanLeaveAndSendWs(String idVratnice, String rzVozidla, Boolean vjezd) throws JSONException {
        Optional<VyjezdVozidla> result = vyjezdVozidlaService.jeMozneVyjet(rzVozidla);

        if (result.isPresent()) {
            return sendWebSocketMessage(idVratnice, rzVozidla, RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO, vjezd);
        } else {
            return sendWebSocketMessage(idVratnice, rzVozidla, RzDetectedMessageStatusEnum.NEPOVOLENE_VOZIDLO, vjezd);
        }
    }

    public RzDetectedMessageDto sendWebSocketMessage(String idVratnice, String rzVozidla, RzDetectedMessageStatusEnum status, Boolean vjezd) throws JSONException {
        RzDetectedMessageDto dto = new RzDetectedMessageDto();
        dto.setIdVratnice(idVratnice);
        dto.setRzVozidla(rzVozidla);
        dto.setStatus(status);
        dto.setIsVjezd(vjezd);

        webSocketService.sendMessage(dto);
        return dto;
    }


}
