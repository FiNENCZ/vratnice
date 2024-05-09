package cz.diamo.share.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import cz.diamo.share.configuration.AppProperties;
import cz.diamo.share.constants.Constants;
import cz.diamo.share.dto.keycloak.KeyCloakPayloadDto;
import cz.diamo.share.dto.security.AuthCookieDto;
import cz.diamo.share.enums.AuthCookieTypeEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecurityUtils {

    final static Logger logger = LogManager.getLogger(SecurityUtils.class);

    public static String cookieName = Constants.BASE_PACKAGE + "_auth";
    public static String cookieNameExp = Constants.BASE_PACKAGE + "_auth_exp";
    // public static String cookieName = "diamo_auth";
    // public static String cookieNameExp = "diamo_auth_exp";

    @Autowired
    private AppProperties appProperties;

    public KeyCloakPayloadDto extractPayload(String accessToken) {
        // String[] chunks = accessToken.split("\\.");
        // Base64.Decoder decoder = Base64.getUrlDecoder();
        // String payload = new String(decoder.decode(chunks[1]),
        // StandardCharsets.UTF_8);
        // KeyCloakPayloadDto keyCloakPayload = new Gson().fromJson(payload,
        // KeyCloakPayloadDto.class);
        // return keyCloakPayload;
        try {

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(
                    appProperties.getKeycloakSigningPublicKey()));
            KeyFactory kf = KeyFactory.getInstance(appProperties.getKeycloakSigningKeyAlgType());
            PublicKey pk = kf.generatePublic(keySpec);

            Claims claims = Jwts.parser().setSigningKey(pk)
                    .parseClaimsJws(accessToken).getBody();
            KeyCloakPayloadDto keyCloakPayloadDto = new KeyCloakPayloadDto(claims);
            return keyCloakPayloadDto;

        } catch (ExpiredJwtException ee) {
            if (ee.getClaims() != null) {
                KeyCloakPayloadDto keyCloakPayloadDto = new KeyCloakPayloadDto(ee.getClaims());
                keyCloakPayloadDto.setExpire(true);
                return keyCloakPayloadDto;
            } else
                throw ee;

        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public Date getExpDateRefreshToken(String refreshToken) {
        try {
            String token = refreshToken.substring(0, refreshToken.lastIndexOf('.') + 1);
            Claims claims = Jwts.parser().parseClaimsJwt(token).getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }

    }

    public Cookie generateAuthCookieKeyCloak(AccessTokenResponse accessTokenResponse) {
        return generateAuthCookieKeyCloak(accessTokenResponse, SecurityUtils.cookieName);
    }

    public Cookie generateAuthCookieKeyCloak(AccessTokenResponse accessTokenResponse, String cookieName) {
        AuthCookieDto authCookieDto = new AuthCookieDto(AuthCookieTypeEnum.KeyCloak);
        authCookieDto.setAccessToken(accessTokenResponse.getToken());
        authCookieDto.setRefreshToken(accessTokenResponse.getRefreshToken());
        Cookie cookie = new Cookie(cookieName,
                Base64.getEncoder().encodeToString(new Gson().toJson(authCookieDto).getBytes(StandardCharsets.UTF_8)));

        cookie.setMaxAge((int) accessTokenResponse.getRefreshExpiresIn());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie generateAuthCookieExpKeyCloak(AccessTokenResponse accessTokenResponse) {

        Date expDate = getExpDateRefreshToken(accessTokenResponse.getRefreshToken());
        // Cookie cookieExp = new Cookie(SecurityUtils.cookieNameExp,
        // String.valueOf(payloadRefresh.getExp().intValue() * 1000L));
        Cookie cookieExp = new Cookie(SecurityUtils.cookieNameExp,
                String.valueOf(expDate.getTime()));
        cookieExp.setMaxAge((int) accessTokenResponse.getRefreshExpiresIn());
        cookieExp.setHttpOnly(false);
        cookieExp.setPath("/");
        return cookieExp;
    }

    public AuthCookieDto extractAuthCookieDto(HttpServletRequest request) {
        AuthCookieDto authCookieDto = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(SecurityUtils.cookieName)) {
                    authCookieDto = new Gson().fromJson(new String(Base64.getDecoder().decode(cookie.getValue())),
                            AuthCookieDto.class);
                    break;
                }
            }
        }
        return authCookieDto;
    }
}