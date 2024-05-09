package cz.diamo.share.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.entity.Zastup;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZastupDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String guid;

    @NotNull(message = "{uzivatel.require}")
    private UzivatelDto uzivatel;

    @NotNull(message = "{zastupce.require}")
    private UzivatelDto uzivatelZastupce;

    @NotNull(message = "{platnost.od.require}")
    private Date platnostOd;

    @NotNull(message = "{platnost.do.require}")
    private Date platnostDo;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita;

    private Boolean distribuovano;

    private String chybaDistribuce;

    public ZastupDto(Zastup zastup) {
        if (zastup == null)
            return;

        setGuid(zastup.getGuid());
        setUzivatel(new UzivatelDto(zastup.getUzivatel()));
        setUzivatelZastupce(new UzivatelDto(zastup.getUzivatelZastupce()));
        setPlatnostOd(zastup.getPlatnostOd());
        setPlatnostDo(zastup.getPlatnostDo());
        setAktivita(zastup.getAktivita());
        setDistribuovano(zastup.getDistribuovano());
        setChybaDistribuce(zastup.getChybaDistribuce());
    }

    @JsonIgnore
    public Zastup getZastup(Zastup zastup) {
        if (zastup == null)
            zastup = new Zastup();
        zastup.setUzivatel(getUzivatel().getUzivatel(null, true));
        zastup.setUzivatelZastupce(getUzivatelZastupce().getUzivatel(null, true));
        zastup.setPlatnostOd(getPlatnostOd());
        zastup.setPlatnostDo(getPlatnostDo());
        zastup.setAktivita(getAktivita());
        return zastup;
    }
}
