package cz.dp.share.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.OznameniDto;
import cz.dp.share.services.OznameniServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class OznameniController extends BaseController {

	final static Logger logger = LogManager.getLogger(OznameniController.class);

	@Autowired
	private OznameniServices oznameniServices;

	@GetMapping("/oznameni/list")
	@PreAuthorize("isFullyAuthenticated()")
	public List<OznameniDto> list(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request) {
		try {
			return oznameniServices.list(request, appUserDto);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@PostMapping("/oznameni/precteno")
	@PreAuthorize("isFullyAuthenticated()")
	public void precteno(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String idOznameni) {
		try {
			oznameniServices.precteno(request, appUserDto, idOznameni);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@PostMapping("/oznameni/odstranit")
	@PreAuthorize("isFullyAuthenticated()")
	public void odstranit(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto,
			HttpServletRequest request, @RequestParam String idOznameni) {
		try {
			oznameniServices.odstranit(request, appUserDto, idOznameni);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

}
