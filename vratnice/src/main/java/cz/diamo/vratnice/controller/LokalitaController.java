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

import cz.diamo.share.controller.LokalitaBaseController;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.LokalitaDto;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.services.LokalitaServices;
import cz.diamo.vratnice.zadosti.services.ZadostiServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/lokalita")
public class LokalitaController extends LokalitaBaseController {

	final static Logger logger = LogManager.getLogger(LokalitaController.class);

	@Autowired
	private LokalitaServices lokalitaServices;

	@Autowired
	private ZadostiServices zadostiServices;

	@PersistenceContext
	private EntityManager entityManager;

	@PostMapping("/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_LOKALIT')")
	public LokalitaDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request,
			@RequestBody @Valid LokalitaDto detail) {
		try {
			Lokalita lokalita = lokalitaServices.save(detail.getLokalita(null, false));
            zadostiServices.saveLokalita(new LokalitaDto(lokalita), request, appUserDto);

			entityManager.detach(lokalita);
			lokalita = lokalitaServices.getDetail(lokalita.getIdLokalita());
			return new LokalitaDto(lokalita);
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
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_LOKALIT')")
	public void delete(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam String idLokalita) {
		try {
			lokalitaServices.odstranit(idLokalita);
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
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_LOKALIT')")
	public void restore(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam String idLokalita) {
		try {
			lokalitaServices.obnovit(idLokalita);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}
}
