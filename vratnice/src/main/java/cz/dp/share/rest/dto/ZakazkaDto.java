package cz.dp.share.rest.dto;

import java.io.Serializable;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Zakázka
 */
@Data
@NoArgsConstructor
public class ZakazkaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 100, message = "{sap.id.max.100}")
    @NotBlank(message = "{sap.id.require}")
    private String sapIdZakazky;

    @Size(max = 1000, message = "{nazev.max.1000}")
    // @NotBlank(message = "{nazev.require}") 2024-02-21 - M.Ch, můžou být bez
    // názvu, pak doplním sapID do názvu
    private String nazev;

    private Date platnostOd;

    private Date platnostDo;
}
