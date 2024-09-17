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
import cz.diamo.share.dto.AktualizacePristupovychKaretResponseDto;
import cz.diamo.share.dto.Ws02ZastupDto;
import cz.diamo.share.dto.Wso2UzivatelExtDto;
import cz.diamo.share.edos.services.PristupovaKartaEdosServices;
import cz.diamo.share.entity.KmenovaData;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.share.rest.dto.KmenovaDataDto;
import cz.diamo.share.services.KmenovaDataServices;
import cz.diamo.share.services.UzivatelServices;
import cz.diamo.share.services.ZastupServices;
import cz.diamo.share.services.ZavodServices;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Zaměstnanec
 */
@RestController
public class KmenovaDataRestController extends BaseRestController {

	final static Logger logger = LogManager.getLogger(KmenovaDataRestController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ZavodServices zavodServices;

	@Autowired
	private KmenovaDataServices kmenovaDataServices;

	@Autowired
	private ZastupServices zastupServices;

	@Autowired
	private UzivatelServices uzivatelServices;

	@Autowired
	private PristupovaKartaEdosServices pristupovaKartaEdosServices;

	/**
	 * Uložení údajů o zaměstnanci
	 * 
	 * @param request  Dotaz
	 * @param idZavodu Identifikátor závodu
	 * @param zaznamy  Seznam záznamů
	 */
	@PostMapping("/kmenova-data/save")
	@PreAuthorize("hasAnyAuthority('ROLE_PERSONALISTIKA')")
	public void save(HttpServletRequest request, @RequestParam String sapIdPersonalniOblasti,
			@RequestBody List<KmenovaDataDto> zaznamy) {
		if (zaznamy == null || zaznamy.size() == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("zaznam.not.null", null, LocaleContextHolder.getLocale()));

		Zavod zavod = zavodServices.getDetailBySapId(sapIdPersonalniOblasti);
		if (zavod == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("zavod.not.found", null, LocaleContextHolder.getLocale()));

		try {

			String guidDavky = Utils.vratGuid();

			for (KmenovaDataDto zaznam : zaznamy) {
				Utils.validate(zaznam);

				// založení záznamu
				KmenovaData kmenovaData = zaznam.getKmenovaData();
				kmenovaData.setZavod(zavod);
				kmenovaData.setGuidDavky(guidDavky);
				kmenovaDataServices.save(kmenovaData);

			}

			// spuštění aktualizace dat zaměstnanců - ASYNC
			kmenovaDataServices.aktualizaceZamestnancuAsync(guidDavky);

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

		}
	}

	@PostMapping("/kmenova-data/zastupy/save")
	@PreAuthorize("hasAnyAuthority('ROLE_PERSONALISTIKA')")
	public void zastupySave(HttpServletRequest request, @RequestBody List<Ws02ZastupDto> zaznamy) {
		if (zaznamy == null || zaznamy.size() == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("zaznam.not.null", null, LocaleContextHolder.getLocale()));
		try {

			for (Ws02ZastupDto zaznam : zaznamy) {
				// validace
				Utils.validate(zaznam);
				zastupServices.aktualizovat(zaznam);
			}

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

		} catch (ResponseStatusException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		}
	}

	@PostMapping("/kmenova-data/externi-uzivatele/save")
	@PreAuthorize("hasAnyAuthority('ROLE_PERSONALISTIKA')")
	public void externiUzivateleSave(HttpServletRequest request, @RequestBody List<Wso2UzivatelExtDto> zaznamy) {
		if (zaznamy == null || zaznamy.size() == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					messageSource.getMessage("zaznam.not.null", null, LocaleContextHolder.getLocale()));
		try {

			for (Wso2UzivatelExtDto zaznam : zaznamy) {
				// validace
				Utils.validate(zaznam);
			}

			uzivatelServices.aktualizovatExterniUzivatele(zaznamy);

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

		} catch (ResponseStatusException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		}
	}

	@PostMapping("/kmenova-data/rfid-edos/synchronizace")
	@PreAuthorize("hasAnyAuthority('ROLE_PERSONALISTIKA')")
	public AktualizacePristupovychKaretResponseDto rfidEdosSynchronizace(HttpServletRequest request) {

		try {
			return pristupovaKartaEdosServices.aktualizacePristupovychKaret(null);
		} catch (ResponseStatusException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
		}
	}
}
