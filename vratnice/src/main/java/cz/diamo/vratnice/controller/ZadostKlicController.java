package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.vratnice.dto.ZadostKlicDto;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.service.KlicService;
import cz.diamo.vratnice.service.ZadostKlicService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ZadostKlicController extends BaseController{

    final static Logger logger = LogManager.getLogger(ZadostKlicController.class);
    
    private int MAX_POCET_VYPUJCEK = 15;

    @Autowired
    private ZadostKlicService zadostKlicService;

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private KlicService klicService;

    @Autowired
    private MessageSource messageSource;


    @PostMapping("/zadost-klic/save")
    public ResponseEntity<?> saveKey(@RequestBody @Valid ZadostKlicDto zadostKlicDto) {
        // kontrola zda nebylo přesaženo max počtu výpůjček
        ZadostKlic zadostKlicEntity = zadostKlicDto.toEntity();
        Uzivatel uzivatel = zadostKlicEntity.getUzivatel();
        long pocetVypujcek = zadostKlicService.countByUzivatel(uzivatel);
        int dostupnychVypujcek = MAX_POCET_VYPUJCEK - (int) pocetVypujcek;

        // úprava existující žádosti
        String idZadostiKey = zadostKlicDto.getIdZadostiKey();
        if (idZadostiKey != null && !idZadostiKey.isEmpty()) {
            ZadostKlic newZadostKlic = zadostKlicService.create(zadostKlicEntity);
            return ResponseEntity.ok(new ZadostKlicDto(newZadostKlic));
        }

        // nová žádost
        if(dostupnychVypujcek > 0) {
            //vytvoření záznamu žádosti klíče
            ZadostKlic newZadostKlic = zadostKlicService.create(zadostKlicEntity);
            return ResponseEntity.ok(new ZadostKlicDto(newZadostKlic));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Žádost nelze provést, protože bylo dosaženo maximálního počtu žádostí o klíče.");
        }
    }

    @PostMapping("/zadost-klic/zmena-stavu")
    public ResponseEntity<ZadostKlicDto> zmenaStavuZadosti(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid ZadostKlicDto zadostKlicDto, @RequestParam String stav) {

        zadostKlicDto.setStav(stav);
        ZadostKlic newZadostKlic = zadostKlicService.create(zadostKlicDto.toEntity());
        return ResponseEntity.ok(new ZadostKlicDto(newZadostKlic));
    }
    

    @GetMapping("/zadosti-klic/list")
    public ResponseEntity<List<ZadostKlicDto>> list(@RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idUzivatel ) {
        List<ZadostKlicDto> result = new ArrayList<ZadostKlicDto>();
        List<ZadostKlic> list = zadostKlicService.getList(aktivni, idUzivatel);

        if (list != null && list.size() > 0) {
            for (ZadostKlic zadostKlic : list) {
                
                Boolean dostupny = klicService.jeDostupny(zadostKlic.getKlic().getIdKlic());
                ZadostKlicDto zadostKlicDto = new ZadostKlicDto(zadostKlic);
                zadostKlicDto.setJeKlicDostupny(dostupny);

                result.add(zadostKlicDto);
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/zadosti-klic/detail")
    public ResponseEntity<ZadostKlicDto> getDetail(@RequestParam String idZadostKey) {
        ZadostKlic zadostKlic = zadostKlicService.getDetail(idZadostKey);
        if (zadostKlic == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ZadostKlicDto(zadostKlic));
    }

    @GetMapping("/zadost-klic/zadosti-dle-stavu")
    public  ResponseEntity<List<ZadostKlicDto>> getZadostiByKlic(@RequestParam String stav) {
        List<ZadostKlicDto> zadostiKlic = zadostKlicService.getZadostiByStav(stav).stream()
            .map(ZadostKlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(zadostiKlic);
    }

    @GetMapping("/zadost-klic/list-by-id-uzivatel")
    public ResponseEntity<List<ZadostKlicDto>> listByIdUzivatel(@RequestParam String idUzivatel) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(idUzivatel);

        if (uzivatel == null) {
            return ResponseEntity.notFound().build();
        }

        List<ZadostKlicDto> zadostiKlic = zadostKlicService.findByUzivatel(uzivatel).stream()
            .map(ZadostKlicDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(zadostiKlic);

    }

    @GetMapping("/zadost-klic/vypujcek-k-dispozici")
    public ResponseEntity<Integer> getPocetVypujcekByUzivatel(@RequestParam String idUzivatel) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelServices.getDetail(idUzivatel);
        if (uzivatel == null) {
            return ResponseEntity.notFound().build();
        }
        long pocetVypujcek = zadostKlicService.countByUzivatel(uzivatel);

        int dostupnychVypujcek = MAX_POCET_VYPUJCEK - (int) pocetVypujcek;


        return ResponseEntity.ok(dostupnychVypujcek);
    }

    @GetMapping("/zadost-klic/get-uzivatel-by-rfid")
    public ResponseEntity<UzivatelDto> getUzivatelByRfid(@RequestParam String rfid) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelRepository.getDetailByRfid(rfid);

        if (uzivatel == null || uzivatel.getIdUzivatel() == null) {
            throw new RecordNotFoundException(
                String.format(messageSource.getMessage("zadost_klic.uzivatel_rfid", null, LocaleContextHolder.getLocale())));
        }

        return ResponseEntity.ok(new UzivatelDto(uzivatel));
    }
    
    
    

}
