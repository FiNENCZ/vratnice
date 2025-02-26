package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.dp.share.dto.UzivatelDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.vratnice.entity.UzivatelVratnice;
import cz.dp.vratnice.entity.Vratnice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UzivatelVratniceDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String id;

    @NotNull(message = "{uzivatel_vratnice.uzivatel.require}")
    @Valid
    private UzivatelDto uzivatel;

    @NotNull(message = "{uzivatel_vratnice.vratnice.require}")
    @Valid
    private List<VratniceDto> vratnice;

    private VratniceDto nastavenaVratnice;

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

        if (uzivatelVratnice.getNastavenaVratnice() != null)
            this.nastavenaVratnice = new VratniceDto(uzivatelVratnice.getNastavenaVratnice());

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

        if (getNastavenaVratnice() != null )
            uzivatelVratnice.setNastavenaVratnice(new Vratnice(getNastavenaVratnice().getId()));

        uzivatelVratnice.setAktivita(this.aktivita);

        return uzivatelVratnice;
    }

    

}
