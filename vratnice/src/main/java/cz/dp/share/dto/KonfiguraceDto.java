package cz.dp.share.dto;

import java.io.Serializable;

import cz.dp.share.enums.ColorSchemeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KonfiguraceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean demo;

    private String verzeApi;

    private String verzeDb;

    private ColorSchemeEnum colorScheme;

    private String portalUrl;

    private String zmenaHeslaUrl;

}
