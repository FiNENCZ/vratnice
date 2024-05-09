package cz.diamo.share.dto;

import java.io.Serializable;
import java.util.Date;

import cz.diamo.share.entity.Zastup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ws02ZastupDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "{guid.require}")
    private String guid;

    @NotBlank(message = "{sapid.zastupce.require}")
    private String sapIdZastupce;

    @NotBlank(message = "{sapid.zastupovany.require}")
    private String sapIdZastupovany;

    @NotNull(message = "{platnost.od.require}")
    private Date platnostOd;

    @NotNull(message = "{platnost.do.require}")
    private Date platnostDo;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita;

    public Ws02ZastupDto(Zastup zastup) {
        setGuid(zastup.getGuid());
        setSapIdZastupce(zastup.getUzivatelZastupce().getSapId());
        setSapIdZastupovany(zastup.getUzivatel().getSapId());
        setPlatnostOd(zastup.getPlatnostOd());
        setPlatnostDo(zastup.getPlatnostDo());
        setAktivita(zastup.getAktivita());
    }
}
