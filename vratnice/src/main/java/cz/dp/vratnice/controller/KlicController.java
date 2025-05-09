package cz.dp.vratnice.controller;

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

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.services.UzivatelServices;
import cz.dp.vratnice.dto.KlicDto;
import cz.dp.vratnice.entity.Klic;
import cz.dp.vratnice.service.HistorieKlicService;
import cz.dp.vratnice.service.KlicService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class KlicController extends BaseController {

    final static Logger logger = LogManager.getLogger(KlicController.class);

    @Autowired
    private HistorieKlicService historieKlicService;

    @Autowired
    private KlicService klicService;

    @Autowired
    private UzivatelServices uzivatelServices;

    @PostMapping("/klic/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_KLICU')")
    public ResponseEntity<KlicDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
                @RequestBody @Valid KlicDto klicDto) throws InterruptedException, ExecutionException, RecordNotFoundException, NoSuchMessageException {
            // Je nutné provádět asynchronně, jinak dochází k nekonzistenci dat 
            CompletableFuture<Klic> oldKlicFuture = CompletableFuture.supplyAsync(() -> {
                if (klicDto.getId() != null) {
                    Klic klic = new Klic();
                    try {
                        klic = klicService.getDetail(klicDto.getId());
                    } catch (RecordNotFoundException | NoSuchMessageException e) {
                        e.printStackTrace();
                    }
                    return klic;
                } else {
                    return new Klic();
                }
            });
    
            CompletableFuture<Klic> newKlicFuture = oldKlicFuture.thenApplyAsync(oldKlic -> {
                try {
                    return klicService.createKey(klicDto.toEntity(), appUserDto);
                    
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            });
    
            Klic oldKlic = oldKlicFuture.get();
            Klic newKlic = newKlicFuture.get();
    

            Uzivatel uzivatelAkce = uzivatelServices.getDetail(appUserDto.getIdUzivatel());

            historieKlicService.create(newKlic, oldKlic, uzivatelAkce);


            return ResponseEntity.ok(new KlicDto(newKlic));
    }

    @GetMapping("/klic/list")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<List<KlicDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable Boolean specialni) throws RecordNotFoundException, NoSuchMessageException {
        List<KlicDto> result = new ArrayList<KlicDto>();
        List<Klic> list = klicService.getList(aktivni, specialni, appUserDto);

        if (list != null && list.size() > 0) {
            for (Klic klic : list) {
                result.add(new KlicDto(klic));
            }
        }

        return ResponseEntity.ok(result);
    }


    @GetMapping("/klic/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_KLICU')")
    public ResponseEntity<KlicDto> getDetail(@RequestParam String idKey) throws RecordNotFoundException, NoSuchMessageException {
        Klic key = klicService.getDetail(idKey);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new KlicDto(key));
    }
}
