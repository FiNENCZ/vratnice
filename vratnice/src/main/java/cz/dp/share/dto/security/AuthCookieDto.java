package cz.dp.share.dto.security;

import cz.dp.share.enums.AuthCookieTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthCookieDto {

    private AuthCookieTypeEnum type = AuthCookieTypeEnum.JWT;

    private String accessToken;

    private String refreshToken;

    public AuthCookieDto(AuthCookieTypeEnum typ) {
        setType(typ);
    }
}
