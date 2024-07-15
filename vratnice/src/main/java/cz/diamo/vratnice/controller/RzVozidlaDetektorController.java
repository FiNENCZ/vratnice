package cz.diamo.vratnice.controller;

import java.util.Optional;

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
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.VyjezdVozidla;
import cz.diamo.vratnice.enums.RzDetectedMessageStatusEnum;
import cz.diamo.vratnice.service.PovoleniVjezduVozidlaService;
import cz.diamo.vratnice.service.SluzebniVozidloService;
import cz.diamo.vratnice.service.VyjezdVozidlaService;
import cz.diamo.vratnice.service.WebSocketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RzVozidlaDetektorController extends BaseController {

    private static final Logger logger = LogManager.getLogger(RzVozidlaDetektorController.class);

    @Autowired
    private PovoleniVjezduVozidlaService povoleniVjezduVozidlaService;

    @Autowired
    private SluzebniVozidloService sluzebniVozidloService;

    @Autowired
    private VyjezdVozidlaService vyjezdVozidlaService;
    @Autowired
    WebSocketService webSocketService;

    @PostMapping(value = "/rz-vozidla-detektor/detekce-vjezd", consumes = "multipart/form-data")
    public String processRzVozidlaVjezd(
        @RequestParam("anpr.xml") MultipartFile xmlFile,
        @RequestParam(value = "licensePlatePicture.jpg", required = false) MultipartFile licensePlatePicture,
        @RequestParam(value = "vehiclePicture.jpg", required = false) MultipartFile vehiclePicture,
        @RequestParam(value = "detectionPicture.jpg", required = false) MultipartFile detectionPicture) {

        return processRzVozidla(xmlFile, true);
    }

    @PostMapping(value = "/rz-vozidla-detektor/detekce-vyjezd", consumes = "multipart/form-data")
    public String processRzVozidlaVyjezd(
        @RequestParam("anpr.xml") MultipartFile xmlFile,
        @RequestParam(value = "licensePlatePicture.jpg", required = false) MultipartFile licensePlatePicture,
        @RequestParam(value = "vehiclePicture.jpg", required = false) MultipartFile vehiclePicture,
        @RequestParam(value = "detectionPicture.jpg", required = false) MultipartFile detectionPicture) {

        return processRzVozidla(xmlFile, false);
    }

    private String processRzVozidla(MultipartFile xmlFile, boolean vjezd) {
        try {
            String licensePlateValue = getLicensePlateFromXml(xmlFile);
            if (licensePlateValue != null) {
                if (licensePlateValue != "unknown") {
                    if (vjezd) {
                        checkIfRzVozidlaIsAllowedAndSendWS(licensePlateValue, vjezd);
                    } else {
                        checkIfRzVozidlaCanLeaveAndSendWs(licensePlateValue, vjezd);
                    }
                }

                logger.info("License Plate: " + licensePlateValue);
                return "License Plate: " + licensePlateValue;
            } else {
                return "License Plate not found in XML.";
            }
        } catch (Exception e) {
            logger.error("Error processing XML file.", e);
            return "Error processing XML file.";
        }
    }

    private String getLicensePlateFromXml(MultipartFile xmlFile) throws Exception {
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
            return licensePlateNode.getTextContent();
        } else {
            return null;
        }
    }

    @PostMapping(value = "/rz-vozidla-detektor/test")
    public void test(@RequestParam String rzVozidla, @RequestParam Boolean vjezd ) throws JSONException {
        if (vjezd) {
            checkIfRzVozidlaIsAllowedAndSendWS(rzVozidla, vjezd);
        } else {
            checkIfRzVozidlaCanLeaveAndSendWs(rzVozidla, vjezd);
        }
        
    }

    public void checkIfRzVozidlaIsAllowedAndSendWS(String rzVozidla, Boolean vjezd) throws JSONException {
        Optional<PovoleniVjezduVozidla> result = povoleniVjezduVozidlaService.jeRzVozidlaPovolena(rzVozidla);

        if (result.isPresent()) {
            sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO, vjezd);
        } else {
            if (sluzebniVozidloService.isSluzebniVozidlo(rzVozidla)) {
                sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.SLUZEBNI_VOZIDLO, vjezd);
            } else {
                sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.NEPOVOLENE_VOZIDLO, vjezd);
            }
        }
    }

    public void checkIfRzVozidlaCanLeaveAndSendWs(String rzVozidla, Boolean vjezd) throws JSONException {
        Optional<VyjezdVozidla> result = vyjezdVozidlaService.jeMozneVyjet(rzVozidla);

        if (result.isPresent()) {
            sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.POVOLENE_VOZIDLO, vjezd);
        } else {
            sendWebSocketMessage(rzVozidla, RzDetectedMessageStatusEnum.NEPOVOLENE_VOZIDLO, vjezd);
        }
    }

    public void sendWebSocketMessage(String rzVozidla, RzDetectedMessageStatusEnum status, Boolean vjezd) throws JSONException {
        RzDetectedMessageDto dto = new RzDetectedMessageDto();
        dto.setRzVozidla(rzVozidla);
        dto.setStatus(status);
        dto.setIsVjezd(vjezd);

        webSocketService.sendMessage(dto);
    }

}