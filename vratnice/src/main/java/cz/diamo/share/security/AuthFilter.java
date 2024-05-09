package cz.diamo.share.security;

import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;

import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.keycloak.KeyCloakPayloadDto;
import cz.diamo.share.dto.security.AuthCookieDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.ZastupSimple;
import cz.diamo.share.enums.AuthCookieTypeEnum;
import cz.diamo.share.repository.UzivatelModulRepository;
import cz.diamo.share.repository.UzivatelOpravneniRepository;
import cz.diamo.share.repository.UzivatelRepository;
import cz.diamo.share.repository.UzivatelZavodRepository;
import cz.diamo.share.services.AuthServices;
import cz.diamo.share.services.ZastupServices;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    final static Logger logger = LogManager.getLogger(AuthFilter.class);

    private final SecurityUtils securityUtils;

    private final UzivatelRepository uzivatelRepository;

    private final AuthServices authServices;

    private final UzivatelOpravneniRepository uzivatelOpravneniRepository;

    private final UzivatelZavodRepository uzivatelZavodRepository;

    private final UzivatelModulRepository uzivatelModulRepository;

    private final ZastupServices zastupServices;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuthCookieDto authCookieDto = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(SecurityUtils.cookieName)) {
                    authCookieDto = new Gson().fromJson(new String(Base64.getDecoder().decode(cookie.getValue())), AuthCookieDto.class);
                    break;
                }
            }
        }

        // if (authCookieDto != null && authCookieDto.getType() ==
        // AuthCookieTypeEnum.JWT)
        // doFilterInternalJwt(request, response, authCookieDto, filterChain);
        // else
        if (authCookieDto != null && authCookieDto.getType() == AuthCookieTypeEnum.KeyCloak)
            doFilterInternalKeyCloak(request, response, authCookieDto, filterChain);
        else
            filterChain.doFilter(request, response);
    }

    private void doFilterInternalKeyCloak(HttpServletRequest request, HttpServletResponse response, AuthCookieDto authCookieDto, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            Integer indexOf = request.getRequestURL().toString().indexOf("/api/konfigurace");
            if (!request.getRequestURL().toString().contains("login-token")
                    && (indexOf == -1 || indexOf + "/api/konfigurace".length() + 1 != request.getRequestURL().toString().length())) {
                // && !request.getRequestURL().toString().contains("/api/konfigurace")) {
                KeyCloakPayloadDto keyCloakPayloadDto = securityUtils.extractPayload(authCookieDto.getAccessToken());
                if (keyCloakPayloadDto != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    Uzivatel uzivatel = uzivatelRepository.getDetailBySapId(keyCloakPayloadDto.getSapid());

                    // donačtení rolí
                    uzivatel.setRole(uzivatelOpravneniRepository.listRole(uzivatel.getIdUzivatel(), true));

                    // donačtení závodů
                    uzivatel.setOstatniZavody(uzivatelZavodRepository.listZavod(uzivatel.getIdUzivatel()));

                    // donačtení modulů
                    uzivatel.setModuly(uzivatelModulRepository.listModul(uzivatel.getIdUzivatel()));

                    KeyCloakPayloadDto payloadAccess = securityUtils.extractPayload(authCookieDto.getAccessToken());
                    Date expDate = securityUtils.getExpDateRefreshToken(authCookieDto.getRefreshToken());

                    if (payloadAccess != null && expDate != null) {
                        AppUserDto appUserDto = null;
                        // token je platný
                        // if (payloadAccess.getExpDate().after(Calendar.getInstance().getTime())) {
                        if (!payloadAccess.getExpire()) {
                            // uzivatel.setAplikace(payloadAccess.getModules());
                            List<ZastupSimple> zastupy = zastupServices.getListDostupne(uzivatel.getIdUzivatel(), uzivatel.getIdZastup());
                            if (!StringUtils.isBlank(uzivatel.getIdZastup()))
                                uzivatel.setZastup(uzivatelRepository.getDetail(uzivatel.getIdZastup()));
                            appUserDto = new AppUserDto(uzivatel, zastupy);
                            // refresh
                        } else if (expDate.after(Calendar.getInstance().getTime())) {

                            AccessTokenResponse accessTokenResponse = authServices.refreshToken(authCookieDto.getRefreshToken());
                            appUserDto = authServices.loginKeyCloak(accessTokenResponse, response).getBody();
                        }

                        if (appUserDto != null) {
                            // UsernamePasswordAuthenticationToken authToken = new
                            // UsernamePasswordAuthenticationToken(
                            // appUserDto,
                            // null, appUserDto.getAuthorities());
                            // authToken.setDetails(new
                            // WebAuthenticationDetailsSource().buildDetails(request));
                            Authentication authentication = new UserAuthentication(appUserDto);
                            // SecurityContextHolder.getContext().setAuthentication(authToken);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }

                }
            }

        } catch (Exception e) {
            logger.info(e);
        }

        filterChain.doFilter(request, response);
    }

}