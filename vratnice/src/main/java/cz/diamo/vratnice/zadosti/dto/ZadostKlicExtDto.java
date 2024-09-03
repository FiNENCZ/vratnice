package cz.diamo.vratnice.zadosti.dto;

import java.io.Serializable;
import java.sql.Date;

import cz.diamo.share.dto.ZadostExterniDto;
import cz.diamo.vratnice.entity.ZadostKlic;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZadostKlicExtDto extends ZadostExterniDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String klicId;

    private Date datumOd;

    private Date datumDo;

    private String duvod;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public ZadostKlicExtDto(ZadostKlic zadostKlic) {
        if (zadostKlic == null) {
            return;
        }

        setId(zadostKlic.getIdZadostKlic());
        setKlicId(zadostKlic.getKlic().getIdKlic());
        setDatumOd(zadostKlic.getDatumOd());
        setDatumDo(zadostKlic.getDatumDo());
        setDuvod(zadostKlic.getDuvod());
        setAktivita(zadostKlic.getAktivita());

        setSapIdZamestnance(zadostKlic.getUzivatel().getSapId());
    }
}