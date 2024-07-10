package cz.diamo.vratnice.dto;

import java.io.Serializable;

import cz.diamo.vratnice.entity.Lokalita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LokalitaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank(message = "{lokalita.nazev.require}")
    @Size(message = "{lokalita.nazev.max.80}")
    private String nazev;
    

    public LokalitaDto(Lokalita lokalita) {
        if (lokalita == null) {
            return;
        }

        this.id = lokalita.getIdLokalita();
        this.nazev = lokalita.getNazev();
    }

    public Lokalita toEntity() {
        Lokalita lokalita = new Lokalita();

        lokalita.setIdLokalita(this.id);
        lokalita.setNazev(this.nazev);

        return lokalita;
    }

}
