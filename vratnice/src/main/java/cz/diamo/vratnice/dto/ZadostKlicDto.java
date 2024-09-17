package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.sql.Date;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.vratnice.entity.ZadostKlic;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZadostKlicDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @NotNull(message = "{zadost_klic.klic.require}")
    private KlicDto klic;

    @NotNull(message = "{zadost_klic.uzivatel.require}")
    private UzivatelDto uzivatel;

    @NotNull(message = "{zadost_klic.zadost_stav.require}")
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
        zadostKlic.setKlic(getKlic().toEntity());
        zadostKlic.setUzivatel(getUzivatel().getUzivatel(null, false));
        zadostKlic.setZadostStav(getStav().getZadostStav()); 
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