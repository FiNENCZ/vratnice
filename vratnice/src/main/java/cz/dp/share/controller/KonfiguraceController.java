package cz.dp.share.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.configuration.AppProperties;
import cz.dp.share.configuration.ApplicationProperties;
import cz.dp.share.dto.KonfiguraceDto;
import cz.dp.share.dto.UcetKcDto;
import cz.dp.share.entity.Databaze;
import cz.dp.share.services.DatabazeServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class KonfiguraceController extends BaseController {

	final static Logger logger = LogManager.getLogger(KonfiguraceController.class);

	@Autowired
	private DatabazeServices databazeServices;

	@Autowired
	private ApplicationProperties applicationProperties;

	@Autowired
	private AppProperties appProperties;

	// @Autowired
	// private ResourcesComponent resourcesComponent;

	@GetMapping("/konfigurace")
	public KonfiguraceDto detail(HttpServletRequest request) {

		KonfiguraceDto konfiguraceDto = new KonfiguraceDto();
		Databaze databaze = databazeServices.getDetail();

		konfiguraceDto.setDemo(databaze.getDemo());
		konfiguraceDto.setVerzeDb(databaze.getVerzeTxt());
		konfiguraceDto.setVerzeApi(applicationProperties.getVersion());
		konfiguraceDto.setColorScheme(appProperties.getColorScheme());
		konfiguraceDto.setPortalUrl(appProperties.getPortalUrl());
		konfiguraceDto.setZmenaHeslaUrl(appProperties.getKeycloakUrl() + "/realms/" + appProperties.getKeycloakRealm()
				+ "/account/#/security/signingin");

		return konfiguraceDto;
	}

	@GetMapping("/konfigurace/kc-uzivatele/detail")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_KC_UZIVATELE')")
	public UcetKcDto kcUzivateleDetail(HttpServletRequest request) {
		try {
			Databaze databaze = databazeServices.getDetail();
			UcetKcDto result = new UcetKcDto();
			result.setJmeno(databaze.getKcUzivateleJmeno());
			return result;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

	@PostMapping("/konfigurace/kc-uzivatele/save")
	@PreAuthorize("hasAnyAuthority('ROLE_SPRAVA_KC_UZIVATELE')")
	public UcetKcDto kcUzivateleSave(HttpServletRequest request, @RequestBody @Valid UcetKcDto detail) {
		try {
			Databaze databaze = databazeServices.saveKcUzivatele(detail.getJmeno(), detail.getHeslo());
			UcetKcDto result = new UcetKcDto();
			result.setJmeno(databaze.getKcUzivateleJmeno());
			return result;
		} catch (Exception e) {
			logger.error(e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
	}

}
