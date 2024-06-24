package cz.diamo.share.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.services.ZavodServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ZavodController extends BaseController {

	final static Logger logger = LogManager.getLogger(ZavodController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ZavodServices zavodServices;

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/zavod/detail")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_ZAVODU')")
	public ZavodDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String id) {
		Zavod zavod = zavodServices.getDetail(id);
		if (zavod == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

		ZavodDto zavodDto = new ZavodDto(zavod);
		return zavodDto;
	}

	@GetMapping("/zavod/list")
    @PreAuthorize("isFullyAuthenticated()")
	public List<ZavodDto> list(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idZavodu) {

		List<ZavodDto> result = new ArrayList<ZavodDto>();
		List<Zavod> list = zavodServices.getList(idZavodu, aktivni);
		
		if (list != null && list.size() > 0) {
			for (Zavod zavod : list) {
				result.add(new ZavodDto(zavod));
			}
		}

		return result;
	}

	@GetMapping("/zavod/get-by-nazev")
	public ZavodDto getByNazev(@RequestParam String nazev) {
		Zavod zavodEntity = zavodServices.getByNazev(nazev);
		if (zavodEntity == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
		ZavodDto zavodDto = new ZavodDto(zavodEntity);
		return zavodDto;
	}
	

	@PostMapping("/zavod/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_ZAVODU')")
	public ZavodDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestBody @Valid ZavodDto detail) {
		try {

			Zavod zavod = zavodServices.save(detail.getZavod(null, false));
			entityManager.detach(zavod);
			zavod = zavodServices.getDetail(zavod.getIdZavod());
			return new ZavodDto(zavod);
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

}
