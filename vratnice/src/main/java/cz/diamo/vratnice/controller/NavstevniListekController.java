package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import cz.diamo.share.controller.BaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.NavstevniListekDto;
import cz.diamo.vratnice.dto.NavstevniListekTypDto;
import cz.diamo.vratnice.entity.NavstevniListek;
import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.service.NavstevniListekService;
import cz.diamo.vratnice.service.QrCodeService;
import cz.diamo.vratnice.service.UzivatelVratniceService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class NavstevniListekController extends BaseController {

    final static Logger logger = LogManager.getLogger(NavstevniListekController.class);

    @Autowired
    private NavstevniListekService navstevniListekService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;


    @PostMapping("/navstevni-listek/create")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<NavstevniListekDto> save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request,
        @RequestBody @Valid NavstevniListekDto navstevniListekDto) throws NoSuchMessageException, BaseException {

        Vratnice vratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);
        NavstevniListek navstevniListek = navstevniListekService.create(request, appUserDto, navstevniListekDto, vratnice);
        return ResponseEntity.ok(new NavstevniListekDto(navstevniListek));
    }
    

    @GetMapping("/navstevni-listek/list")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<List<NavstevniListekDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
                 @RequestParam @Nullable Boolean aktivni) throws RecordNotFoundException, NoSuchMessageException {
        List<NavstevniListekDto> result = new ArrayList<NavstevniListekDto>();
        List<NavstevniListek> list = navstevniListekService.getList(aktivni, appUserDto);

        if (list != null && list.size() > 0) {
            for (NavstevniListek navstevniListek : list) {
                result.add(new NavstevniListekDto(navstevniListek));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/navstevni-listek/detail")
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<NavstevniListekDto> getDetail(@RequestParam String idNavstevniListek) throws RecordNotFoundException, NoSuchMessageException {
        NavstevniListek navstevniListek = navstevniListekService.getDetail(idNavstevniListek);
        if (navstevniListek == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NavstevniListekDto(navstevniListek));
    }

    @GetMapping("/navstevni-listek/qrcode")
    @ApiResponse(responseCode = "200", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", format = "binary")))
    @PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_NAVSTEVNI_LISTEK')")
    public ResponseEntity<byte[]> getQRCode(@RequestParam String idNavstevniListek) throws RecordNotFoundException, NoSuchMessageException {
        NavstevniListek navstevniListek = navstevniListekService.getDetail(idNavstevniListek);
        if (navstevniListek == null) {
            return ResponseEntity.notFound().build();
        }
    
        try {
            byte[] qrCode = qrCodeService.generateQRCodeImage(navstevniListek);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qrcode.png\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(qrCode);
        } catch (Exception e) {
            // Handle exception appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/navstevni-listek/typ-by-uzivatel")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<NavstevniListekTypDto> typByUzivatele(@RequestParam String idUzivatele) {
        NavstevniListekTyp navstevniListekTypUzivatele = navstevniListekService.getNavstevniListekTypByUzivatel(idUzivatele);
        return ResponseEntity.ok(new NavstevniListekTypDto(navstevniListekTypUzivatele));
    }


    

}
