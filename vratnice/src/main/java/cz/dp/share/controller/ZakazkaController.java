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
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.ZakazkaDto;
import cz.dp.share.entity.Zakazka;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.services.ZakazkaServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Zakázka
 */
@RestController
public class ZakazkaController extends BaseController {

	final static Logger logger = LogManager.getLogger(ZakazkaController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ZakazkaServices zakazkaServices;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Detail
	 * 
	 * @param appUserDto Uživatel
	 * @param request    Dotaz
	 * @param id         Identifikátor záznamu
	 * @return Detail záznamu
	 */
	@GetMapping("/zakazka/detail")
	@PreAuthorize("hasAnyAuthority('ROLE_CISELNIKY_ZAKAZKY')")
	public ZakazkaDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String id) {
		Zakazka zakazka = zakazkaServices.getDetail(id, appUserDto.getZavod().getId());
		if (zakazka == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
		ZakazkaDto zakazkaDto = new ZakazkaDto(zakazka);
		return zakazkaDto;
	}

	/**
	 * Seznam
	 * 
	 * @param appUserDto Uživatel
	 * @param request    Dotaz
	 * @param aktivni    Aktivita
	 * @param idZavodu   Identifikátor závodu
	 * @return Seznam záznamů
	 */
	@GetMapping("/zakazka/list")
	@PreAuthorize("isAuthenticated()")
	public List<ZakazkaDto> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idZavodu) {

		List<ZakazkaDto> result = new ArrayList<ZakazkaDto>();

        ArrayList<String> idsZavod = new ArrayList<String>();
        idsZavod.add(appUserDto.getZavod().getId());

		if (StringUtils.isNotBlank(idZavodu))
			idsZavod.add(idZavodu);

        List<Zakazka> list = zakazkaServices.getList(aktivni, idsZavod);
        if (list != null && list.size() > 0) {
			for (Zakazka zakazka : list) {
				result.add(new ZakazkaDto(zakazka));
			}
		}

		return result;
	}

	/**
	 * Uložení záznamů
	 * 
	 * @param appUserDto Uživatel
	 * @param detail     Detail záznamu
	 * @return Detail záznamu
	 */
	@PostMapping("/zakazka/save")
	@PreAuthorize("hasAnyAuthority('ROLE_CISELNIKY_ZAKAZKY')")
	public ZakazkaDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestBody @Valid ZakazkaDto detail) {
		try {
			Zakazka zakazka = zakazkaServices.save(detail.getZakazka(null, appUserDto, false));
			entityManager.clear();
			zakazka = zakazkaServices.getDetail(zakazka.getIdZakazka(), appUserDto.getZavod().getId());
			return new ZakazkaDto(zakazka);
		} catch (BaseException e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

}
