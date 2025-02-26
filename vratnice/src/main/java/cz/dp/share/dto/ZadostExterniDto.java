package cz.dp.share.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZadostExterniDto {

    @NotBlank(message = "{id.require}")
    private String id;

    @NotBlank(message = "{sap.id.require}")
    private String sapIdZamestnance;

    @NotBlank(message = "{zadost.sap.id.vytvoril.require}")
    private String sapIdVytvoril;

    @NotNull(message = "{datum.predani.require}")
    private Date datumPredani;

    @NotBlank(message = "{typ.require}")
    private String typ;
}
