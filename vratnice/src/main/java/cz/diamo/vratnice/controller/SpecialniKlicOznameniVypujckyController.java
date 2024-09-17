package cz.diamo.vratnice.controller;

import java.util.ArrayList;
import java.util.List;

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
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.dto.SpecialniKlicOznameniVypujckyDto;
import cz.diamo.vratnice.entity.SpecialniKlicOznameniVypujcky;
import cz.diamo.vratnice.service.SpecialniKlicOznameniVypujckyService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class SpecialniKlicOznameniVypujckyController extends BaseController {


    final static Logger logger = LogManager.getLogger(NavstevniListekController.class);

    @Autowired
    private SpecialniKlicOznameniVypujckyService specialniKlicOznameniVypujckyService;

    @PostMapping("/specialni-klic-oznameni-vypujcky/save")
    public ResponseEntity<SpecialniKlicOznameniVypujckyDto> save(@RequestBody @Valid SpecialniKlicOznameniVypujckyDto oznameni) throws NoSuchMessageException, BaseException {
        SpecialniKlicOznameniVypujcky specialniKlicOznameniVypujcky = specialniKlicOznameniVypujckyService.save(oznameni.toEntity());
        return ResponseEntity.ok(new SpecialniKlicOznameniVypujckyDto(specialniKlicOznameniVypujcky));
    }

    @GetMapping("/specialni-klic-oznameni-vypujcky/list")
    public ResponseEntity<List<SpecialniKlicOznameniVypujckyDto>> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
                @RequestParam @Nullable Boolean aktivni) throws RecordNotFoundException, NoSuchMessageException {
        List<SpecialniKlicOznameniVypujckyDto> result = new ArrayList<SpecialniKlicOznameniVypujckyDto>();
        List<SpecialniKlicOznameniVypujcky> list = specialniKlicOznameniVypujckyService.getList(aktivni, appUserDto);

        if (list != null && list.size() > 0) {
            for (SpecialniKlicOznameniVypujcky oznameni : list) {
                result.add(new SpecialniKlicOznameniVypujckyDto(oznameni));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/specialni-klic-oznameni-vypujcky/detail")
    public ResponseEntity<SpecialniKlicOznameniVypujckyDto> detail(@RequestParam String idOznameni) {
        SpecialniKlicOznameniVypujcky oznameni = specialniKlicOznameniVypujckyService.getDetail(idOznameni);
        if (oznameni == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new SpecialniKlicOznameniVypujckyDto(oznameni));
    }
}
