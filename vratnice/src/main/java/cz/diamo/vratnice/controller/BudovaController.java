package cz.diamo.vratnice.controller;

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

import cz.diamo.share.controller.BudovaBaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.BudovaDto;
import cz.diamo.share.entity.Budova;
import cz.diamo.share.entity.Opravneni;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.enums.OpravneniTypPristupuBudovaEnum;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.repository.OpravneniBudovaRepository;
import cz.diamo.share.repository.OpravneniZavodRepository;
import cz.diamo.share.repository.UzivatelOpravneniRepository;
import cz.diamo.share.services.BudovaServices;
import cz.diamo.vratnice.zadosti.services.ZadostiServices;
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
	private OpravneniBudovaRepository opravneniBudovaRepository;

	@Autowired
	private OpravneniZavodRepository opravneniZavodRepository;

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
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_BUDOV')")
	public ResponseEntity<Set<BudovaDto>> listDlePristupu(
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam @Nullable Boolean aktivita,
			@RequestParam @Nullable String idLokalita) {

		String idUzivatel = appUserDto.getIdUzivatel();

		Set<BudovaDto> resultBudovy = new HashSet<>();
		List<Opravneni> opravneniUzivatele = uzivatelOpravneniRepository.listOpravneni(idUzivatel, aktivita);
	
		if (opravneniUzivatele == null || opravneniUzivatele.isEmpty()) {
			return ResponseEntity.ok(resultBudovy);  // pokud uživatel nemá oprávnění, vracíme prázdný set
		}
	
		for (Opravneni opravneni : opravneniUzivatele) {
			OpravneniTypPristupuBudovaEnum typPristupu = opravneni.getOpravneniTypPristupuBudova().getOpravneniTypPristupuBudovaEnum();
	
			switch (typPristupu) {
				case TYP_PRIST_BUDOVA_OPR_VSE:
					listVsechnyBudovy(resultBudovy, aktivita, idLokalita);
					break;
	
				case TYP_PRIST_BUDOVA_OPR_ZAVOD:
					listBudovyZavodu(resultBudovy, opravneni, aktivita, idLokalita);
					break;
	
				case TYP_PRIST_BUDOVA_OPR_VYBER:
					listVybraneBudovy(resultBudovy, opravneni, aktivita, idLokalita);
					break;
	
				default:
					break;
			}
		}
	
		return ResponseEntity.ok(resultBudovy);
	}

	private void listVsechnyBudovy(Set<BudovaDto> resultBudovy, Boolean aktivita, String idLokalita) {
		List<Budova> vsechnyBudovy = budovaServices.getList(null, idLokalita, aktivita);
		for (Budova budova : vsechnyBudovy) {
			resultBudovy.add(new BudovaDto(budova));
		}
	}
	
	private void listBudovyZavodu(Set<BudovaDto> resultBudovy, Opravneni opravneni, Boolean aktivita, String idLokalita) {
		List<Zavod> zavodyDleOpravneni = opravneniZavodRepository.listZavod(opravneni.getIdOpravneni());
		for (Zavod zavod : zavodyDleOpravneni) {
			List<Budova> budovyZavodu = budovaServices.getList(zavod.getIdZavod(), idLokalita, aktivita);
			for (Budova budova : budovyZavodu) {
				resultBudovy.add(new BudovaDto(budova));
			}
		}
	}
	
	private void listVybraneBudovy(Set<BudovaDto> resultBudovy, Opravneni opravneni, Boolean aktivita, String idLokalita) {
		List<Budova> vybraneBudovy = opravneniBudovaRepository.listBudova(opravneni.getIdOpravneni());
		if (vybraneBudovy != null) {
			for (Budova budova : vybraneBudovy) {
				if (aktivita != null) {
					if (budova.getAktivita().equals(aktivita) && budova.getLokalita().getIdLokalita().equals(idLokalita))
					resultBudovy.add(new BudovaDto(budova));
				}
			}
		}
	}
}
