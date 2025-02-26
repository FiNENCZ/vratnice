package cz.dp.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.vratnice.entity.VozidloTyp;
import cz.dp.vratnice.enums.VozidloTypEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VozidloTypDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public VozidloTypDto(VozidloTyp vozidloTyp) {
        if(vozidloTyp == null) {
            return;
        }

        setId(vozidloTyp.getIdVozidloTyp());
        setNazev(vozidloTyp.getNazev());
    }

    @JsonIgnore
    public VozidloTyp toEntity() {
        VozidloTyp vozidloTyp = new VozidloTyp();
        vozidloTyp.setIdVozidloTyp(getId());
        return vozidloTyp;
    }

    public VozidloTypEnum getTypEnum() {
        return VozidloTypEnum.getVozidloTypEnum(getId());
    }

}
