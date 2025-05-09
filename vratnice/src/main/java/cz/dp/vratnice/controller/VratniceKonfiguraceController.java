package cz.dp.vratnice.controller;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.vratnice.configuration.VratniceProperties;
import cz.dp.vratnice.dto.VratniceKonfiguraceDto;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class VratniceKonfiguraceController extends BaseController {

    final static Logger logger = LogManager.getLogger(VratniceKonfiguraceController.class);

    @Autowired
    private VratniceProperties vratniceProperties;

    @GetMapping("/vratnice-konfigurace")
    @PreAuthorize("isFullyAuthenticated()")
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

    @PostMapping("/vratnice-konfigurace/is-datum-pouceni-valid")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<Boolean> isDatumPouceniValid(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date datumPouceni) {
        
        int dobaPouceni = vratniceProperties.getDobaPouceni();

        // Získání aktuálního data
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        // Přidání dobaPouceni měsíců k datumPouceni
        cal.setTime(datumPouceni);
        cal.add(Calendar.MONTH, dobaPouceni);
        Date datumPouceniPlusDoba = cal.getTime();

        // Porovnání s aktuálním datem
        boolean isValid = datumPouceniPlusDoba.after(currentDate);

        return ResponseEntity.ok(isValid);
    }
}
