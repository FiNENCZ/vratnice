package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.KlicTyp;
import cz.diamo.vratnice.enums.KlicTypEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KlicTypDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public KlicTypDto(KlicTyp klicTyp) {
        if(klicTyp == null) {
            return;
        }

        setId(klicTyp.getIdKlicTyp());
        setNazev(klicTyp.getNazev());
    }

    @JsonIgnore
    public KlicTyp toEntity() {
        KlicTyp klicTyp = new KlicTyp();
        klicTyp.setIdKlicTyp(getId());
        return klicTyp;
    }

    public KlicTypEnum getTypEnum() {
        return KlicTypEnum.getKlicStateEnum(getId());
    }

}
