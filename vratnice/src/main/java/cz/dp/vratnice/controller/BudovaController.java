package cz.dp.vratnice.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.controller.BudovaBaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.BudovaDto;
import cz.dp.share.entity.Budova;
import cz.dp.share.entity.Opravneni;
import cz.dp.share.enums.OpravneniTypPristupuBudovaEnum;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.repository.UzivatelOpravneniRepository;
import cz.dp.share.services.BudovaServices;
import cz.dp.vratnice.service.BudovaVratniceService;
import cz.dp.vratnice.zadosti.services.ZadostiServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/budova")
public class BudovaController extends BudovaBaseController {

	final static Logger logger = LogManager.getLogger(BudovaController.class);

	@Autowired
	private BudovaServices budovaServices;

	@Autowired
	private ZadostiServices zadostiServices;

	@Autowired
	private UzivatelOpravneniRepository uzivatelOpravneniRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private BudovaVratniceService budovaVratniceService;

	@PostMapping("/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_BUDOV')")
	public BudovaDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request,
			@RequestBody @Valid BudovaDto detail) {
		try {
			Budova budova = budovaServices.save(detail.getBudova(null, false));
            zadostiServices.saveBudova(new BudovaDto(budova), request, appUserDto);

			entityManager.detach(budova);
			budova = budovaServices.getDetail(budova.getIdBudova());
			return new BudovaDto(budova);
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	/**
	 * Odstranění záznamu
	 * 
	 * @param appUserDto Uživatel
	 * @param idLokalita   id záznamu
	 */
	@PostMapping("/delete")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_BUDOV')")
	public void delete(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam String idLokalita) {
		try {
			budovaServices.odstranit(idLokalita);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	/**
	 * Obnovení záznamu
	 * 
	 * @param appUserDto Uživatel
	 * @param idLokalita   id záznamu
	 */
	@PostMapping("/restore")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_BUDOV')")
	public void restore(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam String idLokalita) {
		try {
			budovaServices.obnovit(idLokalita);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@GetMapping("/list-dle-pristupu")
	@PreAuthorize("isFullyAuthenticated()")
	public ResponseEntity<Set<BudovaDto>> listDlePristupu(
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam @Nullable Boolean aktivita,
			@RequestParam @Nullable String idLokalita) {

		String idUzivatel = appUserDto.getIdUzivatel();

		Set<BudovaDto> resultBudovy = new HashSet<>();
		List<Opravneni> opravneniUzivatele = uzivatelOpravneniRepository.listOpravneni(idUzivatel, true);
	
		if (opravneniUzivatele == null || opravneniUzivatele.isEmpty()) {
			return ResponseEntity.ok(resultBudovy);  // pokud uživatel nemá oprávnění, vracíme prázdný set
		}
	
		for (Opravneni opravneni : opravneniUzivatele) {
			OpravneniTypPristupuBudovaEnum typPristupu = opravneni.getOpravneniTypPristupuBudova().getOpravneniTypPristupuBudovaEnum();
	
			switch (typPristupu) {
				case TYP_PRIST_BUDOVA_OPR_VSE:
					resultBudovy.addAll(budovaVratniceService.listVsechnyBudovy(aktivita, idLokalita));
					break;
	
				case TYP_PRIST_BUDOVA_OPR_ZAVOD:
					resultBudovy.addAll(budovaVratniceService.listBudovyZavodu(opravneni, aktivita, idLokalita));
					break;
	
				case TYP_PRIST_BUDOVA_OPR_VYBER:
					resultBudovy.addAll(budovaVratniceService.listVybraneBudovy(opravneni, aktivita, idLokalita));
					break;
	
				default:
					break;
			}
		}
	
		return ResponseEntity.ok(resultBudovy);
	}

	
}
