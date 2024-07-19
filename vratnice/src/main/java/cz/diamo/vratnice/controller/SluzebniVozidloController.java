package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.dto.SluzebniVozidloDto;
import cz.diamo.vratnice.dto.SluzebniVozidloFunkceDto;
import cz.diamo.vratnice.dto.SluzebniVozidloKategorieDto;
import cz.diamo.vratnice.dto.SluzebniVozidloStavDto;
import cz.diamo.vratnice.dto.VozidloTypDto;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.entity.SluzebniVozidloFunkce;
import cz.diamo.vratnice.entity.SluzebniVozidloKategorie;
import cz.diamo.vratnice.entity.SluzebniVozidloStav;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.service.HistorieSluzebniVozidloService;
import cz.diamo.vratnice.service.SluzebniVozidloService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class SluzebniVozidloController extends BaseController {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloController.class);

    @Autowired
    private SluzebniVozidloService sluzebniVozidloService;

    @Autowired
    private HistorieSluzebniVozidloService historieSluzebniVozidloService;

    @Autowired
    private UzivatelServices uzivatelServices;

    @PostMapping("/sluzebni-vozidlo/save")
    public ResponseEntity<SluzebniVozidloDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid SluzebniVozidloDto sluzebniVozidloDto) throws RecordNotFoundException, NoSuchMessageException, InterruptedException, ExecutionException {
        
  
            // Je nutné provádět asynchronně, jinak dochází k nekonzistenci dat -> newSluzebniVozidlo je vytvoře dříve než 
            //načteno oldSluzebniVozidlo z databaze
            CompletableFuture<SluzebniVozidlo> oldSluzebniVozidloFuture = CompletableFuture.supplyAsync(() -> {
                if (sluzebniVozidloDto.getIdSluzebniVozidlo() != null) {
                    return sluzebniVozidloService.getDetail(sluzebniVozidloDto.getIdSluzebniVozidlo());
                } else {
                    return new SluzebniVozidlo();
                }
            });
    
            CompletableFuture<SluzebniVozidlo> newSluzebniVozidloFuture = oldSluzebniVozidloFuture.thenApplyAsync(oldSluzebniVozidlo -> {
                try {
                    return sluzebniVozidloService.create(sluzebniVozidloDto.toEntity());
                    
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            });
    
            SluzebniVozidlo oldSluzebniVozidlo = oldSluzebniVozidloFuture.get();
            SluzebniVozidlo newSluzebniVozidlo = newSluzebniVozidloFuture.get();
    

            Uzivatel uzivatelAkce = uzivatelServices.getDetail(appUserDto.getIdUzivatel());

            historieSluzebniVozidloService.create(newSluzebniVozidlo, oldSluzebniVozidlo, uzivatelAkce);


            return ResponseEntity.ok(new SluzebniVozidloDto(newSluzebniVozidlo));
        
   
        
    }
    
    @GetMapping("/sluzebni-vozidlo/list")
    public ResponseEntity<List<SluzebniVozidloDto>> list(@RequestParam @Nullable Boolean aktivni) {
        List<SluzebniVozidloDto> result = new ArrayList<SluzebniVozidloDto>();
        List<SluzebniVozidlo> list = sluzebniVozidloService.getList(aktivni);

        if (list != null && list.size() > 0) {
            for (SluzebniVozidlo sluzebniVozidlo : list) {
                result.add(new SluzebniVozidloDto(sluzebniVozidlo));
            }
        }

        return ResponseEntity.ok(result);
    }
    

    @GetMapping("/sluzebni-vozidlo/detail")
    public ResponseEntity<SluzebniVozidloDto> getDetail(@RequestParam String id) {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloService.getDetail(id);
        if (sluzebniVozidlo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new SluzebniVozidloDto(sluzebniVozidlo));
    }

    @GetMapping("/sluzebni-vozidlo/dle-stavu")
    public  ResponseEntity<List<SluzebniVozidloDto>> getSluzebniVozidloByStav(@RequestParam String stav) {
        List<SluzebniVozidloDto> sluzebniVozidla = sluzebniVozidloService.getSluzebniVozidloByStav(stav).stream()
            .map(SluzebniVozidloDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(sluzebniVozidla);
    }

    @GetMapping("/sluzebni-vozidlo/typ")
    public ResponseEntity<VozidloTypDto> typ(@RequestParam String idSluzebniVozidlo) {
        VozidloTyp vozidloTyp = sluzebniVozidloService.getVozidloTyp(idSluzebniVozidlo);
        return ResponseEntity.ok(new VozidloTypDto(vozidloTyp));
    }

    @GetMapping("/sluzebni-vozidlo/kategorie")
    public ResponseEntity<SluzebniVozidloKategorieDto> kategorie(@RequestParam String idSluzebniVozidlo) {
        SluzebniVozidloKategorie kategorie = sluzebniVozidloService.getKategorie(idSluzebniVozidlo);
        return ResponseEntity.ok(new SluzebniVozidloKategorieDto(kategorie));
    }

    @GetMapping("/sluzebni-vozidlo/funkce")
    public ResponseEntity<SluzebniVozidloFunkceDto> funkce(@RequestParam String idSluzebniVozidlo) {
        SluzebniVozidloFunkce funkce = sluzebniVozidloService.getFunkce(idSluzebniVozidlo);
        return ResponseEntity.ok(new SluzebniVozidloFunkceDto(funkce));
    }

    @GetMapping("/sluzebni-vozidlo/stav")
    public ResponseEntity<SluzebniVozidloStavDto> stav(@RequestParam String idSluzebniVozidlo) {
        SluzebniVozidloStav stav = sluzebniVozidloService.getVozidloStav(idSluzebniVozidlo);
        return ResponseEntity.ok(new SluzebniVozidloStavDto(stav));
    }

    @GetMapping("/sluzebni-vozidlo/get-by-rz")
    public ResponseEntity<SluzebniVozidloDto> getByRz(@RequestParam String rz) {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloService.getByRz(rz);
        return ResponseEntity.ok(new SluzebniVozidloDto(sluzebniVozidlo));
    }



}
