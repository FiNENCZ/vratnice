package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.dp.share.dto.UzivatelDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.vratnice.entity.HistorieVypujcek;
import cz.dp.vratnice.entity.ZadostKlic;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistorieVypujcekDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idHistorieVypujcek;
    
    private ZadostKlicDto zadostKlic;

    @NotNull(message = "{historie_vypujcek.stav.require}")
    private HistorieVypujcekAkceDto akce;

    private Date datum;

    private UzivatelDto vratny;

    public HistorieVypujcekDto(HistorieVypujcek historieVypujcek) {
        if (historieVypujcek == null) {
            return;
        }

        this.idHistorieVypujcek = historieVypujcek.getIdHistorieVypujcek();
        this.zadostKlic = new ZadostKlicDto(historieVypujcek.getZadostKlic());

        if (historieVypujcek.getAkce() != null)
            this.akce = new HistorieVypujcekAkceDto(historieVypujcek.getAkce());

        this.datum = historieVypujcek.getDatum();
        this.vratny = new UzivatelDto(historieVypujcek.getVratny());
    }

    public HistorieVypujcek toEntity() {
        HistorieVypujcek historieVypujcek = new HistorieVypujcek();

        historieVypujcek.setIdHistorieVypujcek(this.idHistorieVypujcek);
        historieVypujcek.setZadostKlic(new ZadostKlic(getZadostKlic().getId()));

        if (getAkce() != null )
            historieVypujcek.setAkce(getAkce().toEntity());

        historieVypujcek.setDatum(this.datum);
        historieVypujcek.setVratny(new Uzivatel(getVratny().getId()));

        return historieVypujcek;
    }



}
