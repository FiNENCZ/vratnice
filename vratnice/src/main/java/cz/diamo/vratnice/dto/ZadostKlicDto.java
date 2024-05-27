package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.sql.Date;

import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.enums.StavZadostKlic;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZadostKlicDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String idZadostiKey;

    private Klic klic;

    private Uzivatel uzivatel;

    @NotBlank(message = "Building is required")
    @Size(max = 30, message = "Building cannot exceed 30 characters")
    private StavZadostKlic stav; // vyžádán/schválen

    private Boolean trvala = true;

    private Date datumOd;

    private Date datumDo;

    public ZadostKlicDto(ZadostKlic zadostKlic) {
        if (zadostKlic == null) {
            return;
        }

        this.idZadostiKey = zadostKlic.getIdZadostKlic();
        this.klic = zadostKlic.getKlic();
        this.uzivatel = zadostKlic.getUzivatel();
        this.stav = zadostKlic.getStav();
        this.trvala = zadostKlic.getTrvala();
        this.datumOd = zadostKlic.getDatumOd();
        this.datumDo = zadostKlic.getDatumDo();
    }

    public ZadostKlic toEntity() {
        ZadostKlic zadostKlic = new ZadostKlic();
        zadostKlic.setIdZadostKlic(this.idZadostiKey);
        zadostKlic.setKlic(this.klic);
        zadostKlic.setUzivatel(this.uzivatel);
        zadostKlic.setStav(this.stav);
        zadostKlic.setTrvala(this.trvala);
        zadostKlic.setDatumOd(this.datumOd);
        zadostKlic.setDatumDo(this.datumDo);
        return zadostKlic;
    }

    @AssertTrue(message = "datumOd and datumDo must be provided if trvala is false")
    public boolean isDatumValid() {
        if (Boolean.FALSE.equals(trvala)) {
            return datumOd != null && datumDo != null;
        }
        return true;
    }


}
