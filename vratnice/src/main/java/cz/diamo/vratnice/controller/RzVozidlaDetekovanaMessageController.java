package cz.diamo.vratnice.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RzVozidlaDetekovanaMessageController {

    @MessageMapping("/hello") // Handle messages sent to /app/hello
    @SendTo("/rz-vozidla/detekovana") 
    public String handleHello(String message) {
        return message;
    }
}