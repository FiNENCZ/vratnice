package cz.diamo.share.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import cz.diamo.share.base.Utils;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.dto.opravneni.FilterOpravneniDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.enums.RoleEnum;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.services.UzivatelServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class UzivatelController extends BaseController {

	final static Logger logger = LogManager.getLogger(UzivatelController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UzivatelServices uzivatelServices;

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/uzivatel/detail")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_UZIVATELU')")
	public UzivatelDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request, @RequestParam String id) {
		try {

			Uzivatel uzivatel = uzivatelServices.getDetail(id);
			if (uzivatel == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

			UzivatelDto uzivatelDto = new UzivatelDto(uzivatel);

			return uzivatelDto;

		} catch (ResponseStatusException e) {
			logger.error(e);
			throw e;
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@GetMapping("/uzivatel/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_UZIVATELU')")
	public List<UzivatelDto> list(HttpServletRequest request, @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam @Nullable Boolean aktivni) {

		try {

			List<UzivatelDto> result = new ArrayList<UzivatelDto>();
			List<RoleEnum> role = Arrays.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_UZIVATELU });
			List<Uzivatel> list = uzivatelServices.getList(null, new FilterOpravneniDto(appUserDto.getIdUzivatel(), role));
			if (list != null && list.size() > 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -2);
				Date date = Utils.odstranitCas(calendar.getTime());
				for (Uzivatel uzivatel : list) {
					UzivatelDto uzivatelDto = new UzivatelDto(uzivatel);

					// varování
					if (uzivatel.getPlatnostKeDni() == null || uzivatel.getPlatnostKeDni().compareTo(date) == -1
							&& (uzivatel.getDatumDo() == null || uzivatel.getDatumDo().compareTo(date) == 1)) {
						uzivatelDto.setVarovani(true);
						uzivatelDto.setVarovaniText(
								String.format(messageSource.getMessage("uzivatel.platnost.ke.dni.varovani", null, LocaleContextHolder.getLocale()),
										Utils.dateToString(uzivatelDto.getPlatnostKeDni())));
					}

					result.add(uzivatelDto);
				}
			}

			return result;
		} catch (ResponseStatusException e) {
			logger.error(e);
			throw e;
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@GetMapping("/uzivatel/list-dle-opravneni")
	@PreAuthorize("isFullyAuthenticated()")
	public List<UzivatelDto> listDleOpravneni(HttpServletRequest request, @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam List<RoleEnum> role) {

		try {

			// kontrola rolí
			for (RoleEnum roleEnum : role) {
				if (!appUserDto.testAuthority(roleEnum))
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));
			}

			List<UzivatelDto> result = new ArrayList<UzivatelDto>();
			List<Uzivatel> list = uzivatelServices.getList(appUserDto.getZavod().getId(), new FilterOpravneniDto(appUserDto.getIdUzivatel(), role));
			if (list != null && list.size() > 0) {
				for (Uzivatel uzivatel : list) {
					result.add(new UzivatelDto(uzivatel));
				}
			}

			return result;
		} catch (ResponseStatusException e) {
			logger.error(e);
			throw e;
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@GetMapping("/uzivatel/list-dle-opravneni-cely-podnik")
	@PreAuthorize("isFullyAuthenticated()")
	public List<UzivatelDto> listDleOpravneniCelyPodnik(HttpServletRequest request, @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam List<RoleEnum> role) {

		try {

			// kontrola rolí
			for (RoleEnum roleEnum : role) {
				if (!appUserDto.testAuthority(roleEnum))
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));
			}

			List<UzivatelDto> result = new ArrayList<UzivatelDto>();
			List<Uzivatel> list = uzivatelServices.getList(null, new FilterOpravneniDto(appUserDto.getIdUzivatel(), role));
			if (list != null && list.size() > 0) {
				for (Uzivatel uzivatel : list) {
					result.add(new UzivatelDto(uzivatel));
				}
			}

			return result;
		} catch (ResponseStatusException e) {
			logger.error(e);
			throw e;
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@GetMapping("/uzivatel/list-all")
	@PreAuthorize("isFullyAuthenticated()")
	public List<UzivatelDto> listAll(HttpServletRequest request, @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam Boolean vsechnyZavody) {

		try {

			List<UzivatelDto> result = new ArrayList<UzivatelDto>();
			String idZavodu = appUserDto.getZavod().getId();
			if (vsechnyZavody)
				idZavodu = null;
			List<Uzivatel> list = uzivatelServices.getList(idZavodu, null);
			if (list != null && list.size() > 0) {
				for (Uzivatel uzivatel : list) {
					result.add(new UzivatelDto(uzivatel));
				}
			}

			return result;
		} catch (ResponseStatusException e) {
			logger.error(e);
			throw e;
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@PostMapping("/uzivatel/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_UZIVATELU')")
	public UzivatelDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid UzivatelDto detail) {
		try {

			Uzivatel uzivatel = uzivatelServices.save(detail.getUzivatel(null, false), false, true, false);
			entityManager.detach(uzivatel);

			uzivatel = uzivatelServices.getDetail(uzivatel.getIdUzivatel());
			return new UzivatelDto(uzivatel);
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}
}
