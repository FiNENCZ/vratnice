package cz.diamo.share.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.entity.Zavod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LokalitaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String idExterni;

    @NotBlank(message = "{lokalita.nazev.require}")
    @Size(max = 80, message = "{lokalita.nazev.max.80}")
    private String nazev;

    private String poznamka;

    private Boolean aktivita;

    @NotNull(message = "{lokalita.zavod.require}")
    private ZavodDto zavod;
    

    public LokalitaDto(Lokalita lokalita) {
        if (lokalita == null) {
            return;
        }

        setId(lokalita.getIdLokalita());
        setIdExterni(lokalita.getIdExterni());
        setNazev(lokalita.getNazev());
        setPoznamka(lokalita.getPoznamka());
        setAktivita(lokalita.getAktivita());
        setZavod(new ZavodDto(lokalita.getZavod()));
    }

    @JsonIgnore
    public Lokalita getLokalita(Lokalita lokalita, boolean pouzeId) {
        if (lokalita == null)
            lokalita = new Lokalita();

        lokalita.setIdLokalita(getId());
        lokalita.setIdExterni(getIdExterni());

        if (!pouzeId) {
            lokalita.setNazev(getNazev());
            lokalita.setPoznamka(getPoznamka());
            lokalita.setAktivita(getAktivita());
            lokalita.setZavod(new Zavod(getZavod().getId()));
        }

        return lokalita;
    }
}
