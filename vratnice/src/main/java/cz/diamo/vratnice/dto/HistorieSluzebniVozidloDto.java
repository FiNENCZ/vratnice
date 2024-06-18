package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.HistorieSluzebniVozidlo;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistorieSluzebniVozidloDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idHistorieSluzebniAuto;

    private SluzebniVozidloDto sluzebniVozidlo;

    @NotBlank(message = "Akce je nutné vyplnit")
    @Size(max = 30, message = "Akce nesmí přesahovat 30 znaků")
    private String akce;

    private Date datum;

    private UzivatelDto uzivatel;

    public HistorieSluzebniVozidloDto(HistorieSluzebniVozidlo historieSluzebniVozidlo) {
        if (historieSluzebniVozidlo == null) {
            return;
        }

        this.idHistorieSluzebniAuto = historieSluzebniVozidlo.getIdHistorieSluzebniAuto();
        this.sluzebniVozidlo = new SluzebniVozidloDto(historieSluzebniVozidlo.getSluzebniVozidlo());
        this.akce = historieSluzebniVozidlo.getAkce();
        this.datum = historieSluzebniVozidlo.getDatum();
        this.uzivatel = new UzivatelDto(historieSluzebniVozidlo.getUzivatel());
    }

    public HistorieSluzebniVozidlo toEntity() {
        HistorieSluzebniVozidlo historieSluzebniVozidlo = new HistorieSluzebniVozidlo();

        historieSluzebniVozidlo.setIdHistorieSluzebniAuto(this.idHistorieSluzebniAuto);
        historieSluzebniVozidlo.setSluzebniVozidlo(new SluzebniVozidlo(getSluzebniVozidlo().getIdSluzebniVozidlo()));
        historieSluzebniVozidlo.setAkce(this.akce);
        historieSluzebniVozidlo.setDatum(this.datum);
        historieSluzebniVozidlo.setUzivatel(new Uzivatel(getUzivatel().getId()));

        return historieSluzebniVozidlo;
    }


}
