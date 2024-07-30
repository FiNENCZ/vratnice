package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.UzivatelVratnice;
import cz.diamo.vratnice.entity.Vratnice;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UzivatelVratniceDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String id;

    private UzivatelDto uzivatel;

    private List<VratniceDto> vratnice;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public UzivatelVratniceDto(UzivatelVratnice uzivatelVratnice) {
        if (uzivatelVratnice == null) {
            return;
        }

        this.id = uzivatelVratnice.getIdUzivatelVratnice();
        this.uzivatel = new UzivatelDto(uzivatelVratnice.getUzivatel());

        List<VratniceDto> vratniceDtos = new ArrayList<>();
        if (uzivatelVratnice.getVratnice() != null) {
            for (Vratnice vratnice: uzivatelVratnice.getVratnice()) {
                vratniceDtos.add(new VratniceDto(vratnice));
            }
        }
        this.setVratnice(vratniceDtos);

        this.aktivita = uzivatelVratnice.getAktivita();
    }

    public UzivatelVratnice toEntity() {
        UzivatelVratnice uzivatelVratnice = new UzivatelVratnice();

        uzivatelVratnice.setIdUzivatelVratnice(this.id);
        uzivatelVratnice.setUzivatel(new Uzivatel(getUzivatel().getId()));

        List<Vratnice> vratnice = new ArrayList<>();
        if (getVratnice() != null) {
            for (VratniceDto vratniceDto: this.getVratnice()) {
                vratnice.add(new Vratnice(vratniceDto.getId()));
            }
        }
        uzivatelVratnice.setVratnice(vratnice);

        uzivatelVratnice.setAktivita(this.aktivita);

        return uzivatelVratnice;
    }

    

}
