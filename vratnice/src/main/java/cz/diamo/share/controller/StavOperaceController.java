package cz.diamo.share.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.StavOperaceDto;
import cz.diamo.share.entity.TmpStavOperace;
import cz.diamo.share.enums.StavOperaceIdEnum;
import cz.diamo.share.services.StavOperaceServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class StavOperaceController extends BaseController {

	final static Logger logger = LogManager.getLogger(StavOperaceController.class);

	@Autowired
	private StavOperaceServices stavOperaceServices;

	@GetMapping("/stav_operace")
	@PreAuthorize("isFullyAuthenticated()")
	public StavOperaceDto detail(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam StavOperaceIdEnum id) {
		try {
			TmpStavOperace tmpStavOperace = stavOperaceServices.getDetail(appUserDto.getIdUzivatel(), id);
			if (tmpStavOperace != null)
				return new StavOperaceDto(tmpStavOperace);
			else
				return null;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

}
