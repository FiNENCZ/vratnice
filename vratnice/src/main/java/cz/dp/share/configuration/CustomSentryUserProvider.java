package cz.dp.share.configuration;

import java.util.HashMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import cz.dp.share.dto.AppUserDto;
import io.micrometer.common.util.StringUtils;
import io.sentry.protocol.User;
import io.sentry.spring.jakarta.SentryUserProvider;

@Component
class CustomSentryUserProvider implements SentryUserProvider {
    public User provideUser() {
        User user = new User();
        if (user.getData() == null)
            user.setData(new HashMap<String, String>());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDto) {
            AppUserDto appUser = (AppUserDto) authentication.getPrincipal();
            if (appUser != null) {
                user.setId(appUser.getIdUzivatel());
                user.setUsername(appUser.getName());
                if (!StringUtils.isBlank(appUser.getSapId()))
                    user.getData().put("Sap Id", appUser.getSapId());
                if (appUser.getZavod() != null) {
                    if (!StringUtils.isBlank(appUser.getZavod().getId()))
                        user.getData().put("Závod - Id", appUser.getZavod().getId());
                    if (!StringUtils.isBlank(appUser.getZavod().getNazev()))
                        user.getData().put("Závod - Název", appUser.getZavod().getNazev());
                }

            }

        }

        return user;
    }
}
