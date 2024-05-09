package cz.diamo.share.rest.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.Zakazka;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.share.repository.ZakazkaRepository;
import cz.diamo.share.rest.dto.ZakazkaDto;
import cz.diamo.share.services.ZakazkaServices;
import cz.diamo.share.services.ZavodServices;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Zakázka
 */
@RestController
public class ZakazkaRestController extends BaseRestController {

	final static Logger logger = LogManager.getLogger(ZakazkaRestController.class);

	@Autowired
	private ZakazkaServices zakazkaServices;

	@Autowired
	private ZakazkaRepository zakazkaRepository;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ZavodServices zavodServices;

	/**
	 * Uložení údajů o zakázce
	 * 
	 * @param request  Dotaz
	 * @param idZavodu Identifikátor závodu
	 * @param zaznamy  Seznam záznamů
	 */
	@PostMapping("/zakazka/save")
	@PreAuthorize("hasAnyAuthority('ROLE_PERSONALISTIKA')")
	public void save(HttpServletRequest request, @RequestParam String sapIdPersonalniOblasti,
			@RequestBody List<ZakazkaDto> zaznamy) {
		if (zaznamy == null || zaznamy.size() == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("zaznam.not.null", null, LocaleContextHolder.getLocale()));

		Zavod zavod = zavodServices.getDetailBySapId(sapIdPersonalniOblasti);
		if (zavod == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("zavod.not.found", null, LocaleContextHolder.getLocale()));

		try {
			for (ZakazkaDto zaznam : zaznamy) {

				Utils.validate(zaznam);

				Zakazka zakazka = zakazkaRepository.getDetailBySapId(zaznam.getSapIdZakazky(),
						zavod.getIdZavod());
				if (zakazka == null)
					zakazka = new Zakazka();
				zakazka.setZavod(zavod);
				zakazka.setSapId(zaznam.getSapIdZakazky());
				if (!StringUtils.isBlank(zaznam.getNazev()))
					zakazka.setNazev(zaznam.getNazev());
				if (StringUtils.isBlank(zakazka.getNazev()))
					zakazka.setNazev(zakazka.getSapId());

				if (zaznam.getPlatnostOd() == null)
					zakazka.setPlatnostOd(Utils.getMinDate());
				else
					zakazka.setPlatnostOd(zaznam.getPlatnostOd());
				if (zaznam.getPlatnostDo() == null)
					zakazka.setPlatnostDo(Utils.getMaxDate(false));
				else
					zakazka.setPlatnostDo(zaznam.getPlatnostDo());

				zakazka = zakazkaServices.save(zakazka);
			}
		} catch (ValidationException ve) {

			String message = "";
			String[] resx = ve.getMessage().replace("{", "").replace("}", "").split("\n");
			for (String res : resx) {
				if (!StringUtils.isBlank(message))
					message += "\n";
				message += messageSource.getMessage(res, null, LocaleContextHolder.getLocale());
			}
			logger.error(message);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);

		} catch (BaseException ex) {
			logger.error(ex);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.toString());

		}
	}
}
