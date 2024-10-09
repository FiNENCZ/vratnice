package cz.diamo.vratnice.edos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RucniZaznamSnimaceDto {

    @NotBlank(message = "{id.snimac.require}")
    private String idSnimac;

    @NotBlank(message = "{sapid.zamestnanec.require}")
    private String sapIdZamestnanec;

    @NotNull(message = "{akce.require}")
    private Integer idAkce;

    public RucniZaznamSnimaceDto(String idSnimac, String sapIdZamestnanec, Integer idAkce) {
        this.idSnimac = idSnimac;
        this.sapIdZamestnanec = sapIdZamestnanec;
        this.idAkce = idAkce;
    }
}
