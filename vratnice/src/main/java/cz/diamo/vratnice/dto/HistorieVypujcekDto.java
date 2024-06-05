package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.HistorieVypujcek;
import cz.diamo.vratnice.entity.ZadostKlic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistorieVypujcekDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idHistorieVypujcek;
    
    private ZadostKlicDto zadostKlic;

    @NotBlank(message = "Building is required")
    @Size(max = 30, message = "State cannot exceed 30 characters")
    private String stav;

    private Date datum;

    private UzivatelDto vratny;

    public HistorieVypujcekDto(HistorieVypujcek historieVypujcek) {
        if (historieVypujcek == null) {
            return;
        }

        this.idHistorieVypujcek = historieVypujcek.getIdHistorieVypujcek();
        this.zadostKlic = new ZadostKlicDto(historieVypujcek.getZadostKlic());
        this.stav = historieVypujcek.getStav();
        this.datum = historieVypujcek.getDatum();
        this.vratny = new UzivatelDto(historieVypujcek.getVratny());
    }

    public HistorieVypujcek toEntity() {
        HistorieVypujcek historieVypujcek = new HistorieVypujcek();

        historieVypujcek.setIdHistorieVypujcek(this.idHistorieVypujcek);
        historieVypujcek.setZadostKlic(new ZadostKlic(getZadostKlic().getIdZadostiKey()));
        historieVypujcek.setStav(this.stav);
        historieVypujcek.setDatum(this.datum);
        historieVypujcek.setVratny(new Uzivatel(getVratny().getId()));

        return historieVypujcek;
    }



}
