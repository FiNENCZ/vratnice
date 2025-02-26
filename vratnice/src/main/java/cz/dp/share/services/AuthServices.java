package cz.dp.share.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.Http;
import org.keycloak.protocol.oidc.client.authentication.ClientIdAndSecretCredentialsProvider;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;

import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.base.Utils;
import cz.dp.share.configuration.AppProperties;
import cz.dp.share.constants.Constants;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.dto.RoleKcDto;
import cz.dp.share.dto.UzivatelKcDto;
import cz.dp.share.dto.ZastupSimpleDto;
import cz.dp.share.dto.keycloak.KeyCloakPayloadDto;
import cz.dp.share.dto.security.AuthCookieDto;
import cz.dp.share.entity.Databaze;
import cz.dp.share.entity.Opravneni;
import cz.dp.share.entity.Uzivatel;
import cz.dp.share.entity.ZastupSimple;
import cz.dp.share.entity.Zavod;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.ValidationException;
import cz.dp.share.repository.OpravneniRepository;
import cz.dp.share.repository.TmpOpravneniVseRepository;
import cz.dp.share.repository.TmpOpravneniVyberRepository;
import cz.dp.share.repository.UzivatelOpravneniRepository;
import cz.dp.share.repository.UzivatelRepository;
import cz.dp.share.repository.UzivatelZavodRepository;
import cz.dp.share.repository.ZastupRepository;
import cz.dp.share.security.SecurityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServices {

    final static Logger logger = LogManager.getLogger(AuthServices.class);

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UzivatelOpravneniRepository uzivatelOpravneniRepository;

    @Autowired
    private OpravneniRepository opravneniRepository;

    @Autowired
    private TmpOpravneniVseRepository tmpOpravneniVseRepository;

    @Autowired
    private TmpOpravneniVyberRepository tmpOpravneniVyberRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private UzivatelZavodRepository uzivatelZavodRepository;

    @Autowired
    private ZastupServices zastupServices;

    @Autowired
    private DatabazeServices databazeServices;

    @Autowired
    private ZastupRepository zastupRepository;

    @Autowired
    private RestOperations restKeyCloak;

    @TransactionalWrite
    public ResponseEntity<AppUserDto> login(String username, String password, HttpServletResponse httpServletResponse)
            throws JsonProcessingException, BaseException {
        return loginKeyCloak(username, password, httpServletResponse);

    }

    @TransactionalWrite
    public ResponseEntity<AppUserDto> zmenaZavodu(AppUserDto appUserDto, String idZavodu) throws JsonProcessingException, BaseException {

        Uzivatel uzivatel = uzivatelServices.getDetail(appUserDto.getIdUzivatel(), false);

        if (uzivatel == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean dohledano = uzivatel.getZavod().getIdZavod().equals(idZavodu);
        if (!dohledano && uzivatel.getOstatniZavody() != null) {
            for (Zavod zavod : uzivatel.getOstatniZavody()) {
                if (zavod.getIdZavod().equals(idZavodu)) {
                    dohledano = true;
                    break;
                }
            }

        }
        if (!dohledano)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        uzivatel.setZavodVyber(new Zavod(idZavodu));
        uzivatel = uzivatelRepository.save(uzivatel);

        List<ZastupSimple> zastupy = zastupServices.getListDostupne(uzivatel.getIdUzivatel(), uzivatel.getIdZastup());

        return ResponseEntity.ok().body(new AppUserDto(uzivatel, zastupy));

    }

    @TransactionalWrite
    private ResponseEntity<AppUserDto> loginKeyCloak(Uzivatel uzivatel, List<ZastupSimple> zastupy, HttpServletResponse response,
            AccessTokenResponse accessTokenResponse) {
        if (uzivatel != null) {

            // sestavení T tabulek oprávnění
            tmpOpravneniVseRepository.deleteByUzivatel(uzivatel.getIdUzivatel());
            tmpOpravneniVyberRepository.deleteByuzivatel(uzivatel.getIdUzivatel());

            tmpOpravneniVseRepository.insertByUzivatel(uzivatel.getIdUzivatel());
            if (StringUtils.isBlank(uzivatel.getIdZastup()))
                tmpOpravneniVyberRepository.insertByUzivatel(uzivatel.getIdUzivatel());
            else {
                tmpOpravneniVyberRepository.insertByUzivatelZastup(uzivatel.getIdUzivatel(), uzivatel.getIdZastup());
                uzivatel.setZastup(uzivatelRepository.getDetail(uzivatel.getIdZastup()));
            }

            // donačtení rolí
            uzivatel.setRole(uzivatelOpravneniRepository.listRole(uzivatel.getIdUzivatel(), true));

            Cookie authCookie = securityUtils.generateAuthCookieKeyCloak(accessTokenResponse);
            Cookie authCookieExp = securityUtils.generateAuthCookieExpKeyCloak(accessTokenResponse);
            response.addCookie(authCookie);
            response.addCookie(authCookieExp);

            return ResponseEntity.ok().body(new AppUserDto(uzivatel, zastupy));

        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @TransactionalWrite
    private ResponseEntity<AppUserDto> loginKeyCloak(Uzivatel uzivatel, HttpServletRequest request, HttpServletResponse response) {
        if (uzivatel != null) {

            // sestavení T tabulek oprávnění
            tmpOpravneniVseRepository.deleteByUzivatel(uzivatel.getIdUzivatel());
            tmpOpravneniVyberRepository.deleteByuzivatel(uzivatel.getIdUzivatel());

            tmpOpravneniVseRepository.insertByUzivatel(uzivatel.getIdUzivatel());
            if (StringUtils.isBlank(uzivatel.getIdZastup()))
                tmpOpravneniVyberRepository.insertByUzivatel(uzivatel.getIdUzivatel());
            else
                tmpOpravneniVyberRepository.insertByUzivatelZastup(uzivatel.getIdUzivatel(), uzivatel.getIdZastup());

            // donačtení rolí
            uzivatel.setRole(uzivatelOpravneniRepository.listRole(uzivatel.getIdUzivatel(), true));

            // autorizace podle tokenu
            AuthCookieDto authCookieDto = securityUtils.extractAuthCookieDto(request);

            return loginKeyCloak(authCookieDto.getRefreshToken(), response, false);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @TransactionalWrite
    public ResponseEntity<AppUserDto> loginKeyCloak(String userName, String password, HttpServletResponse response) {

        Configuration cfg = new Configuration();
        cfg.setAuthServerUrl(appProperties.getKeycloakUrl());
        cfg.setResource(appProperties.getKeycloakClientId());
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("secret", appProperties.getKeycloakSecret());
        cfg.setCredentials(credentials);
        cfg.setRealm(appProperties.getKeycloakRealm());
        AuthzClient authzClient = AuthzClient.create(cfg);

        AccessTokenResponse accessTokenResponse = authzClient.obtainAccessToken(userName, password);

        return loginKeyCloak(accessTokenResponse, response);
    }

    @TransactionalWrite
    public ResponseEntity<AppUserDto> loginKeyCloakByAuthorizationCode(String authorizationCode, HttpServletRequest request, HttpServletResponse response) {
        AccessTokenResponse accessTokenResponse = tokenByAuthorizationCode(request, authorizationCode);
        return loginKeyCloak(accessTokenResponse, response);
    }

    @TransactionalWrite
    public void updateRoles(String accesToken) {

    }

    @TransactionalWrite
    public ResponseEntity<AppUserDto> loginKeyCloak(AccessTokenResponse accessTokenResponse, HttpServletResponse response) {
        try {
            if (!StringUtils.isBlank(accessTokenResponse.getToken())) {
                KeyCloakPayloadDto keyCloakPayload = securityUtils.extractPayload(accessTokenResponse.getToken());
                if (keyCloakPayload != null) {
                    Uzivatel uzivatel = uzivatelRepository.getDetailBySapId(keyCloakPayload.getSapid());
                    if (uzivatel != null && uzivatel.getAktivita()) {

                        // uložím oprávnění
                        List<Opravneni> listOpravneni = new ArrayList<Opravneni>();
                        List<String> opravneniString;

                        // zástupy
                        if (!StringUtils.isBlank(uzivatel.getIdZastup())) {

                            // pokud není zástup již dostupný - zruším ho
                            if (!zastupRepository.platnyZastup(uzivatel.getIdUzivatel(), uzivatel.getIdZastup())) {
                                uzivatel.setIdZastup(null);
                            }
                        }

                        // získání rolí pro zástup včetně modulů
                        if (!StringUtils.isBlank(uzivatel.getIdZastup())) {
                            opravneniString = getRoleZastupu(uzivatel.getIdZastup());
                            keyCloakPayload.setModuly(new ArrayList<String>());
                            if (opravneniString != null) {
                                for (String r : opravneniString) {
                                    if (r.contains("_")) {
                                        String[] array = r.split("_");
                                        if (array.length > 0) {
                                            if (!keyCloakPayload.getModuly().contains(array[0]))
                                                keyCloakPayload.getModuly().add(array[0]);
                                        }
                                    }
                                }
                            }

                        } else
                            opravneniString = getRoles(accessTokenResponse.getToken(), keyCloakPayload).getRole();

                        if (opravneniString != null) {
                            for (String role : opravneniString) {
                                Opravneni opravneni = opravneniRepository.getDetailByKod(role.toUpperCase(), uzivatel.getZavod().getIdZavod(), true);
                                if (opravneni != null)
                                    listOpravneni.add(opravneni);
                            }
                        }

                        uzivatel.setModuly(keyCloakPayload.getModuly());
                        uzivatel.setOpravneni(listOpravneni);
                        uzivatel.setZmenuProvedl(Utils.getZmenuProv());
                        uzivatel.setCasZmn(Utils.getCasZmn());
                        uzivatel = uzivatelServices.save(uzivatel, true, false, true);
                        uzivatel.setOstatniZavody(uzivatelZavodRepository.listZavod(uzivatel.getIdUzivatel()));

                        List<ZastupSimple> zastupy = zastupServices.getListDostupne(uzivatel.getIdUzivatel(), uzivatel.getIdZastup());

                        return loginKeyCloak(uzivatel, zastupy, response, accessTokenResponse);
                    } else
                        logout(null, accessTokenResponse.getRefreshToken());
                    // throw new
                    // RecordNotFoundException(messageSource.getMessage("uzivatel.not.found", null,
                    // LocaleContextHolder.getLocale()));
                }

            } else {
                throw new BaseException(messageSource.getMessage("ldap.empty.token", null, LocaleContextHolder.getLocale()));
            }
        } catch (Exception e) {
            logger.error(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @TransactionalWrite
    public ResponseEntity<AppUserDto> loginKeyCloak(String refreshToken, HttpServletResponse response, boolean decode) {
        try {

            String token = refreshToken;
            if (decode)
                token = new String(Base64.getDecoder().decode(refreshToken));
            AccessTokenResponse accessTokenResponse = refreshToken(token);
            return loginKeyCloak(accessTokenResponse, response);
        } catch (Exception e) {
            logger.error(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public AccessTokenResponse refreshToken(String refreshToken) {

        Configuration cfg = new Configuration();
        cfg.setAuthServerUrl(appProperties.getKeycloakUrl());
        cfg.setResource(appProperties.getKeycloakClientId());
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("secret", appProperties.getKeycloakSecret());
        cfg.setCredentials(credentials);
        cfg.setRealm(appProperties.getKeycloakRealm());

        String url = appProperties.getKeycloakUrl() + "/realms/" + appProperties.getKeycloakRealm() + "/protocol/openid-connect/token";
        String clientId = appProperties.getKeycloakClientId();
        String secret = appProperties.getKeycloakSecret();

        ClientIdAndSecretCredentialsProvider clientIdAndSecretCredentialsProvider = new ClientIdAndSecretCredentialsProvider();
        clientIdAndSecretCredentialsProvider.init(cfg, cfg);
        Http http = new Http(cfg, clientIdAndSecretCredentialsProvider);

        return http.<AccessTokenResponse>post(url).authentication().client().form().param("grant_type", "refresh_token").param("refresh_token", refreshToken)
                .param("client_id", clientId).param("client_secret", secret).response().json(AccessTokenResponse.class).execute();
    }

    public AccessTokenResponse tokenByAuthorizationCode(HttpServletRequest request, String authorizationCode) {

        Configuration cfg = new Configuration();
        cfg.setAuthServerUrl(appProperties.getKeycloakUrl());
        cfg.setResource(appProperties.getKeycloakClientId());
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("secret", appProperties.getKeycloakSecret());
        cfg.setCredentials(credentials);
        cfg.setRealm(appProperties.getKeycloakRealm());

        String url = appProperties.getKeycloakUrl() + "/realms/" + appProperties.getKeycloakRealm() + "/protocol/openid-connect/token";
        String clientId = appProperties.getKeycloakClientId();
        String secret = appProperties.getKeycloakSecret();

        ClientIdAndSecretCredentialsProvider clientIdAndSecretCredentialsProvider = new ClientIdAndSecretCredentialsProvider();
        clientIdAndSecretCredentialsProvider.init(cfg, cfg);
        Http http = new Http(cfg, clientIdAndSecretCredentialsProvider);
        try {
            return http.<AccessTokenResponse>post(url).authentication().client().form().param("grant_type", "authorization_code")
                    .param("code", authorizationCode)
                    .param("redirect_uri", request.getRequestURL().toString().replace(request.getRequestURI(), "") + "/api/login-sso-complete")
                    .param("client_id", clientId).param("client_secret", secret).response().json(AccessTokenResponse.class).execute();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }

    }

    public void logout(HttpServletRequest request, String refreshToken) {

        Configuration cfg = new Configuration();
        cfg.setAuthServerUrl(appProperties.getKeycloakUrl());
        cfg.setResource(appProperties.getKeycloakClientId());
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("secret", appProperties.getKeycloakSecret());
        cfg.setCredentials(credentials);
        cfg.setRealm(appProperties.getKeycloakRealm());

        String url = appProperties.getKeycloakUrl() + "/realms/" + appProperties.getKeycloakRealm() + "/protocol/openid-connect/logout";
        String clientId = appProperties.getKeycloakClientId();
        String secret = appProperties.getKeycloakSecret();

        ClientIdAndSecretCredentialsProvider clientIdAndSecretCredentialsProvider = new ClientIdAndSecretCredentialsProvider();
        clientIdAndSecretCredentialsProvider.init(cfg, cfg);
        Http http = new Http(cfg, clientIdAndSecretCredentialsProvider);

        http.<Void>post(url).authentication().client().form().param("grant_type", "refresh_token").param("refresh_token", refreshToken)
                .param("client_id", clientId).param("client_secret", secret).response().execute();
    }

    public String loginPageKeyCloak(HttpServletRequest request) {
        String url = appProperties.getKeycloakUrl() + "/realms/" + appProperties.getKeycloakRealm() + "/protocol/openid-connect/auth?client_id="
                + appProperties.getKeycloakClientId() + "&response_type=code&login=true&redirect_uri="
                + request.getRequestURL().toString().replace(request.getRequestURI(), "") + "/api/login-sso-complete";

        return url;
    }

    public String loginSuccessPage(HttpServletRequest request) {
        String url = request.getRequestURL().toString().replace(request.getRequestURI(), "") + "/#/login-sso-complete";

        return url;
    }

    @TransactionalWrite
    public void refreshRoles(AppUserDto appUserDto, HttpServletRequest request) throws Exception {

        AuthCookieDto authCookieDto = securityUtils.extractAuthCookieDto(request);

        KeyCloakPayloadDto keyCloakPayloadDto = new KeyCloakPayloadDto();
        keyCloakPayloadDto = getRoles(authCookieDto.getAccessToken(), keyCloakPayloadDto);
        Uzivatel uzivatel = uzivatelRepository.getDetail(appUserDto.getIdUzivatel());
        if (uzivatel == null)
            return;

        List<Opravneni> listOpravneni = new ArrayList<Opravneni>();
        List<String> opravneniString = keyCloakPayloadDto.getRole();

        if (opravneniString != null) {
            for (String role : opravneniString) {
                Opravneni opravneni = opravneniRepository.getDetailByKod(role.toUpperCase(), uzivatel.getZavod().getIdZavod(), true);
                if (opravneni != null)
                    listOpravneni.add(opravneni);
            }
        }

        uzivatel.setModuly(keyCloakPayloadDto.getModuly());
        uzivatel.setOpravneni(listOpravneni);
        uzivatel.setZmenuProvedl(Utils.getZmenuProv());
        uzivatel.setCasZmn(Utils.getCasZmn());
        uzivatel = uzivatelServices.save(uzivatel, true, false, true);
    }

    @SuppressWarnings("unchecked")
    public KeyCloakPayloadDto getRoles(String accesToken, KeyCloakPayloadDto keyCloakPayloadDto) {

        keyCloakPayloadDto.setRole(new ArrayList<String>());
        keyCloakPayloadDto.setModuly(new ArrayList<String>());

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + accesToken);
        String url = appProperties.getKeycloakUrl() + "/realms/" + appProperties.getKeycloakRealm() + "/protocol/openid-connect/userinfo";
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        UserInfo userInfo = restKeyCloak.postForObject(url, request, UserInfo.class);

        if (userInfo != null && userInfo.getOtherClaims() != null) {

            ArrayList<String> role = new ArrayList<String>();
            if (userInfo.getOtherClaims().get("roles") != null && userInfo.getOtherClaims().get("roles").getClass() == role.getClass()) {
                role = (ArrayList<String>) userInfo.getOtherClaims().get("roles");
                if (role != null && role.size() > 0) {
                    for (String r : role) {
                        if (r.toUpperCase().contains(Constants.SCHEMA.toUpperCase() + "_"))
                            keyCloakPayloadDto.getRole().add(r);
                        if (r.contains("_")) {
                            String[] array = r.split("_");
                            if (array.length > 0) {
                                if (!keyCloakPayloadDto.getModuly().contains(array[0]))
                                    keyCloakPayloadDto.getModuly().add(array[0]);
                            }
                        }
                    }
                }

            }

        }

        return keyCloakPayloadDto;
    }

    @TransactionalWrite
    public ResponseEntity<AppUserDto> zmenaZastupu(AppUserDto appUserDto, String idZastupu, HttpServletRequest request, HttpServletResponse httpServletResponse)
            throws JsonProcessingException, BaseException {

        // zrušení zástupu
        if (StringUtils.isBlank(idZastupu) || idZastupu.equals(appUserDto.getIdUzivatel()))
            idZastupu = null;
        else {
            if (appUserDto.getZastupy() != null) {
                boolean nalezeno = false;
                for (ZastupSimpleDto zastup : appUserDto.getZastupy()) {
                    if (idZastupu.equals(zastup.getIdUzivatel())) {
                        nalezeno = true;
                        break;
                    }
                }

                if (!nalezeno)
                    idZastupu = null;
            }
        }

        Uzivatel uzivatel = uzivatelRepository.getDetail(appUserDto.getIdUzivatel());
        if (!Utils.toString(idZastupu).equals(Utils.toString(uzivatel.getIdZastup()))) {
            uzivatel.setIdZastup(idZastupu);
            uzivatel.setCasZmn(Utils.getCasZmn());
            uzivatel.setZmenuProvedl(Utils.getZmenuProv());
            uzivatel = uzivatelRepository.save(uzivatel);
        }
        return loginKeyCloak(uzivatel, request, httpServletResponse);

    }

    public AccessTokenResponse loginKeyCloakAdminUser() throws ValidationException, NoSuchMessageException {

        Databaze databaze = databazeServices.getDetail();
        if (StringUtils.isBlank(databaze.getKcUzivateleJmeno()) || StringUtils.isBlank(databaze.getKcUzivateleHeslo()))
            throw new ValidationException(messageSource.getMessage("kc.admin.user.null", null, LocaleContextHolder.getLocale()));

        Configuration cfg = new Configuration();
        cfg.setAuthServerUrl(appProperties.getKeycloakUrl());
        cfg.setResource("admin-cli");
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("secret", appProperties.getKeycloakSecret());
        cfg.setCredentials(credentials);
        // cfg.setRealm(appProperties.getKeycloakRealm());
        cfg.setRealm("master");// musí být master
        AuthzClient authzClient = AuthzClient.create(cfg);

        AccessTokenResponse accessTokenResponse = authzClient.obtainAccessToken(databaze.getKcUzivateleJmeno(),
                Utils.textDecrypted(databaze.getKcUzivateleHeslo()));

        return accessTokenResponse;
    }

    public List<String> getRoleZastupu(String idZastupu) {

        try {

            // načtení detailu
            Uzivatel uzivatel = uzivatelRepository.getDetail(idZastupu);
            if (uzivatel == null)
                return null;

            String username = String.format("%s-%s", uzivatel.getZavod().getSapId(), uzivatel.getSapId()).toLowerCase();

            // acces token admin účtu
            AccessTokenResponse accessTokenResponse = loginKeyCloakAdminUser();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Authorization", "Bearer " + accessTokenResponse.getToken());
            String url = appProperties.getKeycloakUrl() + "/admin/realms/" + appProperties.getKeycloakRealm() + "/users?username=" + username;
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
            ResponseEntity<UzivatelKcDto[]> kcUzivatel = restKeyCloak.exchange(url, HttpMethod.GET, request, UzivatelKcDto[].class, new HashMap<>());

            if (kcUzivatel != null && kcUzivatel.getBody() != null && kcUzivatel.getBody().length == 1
                    && !StringUtils.isBlank(kcUzivatel.getBody()[0].getId())) {

                // získání rolí
                url = appProperties.getKeycloakUrl() + "/admin/realms/" + appProperties.getKeycloakRealm() + "/users/" + kcUzivatel.getBody()[0].getId()
                        + "/role-mappings/realm/composite";
                ResponseEntity<RoleKcDto[]> roleKc = restKeyCloak.exchange(url, HttpMethod.GET, request, RoleKcDto[].class, new HashMap<>());
                if (roleKc != null && roleKc.getBody() != null && roleKc.getBody().length > 0) {
                    List<String> roleZastupu = new ArrayList<String>();
                    for (RoleKcDto role : Arrays.asList(roleKc.getBody())) {
                        roleZastupu.add(role.getName());
                    }
                    return roleZastupu;
                } else
                    return null;

            } else
                return null;

        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }
}
