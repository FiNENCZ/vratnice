package cz.diamo.vratnice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.vratnice.configuration.VratniceProperties;
import cz.diamo.vratnice.dto.VratniceKonfiguraceDto;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class VratniceKonfiguraceController extends BaseController {

    final static Logger logger = LogManager.getLogger(VratniceKonfiguraceController.class);

    @Autowired
    private VratniceProperties vratniceProperties;

    @GetMapping("/vratnice-konfigurace")
    public VratniceKonfiguraceDto detail(HttpServletRequest request) {
        VratniceKonfiguraceDto vratniceKonfiguraceDto = new VratniceKonfiguraceDto();

        vratniceKonfiguraceDto.setZavodNazev(vratniceProperties.getZavodNazev());
        vratniceKonfiguraceDto.setDobaPouceni(vratniceProperties.getDobaPouceni());
        vratniceKonfiguraceDto.setBarvaVyprseniPouceni(vratniceProperties.getBarvaVyprseniPouceni());
        vratniceKonfiguraceDto.setBarvaPovoleneSpz(vratniceProperties.getBarvaPovoleneSpz());
        vratniceKonfiguraceDto.setBarvaNepovoleneSpz(vratniceProperties.getBarvaNepovoleneSpz());
        vratniceKonfiguraceDto.setBarvaSluzebnihoVozidla(vratniceProperties.getBarvaSluzebnihoVozidla());

        return vratniceKonfiguraceDto;
    }
    

}
