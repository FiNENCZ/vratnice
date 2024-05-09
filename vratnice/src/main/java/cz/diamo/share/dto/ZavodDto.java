package cz.diamo.share.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.entity.Zavod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZavodDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @Size(max = 100, message = "{sapid.max.100}")
    private String sapId;

    @Size(max = 1000, message = "{nazev.max.1000}")
    @NotBlank(message = "{nazev.require}")
    private String nazev;

    private String barvaPozadi;

    private String barvaPisma;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita;

    public ZavodDto(Zavod zavod) {
        if (zavod == null)
            return;
        setId(zavod.getIdZavod());
        setSapId(zavod.getSapId());
        setNazev(zavod.getNazev());
        setBarvaPisma(zavod.getBarvaPisma());
        setBarvaPozadi(zavod.getBarvaPozadi());
        setAktivita(zavod.getAktivita());
    }

    @JsonIgnore
    public Zavod getZavod(Zavod zavod, boolean pouzeId) {
        if (zavod == null)
            zavod = new Zavod();

        zavod.setIdZavod(getId());
        if (!pouzeId) {
            zavod.setSapId(getSapId());
            zavod.setNazev(getNazev());
            zavod.setBarvaPisma(getBarvaPisma());
            zavod.setBarvaPozadi(getBarvaPozadi());
            zavod.setAktivita(getAktivita());
        }
        return zavod;

    }
}
