package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.SluzebniVozidloStav;
import cz.diamo.vratnice.enums.SluzebniVozidloStavEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SluzebniVozidloStavDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public SluzebniVozidloStavDto(SluzebniVozidloStav sluzebniVozidloStav) {
        if(sluzebniVozidloStav == null) {
            return;
        }

        setId(sluzebniVozidloStav.getIdSluzebniVozidloStav());
        setNazev(sluzebniVozidloStav.getNazev());
    }

    @JsonIgnore
    public SluzebniVozidloStav toEntity() {
        SluzebniVozidloStav sluzebniVozidloStav = new SluzebniVozidloStav();
        sluzebniVozidloStav.setIdSluzebniVozidloStav(getId());
        return sluzebniVozidloStav;
    }

    public SluzebniVozidloStavEnum getSluzebniVozidloStavEnum() {
        return SluzebniVozidloStavEnum.getSluzebniVozidloStavEnum(getId());
    }

}
