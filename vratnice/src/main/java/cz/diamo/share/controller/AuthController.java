package cz.diamo.share.controller;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.share.security.UserAuthentication;
import cz.diamo.share.services.AuthServices;
import cz.diamo.share.services.UzivatelServices;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api")
public class AuthController {

    final static Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthServices authServices;

    @Autowired
    private UzivatelServices uzivatelServices;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/authenticate")
    @PreAuthorize("isFullyAuthenticated()")
    public AppUserDto authenticate(@Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto) {
        return appUserDto;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login(HttpServletRequest request, HttpServletResponse response)
            throws JsonProcessingException {
        try {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(authServices.loginPageKeyCloak(request)))
                    .build();
        } catch (Exception ex) {
            logger.error(ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/login-sso-complete")
    public ResponseEntity<Void> loginSsoComplete(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String code, @RequestParam(name = "session_state") String sessionState)
            throws JsonProcessingException {
        try {
            authServices.loginKeyCloakByAuthorizationCode(code, request, response);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(authServices.loginSuccessPage(request)))
                    .build();
        } catch (Exception ex) {
            logger.error(ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/username-by-rfid")
    public ResponseEntity<String> usernameByRfid(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String rfid) {
        try {
            return ResponseEntity.ok().body(uzivatelServices.getUsername(rfid));
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
        }
    }

    @GetMapping("/username-by-rfid-uid")
    public ResponseEntity<String> usernameByRfidUid(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String rfid) {
        try {
            if (StringUtils.isBlank(rfid) || rfid.length() != 7)
                throw new ValidationException(
                        messageSource.getMessage("rfid.uid.spatny.format", null,
                                LocaleContextHolder.getLocale()));
            return ResponseEntity.ok().body(uzivatelServices.getUsernameByRfidUid(rfid));
        } catch (Exception e) {
            logger.error(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
        }
    }

    @PostMapping("/zmena-zavodu")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<AppUserDto> zmenaZavodu(HttpServletRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, String idZavodu)
            throws JsonProcessingException {
        try {
            return authServices.zmenaZavodu(appUserDto, idZavodu);
        } catch (Exception ex) {
            logger.error(ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/zmena-zastupu")
    public ResponseEntity<AppUserDto> zmenaZastupu(HttpServletRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDto appUserDto, String idZastupu,
            HttpServletResponse response)
            throws JsonProcessingException {
        try {
            response.reset();

            // změna profilu
            ResponseEntity<AppUserDto> responseEntity = authServices.zmenaZastupu(appUserDto,
                    idZastupu,
                    request, response);

            if (responseEntity != null && responseEntity.getBody() != null) {
                Authentication authentication = new UserAuthentication(responseEntity.getBody());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            return responseEntity;

        } catch (BaseException ex) {
            logger.error(ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.toString());
        }
    }

}