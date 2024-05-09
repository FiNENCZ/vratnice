package cz.diamo.share.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
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
import cz.diamo.share.dto.ZastupDto;
import cz.diamo.share.dto.opravneni.FilterOpravneniDto;
import cz.diamo.share.entity.Zastup;
import cz.diamo.share.enums.RoleEnum;
import cz.diamo.share.exceptions.AccessDeniedException;
import cz.diamo.share.services.OpravneniServices;
import cz.diamo.share.services.ZastupServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ZastupController extends BaseController {

	final static Logger logger = LogManager.getLogger(ZastupController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ZastupServices zastupServices;

	@Autowired
	private OpravneniServices opravneniServices;

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/zastup/detail")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_ZASTUPU')")
	public ZastupDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String guid) {
		try {
			Zastup zastup = zastupServices.getDetail(guid);
			if (zastup == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
			ZastupDto zastupDto = new ZastupDto(zastup);
			return zastupDto;
		} catch (ResponseStatusException re) {
			logger.error(re);
			throw re;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@GetMapping("/zastup/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_ZASTUPU')")
	public List<ZastupDto> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam @Nullable Date datumOd,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam @Nullable Date datumDo,
			@RequestParam @Nullable String idUzivatel, @RequestParam @Nullable String idUzivatelZastupce,
			@RequestParam @Nullable Boolean aktivni) {
		try {
			List<ZastupDto> result = new ArrayList<ZastupDto>();

			List<RoleEnum> role = Arrays
					.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_ZASTUPU });
			List<Zastup> list = zastupServices.getList(null, datumOd, datumDo, idUzivatel,
					idUzivatelZastupce, aktivni,
					new FilterOpravneniDto(appUserDto.getIdUzivatel(), role));
			if (list != null && list.size() > 0) {
				for (Zastup zastup : list) {
					result.add(new ZastupDto(zastup));
				}
			}

			return result;
		} catch (ResponseStatusException re) {
			logger.error(re);
			throw re;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@PostMapping("/zastup/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_ZASTUPU')")
	public ZastupDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestBody @Valid ZastupDto detail) {
		try {
			Zastup zastup = null;
			if (!StringUtils.isBlank(detail.getGuid())) {
				zastup = zastupServices.getDetail(detail.getGuid());
				if (zastup == null)
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
			}

			// kontrola přístupu
			List<RoleEnum> role = Arrays
					.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_ZASTUPU });
			if (!opravneniServices.jePodrizeny(new FilterOpravneniDto(appUserDto.getIdUzivatel(), role),
					detail.getUzivatel().getId()))
				throw new AccessDeniedException(
						messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));
			if (!opravneniServices.jePodrizeny(new FilterOpravneniDto(appUserDto.getIdUzivatel(), role),
					detail.getUzivatelZastupce().getId()))
				throw new AccessDeniedException(
						messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));

			zastup = zastupServices.save(detail.getZastup(zastup), true);
			entityManager.clear();
			zastup = zastupServices.getDetail(zastup.getGuid());
			return new ZastupDto(zastup);
		} catch (ResponseStatusException re) {
			logger.error(re);
			throw re;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@PostMapping("/zastup/synchronizace")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE')")
	public void synchronizace(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) {
		try {
			String chyba = zastupServices.synchronizovatVse(appUserDto.getZavod().getId());

			if (!StringUtils.isBlank(chyba))
				throw new Exception(chyba);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

}
