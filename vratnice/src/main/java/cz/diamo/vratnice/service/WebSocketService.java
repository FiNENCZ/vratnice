package cz.diamo.vratnice.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.dto.RzDetectedMessageDto;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;


    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(RzDetectedMessageDto message) {
        messagingTemplate.convertAndSend("/rz-vozidla/detekovana", message);
    }

}
