package cz.dp.share.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.base.Utils;
import cz.dp.share.entity.Zakazka;
import cz.dp.share.entity.Zavod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Zakázka
 */
@Data
@NoArgsConstructor
public class ZakazkaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @Size(max = 100, message = "{sap.id.max.100}")
    @NotBlank(message = "{sap.id.require}")
    private String sapId;

    @Size(max = 1000, message = "{nazev.max.1000}")
    @NotBlank(message = "{nazev.require}")
    private String nazev;

    private Date platnostOd;

    private Date platnostDo;

    private Boolean virtualni = false;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita;

    public ZakazkaDto(Zakazka zakazka) {
        if (zakazka == null)
            return;
        setId(zakazka.getIdZakazka());
        setSapId(zakazka.getSapId());
        setNazev(zakazka.getNazev());
        if (!Utils.stejnyDen(zakazka.getPlatnostOd(), Utils.getMinDate()))
            setPlatnostOd(zakazka.getPlatnostOd());
        else
            setPlatnostOd(null);
        if (!Utils.stejnyDen(zakazka.getPlatnostDo(), Utils.getMaxDate(false)))
            setPlatnostDo(zakazka.getPlatnostDo());
        else
            setPlatnostDo(null);
        setAktivita(zakazka.getAktivita());
    }

    @JsonIgnore
    public Zakazka getZakazka(Zakazka zakazka, AppUserDto appUserDto, boolean pouzeId) {
        if (zakazka == null)
            zakazka = new Zakazka();

        zakazka.setIdZakazka(getId());
        if (!pouzeId) {
            zakazka.setZavod(new Zavod(appUserDto.getZavod().getId()));
            zakazka.setNazev(getNazev());
            zakazka.setSapId(getSapId());

            if (getPlatnostOd() == null)
                zakazka.setPlatnostOd(Utils.getMinDate());
            else
                zakazka.setPlatnostOd(getPlatnostOd());

            if (getPlatnostDo() == null)
                zakazka.setPlatnostDo(Utils.getMaxDate(false));
            else
                zakazka.setPlatnostDo(getPlatnostDo());

            zakazka.setAktivita(getAktivita());
        }

        return zakazka;
    }
}
