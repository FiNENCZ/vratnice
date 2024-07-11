package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.SluzebniVozidloFunkce;
import cz.diamo.vratnice.enums.SluzebniVozidloFunkceEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SluzebniVozidloFunkceDto implements Serializable  {

     private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public SluzebniVozidloFunkceDto(SluzebniVozidloFunkce sluzebniVozidloFunkce) {
        if(sluzebniVozidloFunkce == null) {
            return;
        }

        setId(sluzebniVozidloFunkce.getIdSluzebniVozidloFunkce());
        setNazev(sluzebniVozidloFunkce.getNazev());
    }

    @JsonIgnore
    public SluzebniVozidloFunkce toEntity() {
        SluzebniVozidloFunkce sluzebniVozidloFunkce = new SluzebniVozidloFunkce();
        sluzebniVozidloFunkce.setIdSluzebniVozidloFunkce(getId());
        return sluzebniVozidloFunkce;
    }

    public SluzebniVozidloFunkceEnum getTypEnum() {
        return SluzebniVozidloFunkceEnum.getSluzebniVozidloFunkceEnum(getId());
    }
}
