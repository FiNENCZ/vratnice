package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.HistorieKlic;
import cz.diamo.vratnice.entity.Klic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistorieKlicDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idHistorieKlic;

    private KlicDto klic;

    @NotNull(message = "{historie_klic.akce.require}")
    private HistorieKlicAkceDto akce;

    @NotBlank(message =  "{historie_klic.duvod.require}")
    private String duvod;

    private Date datum;

    private UzivatelDto uzivatel;

    public HistorieKlicDto(HistorieKlic historieKlic) {
        if (historieKlic == null) {
            return;
        }

        this.idHistorieKlic = historieKlic.getIdHistorieKlic();
        this.klic = new KlicDto(historieKlic.getKlic());
        this.akce = new HistorieKlicAkceDto(historieKlic.getAkce());
        this.duvod = historieKlic.getDuvod();
        this.datum = historieKlic.getDatum();
        this.uzivatel = new UzivatelDto(historieKlic.getUzivatel());
    }

    public HistorieKlic toEntity() {
        HistorieKlic historieKlic = new HistorieKlic();

        historieKlic.setIdHistorieKlic(this.idHistorieKlic);
        historieKlic.setKlic(new Klic(getKlic().getIdKlic()));
        historieKlic.setAkce(getAkce().toEntity());
        historieKlic.setDuvod(this.duvod);
        historieKlic.setDatum(this.datum);
        
        if (historieKlic.getUzivatel() != null)
            historieKlic.setUzivatel(new Uzivatel(getUzivatel().getId()));

        return historieKlic;
    }

}
