package cz.dp.share.dto;

import java.io.Serializable;

import cz.dp.share.entity.ZastupSimple;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZastupSimpleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idUzivatel;

    private String idZavod;

    private String nazev;

    private String sapId;

    public ZastupSimpleDto(ZastupSimple zastupSimple) {
        setIdUzivatel(zastupSimple.getIdUzivatel());
        setNazev(zastupSimple.getNazev());
        setSapId(zastupSimple.getSapid());
        setIdZavod(zastupSimple.getIdZavod());
    }
}
