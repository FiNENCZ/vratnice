package cz.diamo.share.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import cz.diamo.share.dto.Wso2UzivatelExtDto;
import cz.diamo.share.dto.opravneni.FilterOpravneniDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.enums.RoleEnum;
import cz.diamo.share.exceptions.AccessDeniedException;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.repository.ZavodRepository;
import cz.diamo.share.services.OpravneniServices;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.share.services.Wso2Services;
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

	@Autowired
	private OpravneniServices opravneniServices;

	@Autowired
	private UzivatelRepository uzivatelRepository;

	@Autowired
	private ZavodRepository zavodRepository;

	@Autowired
	private Wso2Services wso2Services;

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/uzivatel/detail")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_UZIVATELU', 'ROLE_SPRAVA_UZIVATELU_EXT')")
	public UzivatelDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String id) {
		try {

			List<RoleEnum> role = Arrays
					.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_UZIVATELU, RoleEnum.ROLE_SPRAVA_UZIVATELU_EXT });
			if (!opravneniServices.jePodrizeny(new FilterOpravneniDto(appUserDto.getIdUzivatel(), role),
					id))
				throw new AccessDeniedException(
						messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));

			Uzivatel uzivatel = uzivatelServices.getDetail(id);
			if (uzivatel == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

			boolean canEdit = true;
			role = Arrays
					.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_UZIVATELU_EXT });
			if (uzivatel.getExterni()
					&& !opravneniServices.jePodrizeny(new FilterOpravneniDto(appUserDto.getIdUzivatel(), role),
							uzivatel.getIdUzivatel()))
				canEdit = false;
			role = Arrays
					.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_UZIVATELU });
			if (!uzivatel.getExterni()
					&& !opravneniServices.jePodrizeny(new FilterOpravneniDto(appUserDto.getIdUzivatel(), role),
							uzivatel.getIdUzivatel()))
				canEdit = false;

			UzivatelDto uzivatelDto = new UzivatelDto(uzivatel);
			uzivatelDto.setCanEdit(canEdit);

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
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_UZIVATELU', 'ROLE_SPRAVA_UZIVATELU_EXT')")
	public List<UzivatelDto> list(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam @Nullable Boolean aktivni) {

		try {

			List<UzivatelDto> result = new ArrayList<UzivatelDto>();
			List<RoleEnum> role = Arrays
					.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_UZIVATELU, RoleEnum.ROLE_SPRAVA_UZIVATELU_EXT });
			List<Uzivatel> list = uzivatelServices.getList(null,
					new FilterOpravneniDto(appUserDto.getIdUzivatel(), role), aktivni);
			if (list != null && list.size() > 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -2);
				Date date = Utils.odstranitCas(calendar.getTime());
				for (Uzivatel uzivatel : list) {
					UzivatelDto uzivatelDto = new UzivatelDto(uzivatel);

					// varování
					if (!uzivatel.getExterni() && uzivatel.getPlatnostKeDni() == null
							|| uzivatel.getPlatnostKeDni().compareTo(date) == -1
									&& (uzivatel.getDatumDo() == null || uzivatel.getDatumDo().compareTo(date) == 1)) {
						uzivatelDto.setVarovani(true);
						uzivatelDto.setVarovaniText(
								String.format(
										messageSource.getMessage("uzivatel.platnost.ke.dni.varovani", null,
												LocaleContextHolder.getLocale()),
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
	public List<UzivatelDto> listDleOpravneni(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam List<RoleEnum> role) {

		try {

			// kontrola rolí
			List<RoleEnum> roleNew = new ArrayList<RoleEnum>();
			for (RoleEnum roleEnum : role) {
				if (appUserDto.testAuthority(roleEnum)) {
					roleNew.add(roleEnum);
				}
				// throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				// messageSource.getMessage("record.access.denied", null,
				// LocaleContextHolder.getLocale()));
			}

			List<UzivatelDto> result = new ArrayList<UzivatelDto>();
			if (roleNew.size() == 0)
				return result;
			List<Uzivatel> list = uzivatelServices.getList(appUserDto.getZavod().getId(),
					new FilterOpravneniDto(appUserDto.getIdUzivatel(), roleNew));
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
	public List<UzivatelDto> listDleOpravneniCelyPodnik(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam List<RoleEnum> role) {

		try {

			// kontrola rolí
			List<RoleEnum> roleNew = new ArrayList<RoleEnum>();
			for (RoleEnum roleEnum : role) {
				if (appUserDto.testAuthority(roleEnum)) {
					roleNew.add(roleEnum);
				}
				// throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				// messageSource.getMessage("record.access.denied", null,
				// LocaleContextHolder.getLocale()));
			}

			List<UzivatelDto> result = new ArrayList<UzivatelDto>();
			if (roleNew.size() == 0)
				return result;
			List<Uzivatel> list = uzivatelServices.getList(null,
					new FilterOpravneniDto(appUserDto.getIdUzivatel(), roleNew));
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
	public List<UzivatelDto> listAll(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
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
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_UZIVATELU', 'ROLE_SPRAVA_UZIVATELU_EXT')")
	public UzivatelDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestBody @Valid UzivatelDto detail) {
		try {

			Uzivatel uzivatelPuv = null;
			if (!StringUtils.isBlank(detail.getId())) {
				uzivatelPuv = uzivatelRepository.getDetail(detail.getId());
				if (uzivatelPuv == null)
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

				List<RoleEnum> role = Arrays
						.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_UZIVATELU });
				if (!uzivatelPuv.getExterni()
						&& !opravneniServices.jePodrizeny(new FilterOpravneniDto(appUserDto.getIdUzivatel(), role),
								uzivatelPuv.getIdUzivatel()))
					throw new AccessDeniedException(
							messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));

				role = Arrays
						.asList(new RoleEnum[] { RoleEnum.ROLE_SPRAVA_UZIVATELU_EXT });
				if (uzivatelPuv.getExterni()
						&& !opravneniServices.jePodrizeny(new FilterOpravneniDto(appUserDto.getIdUzivatel(), role),
								uzivatelPuv.getIdUzivatel()))
					throw new AccessDeniedException(
							messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));

			} else {
				if (!detail.getExterni())
					throw new AccessDeniedException(
							messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));

				if (!appUserDto.testAuthority(RoleEnum.ROLE_SPRAVA_UZIVATELU_EXT))
					throw new AccessDeniedException(
							messageSource.getMessage("record.access.denied", null, LocaleContextHolder.getLocale()));
			}

			Uzivatel uzivatel = uzivatelServices.save(detail.getUzivatel(uzivatelPuv, appUserDto, false), false, true,
					false);
			// entityManager.detach(uzivatel);

			// uzivatel = uzivatelServices.getDetail(uzivatel.getIdUzivatel());
			return new UzivatelDto(uzivatel);
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@PostMapping("/uzivatel/externi/synchronizace")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_UZIVATELU_EXT')")
	public void uzivatelExterniSynchronizace(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) {
		try {

			// seznam externích uživatelů
			List<Uzivatel> listUzivatel = uzivatelServices.getList(null, null, null, null, true);

			if (listUzivatel != null && listUzivatel.size() > 0) {
				List<Wso2UzivatelExtDto> listWso2UzivatelExtDto = new ArrayList<Wso2UzivatelExtDto>();

				for (Uzivatel uzivatel : listUzivatel) {
					uzivatel.setZavod(zavodRepository.getDetail(uzivatel.getZavod().getIdZavod()));
					if (uzivatel.getOstatniZavody() != null) {
						for (Zavod zavod : uzivatel.getOstatniZavody()) {
							Zavod z = zavodRepository.getDetail(zavod.getIdZavod());
							zavod.setSapId(z.getSapId());
							zavod.setNazev(z.getNazev());
						}
					}
					listWso2UzivatelExtDto.add(new Wso2UzivatelExtDto(uzivatel));

				}
				wso2Services.uzivateleExterni(listWso2UzivatelExtDto);
			}

		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}
}
