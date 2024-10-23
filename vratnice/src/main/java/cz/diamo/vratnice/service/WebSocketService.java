package cz.diamo.vratnice.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.dto.RzDetectedMessageDto;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Konstruktor pro inicializaci služby WebSocket.
     *
     * @param messagingTemplate {@link SimpMessagingTemplate} pro odesílání zpráv
     *                          přes WebSocket.
     */
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Odesílá zprávu o detekci registrační značky vozidla.
     *
     * @param message Objekt {@link RzDetectedMessageDto} obsahující informace o
     *                detekované registrační značce.
     */
    public void sendMessage(RzDetectedMessageDto message) {
        messagingTemplate.convertAndSend("/rz-vozidla/detekovana", message);
    }

}
