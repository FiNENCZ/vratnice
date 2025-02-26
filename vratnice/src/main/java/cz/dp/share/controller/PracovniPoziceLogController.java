package cz.dp.share.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.dto.PracovniPoziceLogDto;
import cz.dp.share.entity.PracovniPoziceLog;
import cz.dp.share.services.PracovniPoziceLogServices;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PracovniPoziceLogController extends BaseController {

	final static Logger logger = LogManager.getLogger(PracovniPoziceLogController.class);

	@Autowired
	private PracovniPoziceLogServices pracovniPoziceLogServices;

	@GetMapping("/pracovni-pozice/log/last-ok")
	@PreAuthorize("isAuthenticated()")
	public PracovniPoziceLogDto lastOk(HttpServletRequest request) {

		try {
			PracovniPoziceLog lastOk = pracovniPoziceLogServices.getLastOk();
			if (lastOk == null)
				return null;
			else
				return new PracovniPoziceLogDto(lastOk);
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

	@GetMapping("/pracovni-pozice/log/list")
	@PreAuthorize("hasAnyAuthority('ROLE_SERVIS_ORG_STR')")
	public List<PracovniPoziceLogDto> list(HttpServletRequest request) {

		try {
			List<PracovniPoziceLogDto> result = new ArrayList<PracovniPoziceLogDto>();
			List<PracovniPoziceLog> list = pracovniPoziceLogServices.getList();
			if (list != null && list.size() > 0) {
				for (PracovniPoziceLog pracovniPoziceLog : list) {
					result.add(new PracovniPoziceLogDto(pracovniPoziceLog));
				}
			}
			return result;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}

	}

}
