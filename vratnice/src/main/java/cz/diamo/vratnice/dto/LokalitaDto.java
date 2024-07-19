package cz.diamo.vratnice.dto;

import java.io.Serializable;

import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Zavod;
import cz.diamo.vratnice.entity.Lokalita;
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

    @NotBlank(message = "{lokalita.nazev.require}")
    @Size(max = 80, message = "{lokalita.nazev.max.80}")
    private String nazev;

    @NotNull(message = "{lokalita.zavod.require}")
    private ZavodDto zavod;
    

    public LokalitaDto(Lokalita lokalita) {
        if (lokalita == null) {
            return;
        }

        this.id = lokalita.getIdLokalita();
        this.nazev = lokalita.getNazev();
        this.zavod = new ZavodDto(lokalita.getZavod());
    }

    public Lokalita toEntity() {
        Lokalita lokalita = new Lokalita();

        lokalita.setIdLokalita(this.id);
        lokalita.setNazev(this.nazev);
        lokalita.setZavod(new Zavod(getZavod().getId()));

        return lokalita;
    }

}
