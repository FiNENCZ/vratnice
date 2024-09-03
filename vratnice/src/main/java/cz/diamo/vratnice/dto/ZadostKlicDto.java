package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.sql.Date;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.entity.ZadostStav;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZadostKlicDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private KlicDto klic;

    private UzivatelDto uzivatel;

    private ZadostStavDto stav;

    private Boolean trvala = true;

    private Date datumOd;

    private Date datumDo;

    private String duvod;

    private Boolean jeKlicDostupny;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public ZadostKlicDto(ZadostKlic zadostKlic) {
        if (zadostKlic == null) {
            return;
        }

        setId(zadostKlic.getIdZadostKlic());
        setKlic(new KlicDto(zadostKlic.getKlic()));
        setUzivatel(new UzivatelDto(zadostKlic.getUzivatel()));
        setStav(new ZadostStavDto(zadostKlic.getZadostStav()));
        setTrvala(zadostKlic.getTrvala());
        setDatumOd(zadostKlic.getDatumOd());
        setDatumDo(zadostKlic.getDatumDo());
        setDuvod(zadostKlic.getDuvod());
        setAktivita(zadostKlic.getAktivita());
    }

    public ZadostKlic toEntity() {
        ZadostKlic zadostKlic = new ZadostKlic();
        
        zadostKlic.setIdZadostKlic(getId());
        zadostKlic.setKlic(new Klic(getKlic().getId()));
        zadostKlic.setUzivatel(new Uzivatel(getUzivatel().getId()));
        zadostKlic.setZadostStav(new ZadostStav(getStav().getStavEnum()));
        zadostKlic.setTrvala(getTrvala());
        zadostKlic.setDatumOd(getDatumOd());
        zadostKlic.setDatumDo(getDatumDo());
        zadostKlic.setDuvod(getDuvod());
        zadostKlic.setAktivita(getAktivita());

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