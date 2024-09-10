package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.ZadostStav;
import cz.diamo.vratnice.enums.ZadostStavEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZadostStavDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public ZadostStavDto(ZadostStav zadostStav) {
        if (zadostStav == null)
            return;
        setId(zadostStav.getIdZadostStav());
        setNazev(zadostStav.getNazev());
    }

    @JsonIgnore
    public ZadostStav getZadostStav() {
        ZadostStav zadostStav = new ZadostStav();
        zadostStav.setIdZadostStav(getId());
        return zadostStav;

    }

    public ZadostStavEnum getStavEnum() {
        return ZadostStavEnum.getZadostStavEnum(getId());

    }
}
