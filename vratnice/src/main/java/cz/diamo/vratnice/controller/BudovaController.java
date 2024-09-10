package cz.diamo.vratnice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import cz.diamo.share.exceptions.BaseException;
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

	@PersistenceContext
	private EntityManager entityManager;

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
}
