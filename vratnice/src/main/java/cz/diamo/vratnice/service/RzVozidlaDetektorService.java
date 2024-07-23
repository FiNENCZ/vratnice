package cz.diamo.vratnice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

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

    public RzDetectedMessageDto checkIfRzVozidlaIsAllowedAndSendWS(String rzVozidla, Boolean vjezd) throws JSONException {
        Optional<PovoleniVjezduVozidla> result = povoleniVjezduVozidlaService.jeRzVozidlaPovolena(rzVozidla);

        if (result.isPresent()) {
            return sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO, vjezd);
        } else {
            if (sluzebniVozidloService.isSluzebniVozidlo(rzVozidla)) {
                return sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.SLUZEBNI_VOZIDLO, vjezd);
            } else {
                return sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.NEPOVOLENE_VOZIDLO, vjezd);
            }
        }
    }

    public RzDetectedMessageDto checkIfRzVozidlaCanLeaveAndSendWs(String rzVozidla, Boolean vjezd) throws JSONException {
        Optional<VyjezdVozidla> result = vyjezdVozidlaService.jeMozneVyjet(rzVozidla);

        if (result.isPresent()) {
            return sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO, vjezd);
        } else {
            return sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.NEPOVOLENE_VOZIDLO, vjezd);
        }
    }

    public RzDetectedMessageDto sendWebSocketMessage(String rzVozidla, RzDetectedMessageStatusEnum status, Boolean vjezd) throws JSONException {
        RzDetectedMessageDto dto = new RzDetectedMessageDto();
        dto.setRzVozidla(rzVozidla);
        dto.setStatus(status);
        dto.setIsVjezd(vjezd);

        webSocketService.sendMessage(dto);
        return dto;
    }


}
