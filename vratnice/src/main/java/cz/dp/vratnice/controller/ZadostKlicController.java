package cz.dp.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.UzivatelDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.exceptions.AccessDeniedException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.exceptions.ValidationException;
import cz.dp.share.repository.UzivatelRepository;
import cz.dp.vratnice.dto.ZadostKlicDto;
import cz.dp.vratnice.entity.ZadostKlic;
import cz.dp.vratnice.enums.ZadostStavEnum;
import cz.dp.vratnice.service.KlicService;
import cz.dp.vratnice.service.ZadostKlicService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ZadostKlicController extends BaseController{

    final static Logger logger = LogManager.getLogger(ZadostKlicController.class);
    @Autowired
    private ZadostKlicService zadostKlicService;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private KlicService klicService;

    @Autowired
    private MessageSource messageSource;


    @PostMapping("/zadost-klic/save")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_ZADOSTI_KLICU')")
    public ResponseEntity<ZadostKlicDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, 
        @RequestBody @Valid ZadostKlicDto zadostKlicDto) throws ValidationException, RecordNotFoundException, NoSuchMessageException, AccessDeniedException {

        ZadostKlic newZadostKlic = zadostKlicService.save(zadostKlicDto.toEntity(), appUserDto);
        return ResponseEntity.ok(new ZadostKlicDto(newZadostKlic));
    }


    @GetMapping("/zadosti-klic/list")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<List<ZadostKlicDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
        @RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idUzivatel, @RequestParam @Nullable ZadostStavEnum zadostStavEnum ) throws RecordNotFoundException, NoSuchMessageException {

        List<ZadostKlicDto> result = new ArrayList<ZadostKlicDto>();
        List<ZadostKlic> list = zadostKlicService.getList(aktivni, idUzivatel, zadostStavEnum, appUserDto);

        if (list != null && list.size() > 0) {
            for (ZadostKlic zadostKlic : list) {
                
                Boolean dostupny = klicService.jeDostupny(zadostKlic);
                ZadostKlicDto zadostKlicDto = new ZadostKlicDto(zadostKlic);
                zadostKlicDto.setJeKlicDostupny(dostupny);

                result.add(zadostKlicDto);
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/zadosti-klic/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_ZADOSTI_KLICU')")
    public ResponseEntity<ZadostKlicDto> getDetail(@RequestParam String idZadostKey) throws RecordNotFoundException, NoSuchMessageException {
        ZadostKlic zadostKlic = zadostKlicService.getDetail(idZadostKey);
        if (zadostKlic == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ZadostKlicDto(zadostKlic));
    }


    @GetMapping("/zadost-klic/get-uzivatel-by-rfid")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<UzivatelDto> getUzivatelByRfid(@RequestParam String rfid) throws RecordNotFoundException, NoSuchMessageException {
        Uzivatel uzivatel = uzivatelRepository.getDetailByRfid(rfid);

        if (uzivatel == null || uzivatel.getIdUzivatel() == null) {
            throw new RecordNotFoundException(
                String.format(messageSource.getMessage("zadost_klic.uzivatel_rfid", null, LocaleContextHolder.getLocale())));
        }

        return ResponseEntity.ok(new UzivatelDto(uzivatel));
    }
}
