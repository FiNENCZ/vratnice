package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.AccessDeniedException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.dto.SluzebniVozidloDto;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
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
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_SLUZEBNI_VOZIDLO')")
    public ResponseEntity<SluzebniVozidloDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid SluzebniVozidloDto sluzebniVozidloDto) throws RecordNotFoundException, NoSuchMessageException, InterruptedException, ExecutionException {
        
  
            // Je nutné provádět asynchronně, jinak dochází k nekonzistenci dat -> newSluzebniVozidlo je vytvoře dříve než 
            //načteno oldSluzebniVozidlo z databaze
            CompletableFuture<SluzebniVozidlo> oldSluzebniVozidloFuture = CompletableFuture.supplyAsync(() -> {
                if (sluzebniVozidloDto.getIdSluzebniVozidlo() != null) {
                    try {
                        return sluzebniVozidloService.getDetail(appUserDto, sluzebniVozidloDto.getIdSluzebniVozidlo());
                    } catch (RecordNotFoundException | NoSuchMessageException | AccessDeniedException e) {
                        // Propagace výjimky dál pomocí CompletionException
                        throw new CompletionException(e);
                    }
                } else {
                    return new SluzebniVozidlo();
                }
            });
            CompletableFuture<SluzebniVozidlo> newSluzebniVozidloFuture = oldSluzebniVozidloFuture.thenApplyAsync(oldSluzebniVozidlo -> {
                try {
                    return sluzebniVozidloService.create(appUserDto, sluzebniVozidloDto.toEntity());
                    
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
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_SLUZEBNI_VOZIDLO')")
    public ResponseEntity<List<SluzebniVozidloDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam @Nullable Boolean aktivni) throws RecordNotFoundException, NoSuchMessageException {
        List<SluzebniVozidloDto> result = new ArrayList<SluzebniVozidloDto>();
        List<SluzebniVozidlo> list = sluzebniVozidloService.getList(appUserDto, aktivni);

        if (list != null && list.size() > 0) {
            for (SluzebniVozidlo sluzebniVozidlo : list) {
                result.add(new SluzebniVozidloDto(sluzebniVozidlo));
            }
        }

        return ResponseEntity.ok(result);
    }
    

    @GetMapping("/sluzebni-vozidlo/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_SLUZEBNI_VOZIDLO')")
    public ResponseEntity<SluzebniVozidloDto> getDetail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam String id) throws RecordNotFoundException, NoSuchMessageException, AccessDeniedException {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloService.getDetail(appUserDto, id);
        if (sluzebniVozidlo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new SluzebniVozidloDto(sluzebniVozidlo));
    }

    @GetMapping("/sluzebni-vozidlo/get-by-rz")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<SluzebniVozidloDto> getByRz(@RequestParam String rz) {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloService.getByRz(rz);
        return ResponseEntity.ok(new SluzebniVozidloDto(sluzebniVozidlo));
    }
}
