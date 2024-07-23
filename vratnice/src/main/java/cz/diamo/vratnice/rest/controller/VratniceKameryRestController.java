package cz.diamo.vratnice.rest.controller;


import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.rest.controller.BaseRestController;
import cz.diamo.vratnice.dto.RzDetectedMessageDto;
import cz.diamo.vratnice.service.RzVozidlaDetektorService;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("vratnice-public")
public class VratniceKameryRestController extends BaseRestController {

    final static Logger logger = LogManager.getLogger(VratniceKameryRestController.class);

    @Autowired
    private RzVozidlaDetektorService rzVozidlaDetektorService;

    @PostMapping("/rz-vozidla-detektor/detekce")
    private RzDetectedMessageDto processRzVozidla(@RequestParam String rzVozidla, @RequestParam Boolean vjezd) throws JSONException {
        if (vjezd) {
            return rzVozidlaDetektorService.checkIfRzVozidlaIsAllowedAndSendWS(rzVozidla, vjezd);
        } else {
            return rzVozidlaDetektorService.checkIfRzVozidlaCanLeaveAndSendWs(rzVozidla, vjezd);
        }
    }

}
