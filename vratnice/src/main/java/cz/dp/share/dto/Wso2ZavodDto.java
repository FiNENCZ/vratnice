package cz.dp.share.dto;

import java.io.Serializable;

import cz.dp.share.entity.Zavod;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Wso2ZavodDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "{sap.id.require}")
    private String sapId;

    private String nazev;

    public Wso2ZavodDto(Zavod zavod) {
        setSapId(zavod.getSapId());
        setNazev(zavod.getNazev());
    }
}
