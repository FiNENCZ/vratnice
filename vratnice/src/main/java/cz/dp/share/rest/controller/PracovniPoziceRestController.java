package cz.dp.share.rest.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.base.Utils;
import cz.dp.share.entity.PracovniPozice;
import cz.dp.share.entity.PracovniPoziceLog;
import cz.dp.share.exceptions.ValidationException;
import cz.dp.share.rest.dto.PracovniPoziceDohodaDto;
import cz.dp.share.rest.dto.PracovniPoziceDto;
import cz.dp.share.services.PracovniPoziceLogServices;
import cz.dp.share.services.PracovniPoziceServices;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PracovniPoziceRestController extends BaseRestController {

	final static Logger logger = LogManager.getLogger(PracovniPoziceRestController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private PracovniPoziceServices pracovniPoziceServices;

	@Autowired
	private PracovniPoziceLogServices pracovniPoziceLogServices;

	@PostMapping("/pracovni-pozice/save")
	@PreAuthorize("hasAnyAuthority('ROLE_PERSONALISTIKA')")
	public void pracovniPoziceSave(HttpServletRequest request, @RequestBody List<PracovniPoziceDto> zaznamy) {
		if (zaznamy == null || zaznamy.size() == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("zaznam.not.null", null, LocaleContextHolder.getLocale()));
		// zalogování dotazu
		PracovniPoziceLog log = pracovniPoziceLogServices.save(null, zaznamy, null, true);

		try {

			List<PracovniPozice> pracovniPoziceList = new ArrayList<PracovniPozice>();
			Date now = Calendar.getInstance().getTime();

			List<String> pridano = new ArrayList<String>();
			for (PracovniPoziceDto zaznam : zaznamy) {
				Utils.validate(zaznam);

				PracovniPozice pracovniPozice = new PracovniPozice();
				pracovniPozice.setSapId(zaznam.getSapIdPracovniPozice());
				pracovniPozice.setZkratka(zaznam.getZkratka());
				pracovniPozice.setNazev(zaznam.getNazev());
				if (!zaznam.getSapIdPracovniPozice().equals(zaznam.getSapIdPracovniPoziceNadrizene()))
					pracovniPozice.setSapIdNadrizeny(zaznam.getSapIdPracovniPoziceNadrizene());
				pracovniPozice.setPlatnostOd(zaznam.getPlatnostOd());
				pracovniPozice.setPlatnostDo(zaznam.getPlatnostDo());
				if (pracovniPozice.getPlatnostOd() == null)
					pracovniPozice.setPlatnostOd(Utils.getMinDate());
				else
					pracovniPozice.setPlatnostOd(Utils.setMinTime(pracovniPozice.getPlatnostOd()));
				if (pracovniPozice.getPlatnostDo() == null)
					pracovniPozice.setPlatnostDo(Utils.getMaxDate(false));
				else
					pracovniPozice.setPlatnostDo(Utils.setMaxTime(pracovniPozice.getPlatnostDo()));
				pracovniPozice.setCasAktualizace(now);

				// pouze pokud je aktualni, nebo pokud neexistuje aktualni a je do budoucna
				if (!pridano.contains(pracovniPozice.getSapId())) {
					if (pracovniPozice.getPlatnostOd().compareTo(now) == -1
							&& pracovniPozice.getPlatnostDo().compareTo(now) == 1) {
						pracovniPoziceList.add(pracovniPozice);
						pridano.add(pracovniPozice.getSapId());
					} else if (pracovniPozice.getPlatnostOd().compareTo(now) == 1
							&& !existsAktualni(zaznamy, pracovniPozice.getSapId())) {
						pracovniPoziceList.add(pracovniPozice);
						pridano.add(pracovniPozice.getSapId());
					}
				}

			}

			// donačtení dohodářských pracovních pozic
			List<PracovniPozice> listDohody = pracovniPoziceServices.getList(true, true);
			if (listDohody != null && listDohody.size() > 0)
				pracovniPoziceList.addAll(listDohody);

			// zpracování pracovních pozic
			pracovniPoziceServices.zpracovatPracovniPozice(pracovniPoziceList);
			pracovniPoziceLogServices.save(log, zaznamy, null, true);
		} catch (ValidationException ve) {
			logger.error(ve);

			String message = "";
			String[] resx = ve.getMessage().replace("{", "").replace("}", "").split("\n");
			for (String res : resx) {
				if (!StringUtils.isBlank(message))
					message += "\n";
				message += messageSource.getMessage(res, null, LocaleContextHolder.getLocale());
			}
			pracovniPoziceLogServices.save(log, zaznamy, message, true);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);

		} catch (Exception e) {
			logger.error(e);
			pracovniPoziceLogServices.save(log, zaznamy, e.toString(), true);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@PostMapping("/pracovni-pozice/dohodari/save")
	@PreAuthorize("hasAnyAuthority('ROLE_PERSONALISTIKA')")
	public void pracovniPoziceDohodariSave(HttpServletRequest request,
			@RequestBody List<PracovniPoziceDohodaDto> zaznamy) {
		if (zaznamy == null || zaznamy.size() == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("zaznam.not.null", null, LocaleContextHolder.getLocale()));

		try {

			pracovniPoziceServices.zpracovatPracovniPoziceDohodare(zaznamy);
		} catch (ValidationException ve) {
			logger.error(ve);

			String message = "";
			String[] resx = ve.getMessage().replace("{", "").replace("}", "").split("\n");
			for (String res : resx) {
				if (!StringUtils.isBlank(message))
					message += "\n";
				message += messageSource.getMessage(res, null, LocaleContextHolder.getLocale());
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);

		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	private boolean existsAktualni(List<PracovniPoziceDto> pracovniPozice, String sapId) {

		for (PracovniPoziceDto zaznam : pracovniPozice) {
			if (zaznam.getSapIdPracovniPozice().equals(sapId)) {
				Date platnostOd = zaznam.getPlatnostOd();
				Date platnostDo = zaznam.getPlatnostDo();
				if (platnostOd == null)
					platnostOd = Utils.getMinDate();
				if (platnostDo == null)
					platnostDo = Utils.getMaxDate(false);
				Date now = Calendar.getInstance().getTime();
				if (platnostOd.compareTo(now) == -1 && platnostDo.compareTo(now) == 1)
					return true;
			}

		}

		return false;
	}
}
