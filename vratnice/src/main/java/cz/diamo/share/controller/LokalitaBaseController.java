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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.LokalitaDto;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.services.LokalitaServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/base/lokalita")
public class LokalitaBaseController extends BaseController {

	final static Logger logger = LogManager.getLogger(LokalitaBaseController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LokalitaServices lokalitaServices;

	@PersistenceContext
	private EntityManager entityManager;

	@GetMapping("/detail")
	@PreAuthorize("isFullyAuthenticated()")
	public LokalitaDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String id) {
		Lokalita lokalita = lokalitaServices.getDetail(id);
		if (lokalita == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));

		LokalitaDto lokalitaDto = new LokalitaDto(lokalita);
		return lokalitaDto;
	}

	@GetMapping("/list")
    @PreAuthorize("isFullyAuthenticated()")
	public List<LokalitaDto> list(HttpServletRequest request,
			@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			@RequestParam @Nullable Boolean aktivni, @RequestParam @Nullable Boolean verejne, @RequestParam @Nullable String idZavod) {

		List<LokalitaDto> result = new ArrayList<LokalitaDto>();
		List<Lokalita> list = lokalitaServices.getList(idZavod, aktivni, verejne);
		
		if (list != null && list.size() > 0) {
			for (Lokalita lokalita : list) {
				result.add(new LokalitaDto(lokalita));
			}
		}

		return result;
	}

	@GetMapping("/update")
    @PreAuthorize("isFullyAuthenticated()")
	public void updateFromEdos(HttpServletRequest request, @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) {
        try {
            lokalitaServices.updateLokalitaFromEdos(request, appUserDto);
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

	@PostMapping("/zverejnit")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_LOKALIT')")
	public void updateZverejnit(HttpServletRequest request, @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, @RequestBody List<LokalitaDto> lokality) {
        try {
            for (LokalitaDto lokalitaDto : lokality) {
                if (lokalitaDto.getVerejne()) {
                    lokalitaServices.skryt(lokalitaDto.getId());
                } else {
                    lokalitaServices.zviditelnit(lokalitaDto.getId());
                }
            }
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }
}
