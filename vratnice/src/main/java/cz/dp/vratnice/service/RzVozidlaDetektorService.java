package cz.dp.vratnice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.vratnice.dto.RzDetectedMessageDto;
import cz.dp.vratnice.entity.PovoleniVjezduVozidla;
import cz.dp.vratnice.entity.VyjezdVozidla;
import cz.dp.vratnice.enums.RzDetectedMessageStatusEnum;

@Service
public class RzVozidlaDetektorService {

    @Autowired
    private PovoleniVjezduVozidlaService povoleniVjezduVozidlaService;

    @Autowired
    private SluzebniVozidloService sluzebniVozidloService;

    @Autowired
    private VyjezdVozidlaService vyjezdVozidlaService;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * Kontroluje, zda je registrační značka vozidla povolena, a odesílá zprávu přes
     * WebSocket.
     *
     * @param idVratnice Identifikátor vratnice, kde se kontroluje povolení vozidla.
     * @param rzVozidla  Registrační značka vozidla, které se kontroluje.
     * @param vjezd      Boolean hodnota, která určuje, zda se jedná o vjezd (true)
     *                   nebo výjezd (false).
     * @return Objekt {@link RzDetectedMessageDto} obsahující informace o stavu
     *         vozidla.
     * @throws JSONException           Pokud dojde k chybě při zpracování JSON.
     * @throws RecordNotFoundException Pokud nebylo nalezeno odpovídající povolení.
     * @throws NoSuchMessageException  Pokud není nalezena zpráva pro daný klíč.
     */
    public RzDetectedMessageDto checkIfRzVozidlaIsAllowedAndSendWS(String idVratnice, String rzVozidla, Boolean vjezd)
            throws JSONException, RecordNotFoundException, NoSuchMessageException {
        Optional<PovoleniVjezduVozidla> result = povoleniVjezduVozidlaService.jeRzVozidlaPovolena(rzVozidla,
                idVratnice);

        if (result.isPresent()) {
            return sendWebSocketMessage(idVratnice, rzVozidla, RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO, vjezd);
        } else {
            if (sluzebniVozidloService.muzeSluzebniVozidloProjetVratnici(rzVozidla, idVratnice)) {
                return sendWebSocketMessage(idVratnice, rzVozidla, RzDetectedMessageStatusEnum.SLUZEBNI_VOZIDLO, vjezd);
            } else {
                return sendWebSocketMessage(idVratnice, rzVozidla, RzDetectedMessageStatusEnum.NEPOVOLENE_VOZIDLO,
                        vjezd);
            }
        }
    }

    /**
     * Kontroluje, zda může vozidlo opustit vratnici, a odesílá zprávu přes
     * WebSocket.
     *
     * @param idVratnice Identifikátor vratnice, odkud vozidlo odjíždí.
     * @param rzVozidla  Registrační značka vozidla, které se kontroluje.
     * @param vjezd      Boolean hodnota, která určuje, zda se jedná o vjezd (true)
     *                   nebo výjezd (false).
     * @return Objekt {@link RzDetectedMessageDto} obsahující informace o stavu
     *         vozidla.
     * @throws JSONException Pokud dojde k chybě při zpracování JSON.
     */
    public RzDetectedMessageDto checkIfRzVozidlaCanLeaveAndSendWs(String idVratnice, String rzVozidla, Boolean vjezd)
            throws JSONException {
        Optional<VyjezdVozidla> result = vyjezdVozidlaService.jeMozneVyjet(rzVozidla);

        RzDetectedMessageStatusEnum stav = result.isPresent() ? RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO
                : RzDetectedMessageStatusEnum.NEPOVOLENE_VOZIDLO;

        return sendWebSocketMessage(idVratnice, rzVozidla, stav, vjezd);
    }

    /**
     * Odesílá zprávu přes WebSocket s informacemi o vozidle.
     *
     * @param idVratnice Identifikátor vratnice, kde se zpráva odesílá.
     * @param rzVozidla  Registrační značka vozidla, které se zpráva týká.
     * @param status     Stav vozidla jako {@link RzDetectedMessageStatusEnum}.
     * @param vjezd      Boolean hodnota, která určuje, zda se jedná o vjezd (true)
     *                   nebo výjezd (false).
     * @return Objekt {@link RzDetectedMessageDto} obsahující odeslané informace.
     * @throws JSONException Pokud dojde k chybě při zpracování JSON.
     */
    public RzDetectedMessageDto sendWebSocketMessage(String idVratnice, String rzVozidla,
            RzDetectedMessageStatusEnum status, Boolean vjezd) throws JSONException {
        RzDetectedMessageDto dto = new RzDetectedMessageDto();
        dto.setIdVratnice(idVratnice);
        dto.setRzVozidla(rzVozidla);
        dto.setStatus(status);
        dto.setIsVjezd(vjezd);

        webSocketService.sendMessage(dto);
        return dto;
    }

}
