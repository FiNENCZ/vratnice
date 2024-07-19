package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.sql.Date;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZadostKlicDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String idZadostiKey;

    private KlicDto klic;

    private UzivatelDto uzivatel;

    @NotBlank(message = "{zadost_klic.stav.require}")
    @Size(max = 30, message = "{zadost_klic.stav.max.30}")
    private String stav = "vyžádáno"; // vyžádán/schválen

    private Boolean trvala = true;

    private Date datumOd;

    private Date datumDo;

    private String duvod;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public ZadostKlicDto(ZadostKlic zadostKlic) {
        if (zadostKlic == null) {
            return;
        }

        this.idZadostiKey = zadostKlic.getIdZadostKlic();
        this.klic = new KlicDto(zadostKlic.getKlic());
        this.uzivatel = new UzivatelDto(zadostKlic.getUzivatel());
        this.stav = zadostKlic.getStav();
        this.trvala = zadostKlic.getTrvala();
        this.datumOd = zadostKlic.getDatumOd();
        this.datumDo = zadostKlic.getDatumDo();
        this.duvod = zadostKlic.getDuvod();
        this.aktivita = zadostKlic.getAktivita();
    }

    public ZadostKlic toEntity() {
        ZadostKlic zadostKlic = new ZadostKlic();
        
        zadostKlic.setIdZadostKlic(this.idZadostiKey);
        zadostKlic.setKlic(new Klic(getKlic().getId()));
        zadostKlic.setUzivatel(new Uzivatel(getUzivatel().getId()));
        zadostKlic.setStav(this.stav);
        zadostKlic.setTrvala(this.trvala);
        zadostKlic.setDatumOd(this.datumOd);
        zadostKlic.setDatumDo(this.datumDo);
        zadostKlic.setDuvod(this.duvod);
        zadostKlic.setAktivita(this.aktivita);

        return zadostKlic;
    }

    @AssertTrue(message = "{zadost_klic.datum_od_do.require}")
    public boolean isDatumValid() {
        if (Boolean.FALSE.equals(trvala)) {
            return datumOd != null && datumDo != null;
        }
        return true;
    }

    @AssertTrue(message = "{zadost_klic.duvod.require}")
    public boolean isDuvodValid() {
        if (klic != null && Boolean.TRUE.equals(klic.getSpecialni())) {
            return duvod != null && !duvod.trim().isEmpty();
        }
        return true;
    }
}