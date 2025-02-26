package cz.dp.share.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.entity.Lokalita;
import cz.dp.share.entity.Zavod;
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

    private String kod;

    @NotBlank(message = "{lokalita.nazev.require}")
    @Size(max = 80, message = "{lokalita.nazev.max.80}")
    private String nazev;

    private String poznamka;

    private Boolean aktivita;

    private Boolean verejne;

    @NotNull(message = "{lokalita.zavod.require}")
    private ZavodDto zavod;
    

    public LokalitaDto(Lokalita lokalita) {
        if (lokalita == null) {
            return;
        }

        setId(lokalita.getIdLokalita());
        setKod(lokalita.getKod());
        setNazev(lokalita.getNazev());
        setPoznamka(lokalita.getPoznamka());
        setAktivita(lokalita.getAktivita());
        setVerejne(lokalita.getVerejne());
        setZavod(new ZavodDto(lokalita.getZavod()));
    }

    @JsonIgnore
    public Lokalita getLokalita(Lokalita lokalita, boolean pouzeId) {
        if (lokalita == null)
            lokalita = new Lokalita();

        lokalita.setIdLokalita(getId());
        lokalita.setKod(getKod());

        if (!pouzeId) {
            lokalita.setNazev(getNazev());
            lokalita.setPoznamka(getPoznamka());
            lokalita.setAktivita(getAktivita());
            lokalita.setVerejne(getVerejne());
            lokalita.setZavod(new Zavod(getZavod().getId()));
        }

        return lokalita;
    }
}
