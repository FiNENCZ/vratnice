package cz.dp.share.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.component.ResourcesComponent;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.ExterniRoleDto;
import cz.dp.share.dto.ExterniUzivatelDto;
import cz.dp.share.entity.ExterniRole;
import cz.dp.share.entity.ExterniUzivatel;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.repository.ExterniRoleRepository;
import cz.dp.share.services.ExterniUzivatelServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class ExterniUzivatelController extends BaseController {

	final static Logger logger = LogManager.getLogger(ExterniUzivatelController.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ExterniUzivatelServices externiUzivatelServices;

	@Autowired
	private ExterniRoleRepository externiRoleRepository;

	@Autowired
	private ResourcesComponent resourcesComponent;

	@GetMapping("/externi-uzivatel/detail")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_EXTERNICH_UZIVATELU')")
	public ExterniUzivatelDto detail(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam String id) {
		try {

			ExterniUzivatel externiUzivatel = externiUzivatelServices.getDetail(id);

			if (externiUzivatel == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

			ExterniUzivatelDto externiUzivatelDto = new ExterniUzivatelDto(externiUzivatel);

			return externiUzivatelDto;
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

	@GetMapping("/externi-uzivatel/role/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_EXTERNICH_UZIVATELU')")
	public List<ExterniRoleDto> listRole(HttpServletRequest request) {

		List<ExterniRoleDto> result = new ArrayList<ExterniRoleDto>();
		try {
			List<ExterniRole> list = externiRoleRepository.findAll();
			if (list != null && list.size() > 0) {
				for (ExterniRole externiRole : list) {
					externiRole.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
							externiRole.getNazevResx()));
					result.add(new ExterniRoleDto(externiRole));
				}
			}
		} catch (RecordNotFoundException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

		return result;
	}

	@GetMapping("/externi-uzivatel/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_EXTERNICH_UZIVATELU')")
	public List<ExterniUzivatelDto> list(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) {

		try {
			List<ExterniUzivatelDto> result = new ArrayList<ExterniUzivatelDto>();

			List<ExterniUzivatel> uzivatele = externiUzivatelServices.getList();

			if (uzivatele != null && uzivatele.size() > 0) {
				for (ExterniUzivatel externiUzivatel : uzivatele) {
					result.add(new ExterniUzivatelDto(externiUzivatel));
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

	@GetMapping("/externi-uzivatel/exists-by-username")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_EXTERNICH_UZIVATELU')")
	public boolean existsByUsername(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam String username) {

		return externiUzivatelServices.exists(username);
	}

	@PostMapping("/externi-uzivatel/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_EXTERNICH_UZIVATELU')")
	public ExterniUzivatelDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestBody @Valid ExterniUzivatelDto detail) {

		try {

			ExterniUzivatel externiUzivatelPuv = null;

			if (!StringUtils.isBlank(detail.getId())) {
				externiUzivatelPuv = externiUzivatelServices.getDetail(detail.getId());
				if (externiUzivatelPuv == null)
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
			}

			ExterniUzivatel externiuzivatel = detail.getExterniUzivatel(externiUzivatelPuv, false);
			externiuzivatel = externiUzivatelServices.save(externiuzivatel);
			entityManager.clear();
			externiuzivatel = externiUzivatelServices.getDetail(externiuzivatel.getIdExterniUzivatel());
			return new ExterniUzivatelDto(externiuzivatel);
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

}
