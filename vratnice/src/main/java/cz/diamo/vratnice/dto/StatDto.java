package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.enums.StatEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public StatDto(Stat stat) {
        if(stat == null) {
            return;
        }

        setId(stat.getIdStat());
        setNazev(stat.getNazev());
    }

    @JsonIgnore
    public Stat toEntity() {
        Stat klicTyp = new Stat();
        klicTyp.setIdStat(getId());
        return klicTyp;
    }

    public StatEnum getStatEnum() {
        return StatEnum.getStatEnum(getId());
    }
}
