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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.BudovaDto;
import cz.diamo.share.entity.Budova;
import cz.diamo.share.services.BudovaServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/base/budova")
public class BudovaBaseController extends BaseController {

	final static Logger logger = LogManager.getLogger(BudovaBaseController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private BudovaServices budovaServices;

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/detail")
	@PreAuthorize("isFullyAuthenticated()")
	public BudovaDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String id) {
		Budova budova = budovaServices.getDetail(id);
		if (budova == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

		BudovaDto budovaDto = new BudovaDto(budova);
		return budovaDto;
	}

	@GetMapping("/list")
    @PreAuthorize("isFullyAuthenticated()")
	public List<BudovaDto> list(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idZavod, @RequestParam @Nullable String idLokalita) {

		List<BudovaDto> result = new ArrayList<BudovaDto>();
		List<Budova> list = budovaServices.getList(idZavod, idLokalita, aktivni);
		
		if (list != null && list.size() > 0) {
			for (Budova budova : list) {
				result.add(new BudovaDto(budova));
			}
		}

		return result;
	}
}
