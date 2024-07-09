package cz.diamo.vratnice.controller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.dto.RzDetectedMessageDto;
import cz.diamo.vratnice.enums.RzDetectedMessageStatusEnum;
import cz.diamo.vratnice.service.WebSocketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RzVozidlaDetektorController extends BaseController {

    private static final Logger logger = LogManager.getLogger(RzVozidlaDetektorController.class);

    @Autowired
    WebSocketService webSocketService;

    @PostMapping(value = "/rz-vozidla-detektor/detekce", consumes = "multipart/form-data")
    public String processLicensePlate(
        @RequestParam("anpr.xml") MultipartFile xmlFile,
        @RequestParam(value = "licensePlatePicture.jpg", required = false) MultipartFile licensePlatePicture,
        @RequestParam(value = "vehiclePicture.jpg", required = false) MultipartFile vehiclePicture,
        @RequestParam(value = "detectionPicture.jpg", required = false) MultipartFile detectionPicture) {

    try {
            // Přečtení XML souboru z MultipartFile
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile.getInputStream());

            // Nastavení pro zpracování XML souboru
            doc.getDocumentElement().normalize();

            // Získání kořenového elementu
            Element rootElement = doc.getDocumentElement();

            // Získání elementu <licensePlate>
            NodeList licensePlateNodeList = rootElement.getElementsByTagName("licensePlate");
            if (licensePlateNodeList.getLength() > 0) {
                Node licensePlateNode = licensePlateNodeList.item(0);
                String licensePlateValue = licensePlateNode.getTextContent();
                

                logger.info("License Plate: " + licensePlateValue);

                return "License Plate: " + licensePlateValue;
            } else {
                return "License Plate not found in XML.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing XML file.";
        }
    }

    @PostMapping(value = "/rz-vozidla-detektor/test")
    public void test(@RequestParam String message) throws JSONException {
        RzDetectedMessageDto dto = new RzDetectedMessageDto();
        dto.setRzVozidla(message);
        dto.setStatus(RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO);

        webSocketService.sendMessage(dto);
    }

    public void sendWebSocketMessage(String message, String status) throws JSONException {
        RzDetectedMessageDto dto = new RzDetectedMessageDto();
        dto.setRzVozidla(message);
        dto.setStatus(RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO);

        webSocketService.sendMessage(dto);
    }

}