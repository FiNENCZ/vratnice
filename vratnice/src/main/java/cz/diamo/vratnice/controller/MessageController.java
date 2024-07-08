package cz.diamo.vratnice.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @MessageMapping("/hello") // Handle messages sent to /app/hello
    @SendTo("/topic/greetings") // Send the response to /topic/greetings
    public String handleHello(String message) {
        return message;
    }
}