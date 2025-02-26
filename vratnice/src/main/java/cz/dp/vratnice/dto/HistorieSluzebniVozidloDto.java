package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.dp.share.dto.UzivatelDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.vratnice.entity.HistorieSluzebniVozidlo;
import cz.dp.vratnice.entity.SluzebniVozidlo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistorieSluzebniVozidloDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idHistorieSluzebniAuto;

    private SluzebniVozidloDto sluzebniVozidlo;

    @NotNull(message = "{historie_sluzebni_vozidlo.akce.require}")
    private HistorieSluzebniVozidloAkceDto akce;

    private Date datum;

    private UzivatelDto uzivatel;

    public HistorieSluzebniVozidloDto(HistorieSluzebniVozidlo historieSluzebniVozidlo) {
        if (historieSluzebniVozidlo == null) {
            return;
        }

        this.idHistorieSluzebniAuto = historieSluzebniVozidlo.getIdHistorieSluzebniAuto();
        this.sluzebniVozidlo = new SluzebniVozidloDto(historieSluzebniVozidlo.getSluzebniVozidlo());
        this.akce = new HistorieSluzebniVozidloAkceDto(historieSluzebniVozidlo.getAkce());
        this.datum = historieSluzebniVozidlo.getDatum();
        this.uzivatel = new UzivatelDto(historieSluzebniVozidlo.getUzivatel());
    }

    public HistorieSluzebniVozidlo toEntity() {
        HistorieSluzebniVozidlo historieSluzebniVozidlo = new HistorieSluzebniVozidlo();

        historieSluzebniVozidlo.setIdHistorieSluzebniAuto(this.idHistorieSluzebniAuto);
        historieSluzebniVozidlo.setSluzebniVozidlo(new SluzebniVozidlo(getSluzebniVozidlo().getIdSluzebniVozidlo()));
        historieSluzebniVozidlo.setAkce(getAkce().toEntity());
        historieSluzebniVozidlo.setDatum(this.datum);
        historieSluzebniVozidlo.setUzivatel(new Uzivatel(getUzivatel().getId()));

        return historieSluzebniVozidlo;
    }


}
