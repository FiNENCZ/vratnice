package cz.dp.share.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.dto.AppUserDto;
import cz.dp.share.services.UzivatelskeNastaveniServices;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
public class UzivatelskeNastaveniController extends BaseController {

    final static Logger logger = LogManager.getLogger(UzivatelskeNastaveniController.class);

    @Autowired
    private UzivatelskeNastaveniServices uzivatelskeNastaveniServices;

    @GetMapping("/uzivatelske-nastaveni/detail")
    public String detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam String klic) {
        return uzivatelskeNastaveniServices.getDetail(appUserDto.getIdUzivatel(), klic);
    }

    @PostMapping("/uzivatelske-nastaveni/save")
    public void save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam String klic, @RequestBody String hodnota) {
        uzivatelskeNastaveniServices.save(appUserDto.getIdUzivatel(), klic, hodnota);
    }

    @DeleteMapping("/uzivatelske-nastaveni/delete")
    public void delete(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam String klic) {
        uzivatelskeNastaveniServices.delete(appUserDto.getIdUzivatel(), klic);
    }

    @DeleteMapping("/uzivatelske-nastaveni/delete-all")
    public void deleteAll(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) {
        uzivatelskeNastaveniServices.delete(appUserDto.getIdUzivatel());
    }
}
