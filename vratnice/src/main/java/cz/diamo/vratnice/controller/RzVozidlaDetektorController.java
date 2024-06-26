package cz.diamo.vratnice.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class RzVozidlaDetektorController extends BaseController {

    final static Logger logger = LogManager.getLogger(RzVozidlaDetektorController.class);

 @PostMapping("/rz-vozidla-detektor/detekce")
    public String handleUniversalPost(@RequestBody Map<String, Object> requestBody) {
        // Zde můžete zpracovat libovolná JSON data
        System.out.println("Received request body: " + requestBody);
        
        // Zapisování dat do souboru
        try {
            String content = "Received request body: " + requestBody.toString();
            Files.write(Paths.get("testRequestbody.txt"), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("Error writing to file", e);
        }
        
        return "Data received successfully";
    }

    
}
