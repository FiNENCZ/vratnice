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

import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.OpravneniTypPristupuBudovaDto;
import cz.diamo.share.dto.OpravneniTypPristupuDto;
import cz.diamo.share.dto.PracovniPoziceNodeDto;
import cz.diamo.share.dto.opravneni.OpravneniDto;
import cz.diamo.share.dto.opravneni.OpravneniPrehledDto;
import cz.diamo.share.dto.opravneni.RoleDto;
import cz.diamo.share.entity.Opravneni;
import cz.diamo.share.entity.OpravneniTypPristupu;
import cz.diamo.share.entity.OpravneniTypPristupuBudova;
import cz.diamo.share.entity.Role;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.repository.OpravneniTypPristupuBudovaRepository;
import cz.diamo.share.repository.OpravneniTypPristupuRepository;
import cz.diamo.share.repository.RoleRepository;
import cz.diamo.share.services.OpravneniServices;
import cz.diamo.share.services.PracovniPoziceServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class OpravneniController extends BaseController {

	final static Logger logger = LogManager.getLogger(OpravneniController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private OpravneniServices opravneniServices;

	@Autowired
	private PracovniPoziceServices pracovniPoziceServices;

	@Autowired
	private OpravneniTypPristupuRepository opravneniTypPristupuRepository;

	@Autowired
	private OpravneniTypPristupuBudovaRepository opravneniTypPristupuBudovaRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ResourcesComponent resourcesComponent;

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/opravneni/detail")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public OpravneniDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request, @RequestParam String id) {
		try {
			Opravneni opravneni = opravneniServices.getDetail(id);
			if (opravneni == null)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
			OpravneniDto opravneniDto = new OpravneniDto(opravneni, messageSource, true, true);
			return opravneniDto;
		} catch (RecordNotFoundException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@GetMapping("/opravneni/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public List<OpravneniDto> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request,
			@RequestParam @Nullable Boolean aktivita, @RequestParam @Nullable String idZavodu) {
		try {
			List<OpravneniDto> result = new ArrayList<OpravneniDto>();
			List<Opravneni> list = opravneniServices.getList(idZavodu, aktivita, true, true);
			if (list != null && list.size() > 0) {
				for (Opravneni opravneni : list) {
					result.add(new OpravneniDto(opravneni, messageSource, true, true));
				}
			}

			return result;

		} catch (RecordNotFoundException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@GetMapping("/opravneni/role/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public List<RoleDto> roleList(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request) {
		try {
			List<RoleDto> result = new ArrayList<RoleDto>();
			List<Role> list = roleRepository.findAll();
			if (list != null && list.size() > 0) {
				for (Role role : list) {
					role.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), role.getNazevResx()));
					result.add(new RoleDto(role));
				}
			}

			return result;

		} catch (RecordNotFoundException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@GetMapping("/opravneni/prehled")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public List<OpravneniPrehledDto> prehled(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request,
			@RequestParam @Nullable String idZavodu, @RequestParam @Nullable Boolean aktivita) {
		try {
			return opravneniServices.getListPrehled(idZavodu, aktivita);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@GetMapping("/opravneni/typ-pristupu")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public List<OpravneniTypPristupuDto> listTypPristupu(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request) {
		try {
			List<OpravneniTypPristupuDto> result = new ArrayList<OpravneniTypPristupuDto>();

			List<OpravneniTypPristupu> list = opravneniTypPristupuRepository.findAll();
			for (OpravneniTypPristupu opravneniTypPristupu : list) {
				opravneniTypPristupu.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), opravneniTypPristupu.getNazevResx()));

				result.add(new OpravneniTypPristupuDto(opravneniTypPristupu));
			}
			return result;
		} catch (RecordNotFoundException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@GetMapping("/opravneni/typ-pristupu-budova")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public List<OpravneniTypPristupuBudovaDto> listTypPristupuBudova(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request) {
		try {
			List<OpravneniTypPristupuBudovaDto> result = new ArrayList<OpravneniTypPristupuBudovaDto>();

			List<OpravneniTypPristupuBudova> list = opravneniTypPristupuBudovaRepository.findAll();
			for (OpravneniTypPristupuBudova opravneniTypPristupuBudova : list) {
				opravneniTypPristupuBudova
						.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), opravneniTypPristupuBudova.getNazevResx()));

				result.add(new OpravneniTypPristupuBudovaDto(opravneniTypPristupuBudova));
			}
			return result;
		} catch (RecordNotFoundException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@GetMapping("/opravneni/pracovni-pozice/strom")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public List<PracovniPoziceNodeDto> stromPracovniPozice(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request) {
		try {

			return pracovniPoziceServices.getStromPracovniPozice(null, false);

		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@PostMapping("/opravneni/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public OpravneniDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody @Valid OpravneniDto detail) {
		try {

			Opravneni opravneni = opravneniServices.save(detail.getOpravneni(null, false));
			entityManager.clear();
			opravneni = opravneniServices.getDetail(opravneni.getIdOpravneni());
			return new OpravneniDto(opravneni, messageSource, true, true);
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@PostMapping("/opravneni/hromadne-pridat-zavod")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public void hromadnePridatZavod(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam String idZavodu,
			@RequestBody List<String> idZaznamu) {
		try {
			if (idZaznamu != null && idZaznamu.size() > 0) {
				for (String id : idZaznamu) {
					Opravneni opravneni = opravneniServices.getDetail(id);
					if (opravneni != null) {
						boolean exists = false;
						if (opravneni.getZavody() != null && opravneni.getZavody().size() > 0) {
							for (Zavod zavod : opravneni.getZavody()) {
								if (zavod.getIdZavod().equals(idZavodu)) {
									exists = true;
									break;
								}
							}
						}

						if (!exists) {
							if (opravneni.getZavody() == null)
								opravneni.setZavody(new ArrayList<Zavod>());
							opravneni.getZavody().add(new Zavod(idZavodu));
							opravneniServices.save(opravneni);
						}
					}
				}
			}
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@PostMapping("/opravneni/hromadne-odebrat-zavod")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_OPRAVNENI')")
	public void hromadneOdebratZavod(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestParam String idZavodu,
			@RequestBody List<String> idZaznamu) {
		try {
			if (idZaznamu != null && idZaznamu.size() > 0) {
				for (String id : idZaznamu) {
					Opravneni opravneni = opravneniServices.getDetail(id);
					if (opravneni != null) {
						Zavod odebrat = null;
						if (opravneni.getZavody() != null && opravneni.getZavody().size() > 0) {
							for (Zavod zavod : opravneni.getZavody()) {
								if (zavod.getIdZavod().equals(idZavodu)) {
									odebrat = zavod;
									break;
								}
							}
						}

						if (odebrat != null) {
							opravneni.getZavody().remove(odebrat);
							opravneniServices.save(opravneni);
						}
					}
				}
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
