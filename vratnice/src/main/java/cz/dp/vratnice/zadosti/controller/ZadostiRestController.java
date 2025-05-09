package cz.dp.vratnice.zadosti.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.dto.AppUserDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.repository.UzivatelRepository;
import cz.dp.share.rest.controller.BaseRestController;
import cz.dp.share.security.UserAuthentication;
import cz.dp.share.services.AuthServices;
import cz.dp.vratnice.dto.KlicDto;
import cz.dp.vratnice.dto.PoschodiDto;
import cz.dp.vratnice.enums.NavstevniListekStavEnum;
import cz.dp.vratnice.service.NavstevniListekUzivatelStavService;
import cz.dp.vratnice.zadosti.dto.ZadostKlicExtDto;
import cz.dp.vratnice.zadosti.services.ZadostiServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Žádost REST - voláno ze žádostí
 */
@RestController
@RequestMapping("zadosti")
public class ZadostiRestController extends BaseRestController {

	final static Logger logger = LogManager.getLogger(ZadostiRestController.class);

	@Autowired
	private ZadostiServices zadostiServices;

	@Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private NavstevniListekUzivatelStavService navstevniListekUzivatelStavService;


	@Autowired
    private AuthServices authServices;

    /**
	 * Seznam poschodi
	 * @param appUserDto
	 * @param idBudova
	 * @return
	 */
	@GetMapping("/poschodi/list")
	@PreAuthorize("isFullyAuthenticated()")
	public List<PoschodiDto> listPoschodi(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam String idBudova) {
        try {
            return zadostiServices.seznamPoschodi(idBudova, appUserDto);
        } catch (BaseException be) {
            logger.error(be);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.toString());
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
	}

    /**
	 * Seznam klíčů
	 * @param appUserDto
	 * @param idBudova
	 * @return
	 */
	@GetMapping("/klic/list")
	@PreAuthorize("isFullyAuthenticated()")
	public List<KlicDto> listKlic(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam String idLokalita,
        @RequestParam String idBudova, @RequestParam String idPoschodi) {
        try {
            return zadostiServices.seznamKlic(idLokalita, idBudova, idPoschodi, appUserDto);
        } catch (BaseException be) {
            logger.error(be);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.toString());
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
	}

    /**
	 * Seznam klíčů
	 * @param appUserDto
	 * @param zadostKlicDto
	 * @return
	 */
	@PostMapping("/klic/pristup")
	@PreAuthorize("isFullyAuthenticated()")
    public void saveZadostKlic(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid ZadostKlicExtDto zadostKlicDto) {
        try {
            zadostiServices.saveZadostKlic(zadostKlicDto, appUserDto);
        } catch (BaseException be) {
            logger.error(be);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.toString());
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

    @PostMapping("/zastup")
    @PreAuthorize("isFullyAuthenticated()")
    public void zastup(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String sapIdZastup) {
        try {

            response.reset();

            // detail uživatele dle SAPID
            String idZastupu = "", idZastupuNew = "";
            Uzivatel uzivatel = uzivatelRepository.getDetailBySapId(sapIdZastup);

            if (uzivatel != null)
                idZastupuNew = uzivatel.getIdUzivatel();
            if (appUserDto.getZastup() != null)
                idZastupu = appUserDto.getZastup().getIdUzivatel();

            if (!idZastupu.equals(idZastupuNew)) {
                ResponseEntity<AppUserDto> responseEntity = authServices.zmenaZastupu(appUserDto,
                        idZastupuNew,
                        request, response);

                if (responseEntity != null && responseEntity.getBody() != null) {
                    Authentication authentication = new UserAuthentication(responseEntity.getBody());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (BaseException be) {
            logger.error(be);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.toString());
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

    @PostMapping("/navstevni-listek/zmenit-stav")
	@PreAuthorize("isFullyAuthenticated()")
    public void zmenitStavNavstevnihoListku(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
            @RequestParam String idNavstevniListek, @RequestParam String idUzivatel, @RequestParam NavstevniListekStavEnum novyStavEnum) {
        try {
            navstevniListekUzivatelStavService.zmenitStav(idNavstevniListek, idUzivatel, novyStavEnum);
        } catch (BaseException be) {
            logger.error(be);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.toString());
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }
}
