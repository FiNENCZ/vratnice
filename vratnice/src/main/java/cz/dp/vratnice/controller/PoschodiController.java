package cz.dp.vratnice.controller;

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

import cz.dp.share.controller.BaseController;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.BaseException;
import cz.dp.vratnice.dto.PoschodiDto;
import cz.dp.vratnice.entity.Poschodi;
import cz.dp.vratnice.service.PoschodiService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class PoschodiController extends BaseController {

    final static Logger logger = LogManager.getLogger(PoschodiController.class);

 	@Autowired
	private MessageSource messageSource;

   @Autowired
    private PoschodiService poschodiService;

    @PersistenceContext
	private EntityManager entityManager;

    @GetMapping("/poschodi/list")
	@PreAuthorize("isFullyAuthenticated()")
    public List<PoschodiDto> list(@RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable String idBudova) {

        List<PoschodiDto> result = new ArrayList<PoschodiDto>();
        List<Poschodi> list = poschodiService.getList(idBudova, aktivni);

        if (list != null && list.size() > 0) {
            for (Poschodi poschodi : list) {
                result.add(new PoschodiDto(poschodi));
            }
        }

        return result;
    }

    @GetMapping("/poschodi/detail")
	@PreAuthorize("isFullyAuthenticated()")
    public PoschodiDto detail(@RequestParam String idPoschodi) {
        Poschodi poschodi = poschodiService.getDetail(idPoschodi);
		if (poschodi == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

        PoschodiDto poschodiDto = new PoschodiDto(poschodi); 
        return poschodiDto;
    }
    
	@PostMapping("/poschodi/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_BUDOV')")
	public PoschodiDto save(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, HttpServletRequest request,
			@RequestBody @Valid PoschodiDto detail) {
		try {
			Poschodi poschodi = poschodiService.save(detail.getPoschodi(null, false));

			entityManager.detach(poschodi);
			poschodi = poschodiService.getDetail(poschodi.getIdPoschodi());
			return new PoschodiDto(poschodi);
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
	 * @param idPoschodi   id záznamu
	 */
	@PostMapping("/poschodi/delete")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_BUDOV')")
	public void delete(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam String idPoschodi) {
		try {
			poschodiService.odstranit(idPoschodi);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	/**
	 * Obnovení záznamu
	 * 
	 * @param appUserDto Uživatel
	 * @param idPoschodi   id záznamu
	 */
	@PostMapping("/poschodi/restore")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_BUDOV')")
	public void restore(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam String idPoschodi) {
		try {
			poschodiService.obnovit(idPoschodi);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}
}
