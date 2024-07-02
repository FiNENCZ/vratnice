package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.NavstevniListekTyp;
import cz.diamo.vratnice.enums.NavstevniListekTypEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NavstevniListekTypDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public NavstevniListekTypDto(NavstevniListekTyp navstevniListekTyp) {
        if(navstevniListekTyp == null) {
            return;
        }

        setId(navstevniListekTyp.getIdNavstevniListekTyp());
        setNazev(navstevniListekTyp.getNazev());
    }

    @JsonIgnore
    public NavstevniListekTyp toEntity() {
        NavstevniListekTyp navstevniListekTyp = new NavstevniListekTyp();
        navstevniListekTyp.setIdNavstevniListekTyp(getId());
        return navstevniListekTyp;
    }

    public NavstevniListekTypEnum getTypEnum() {
        return NavstevniListekTypEnum.getNavstevniListekTypEnum(getId());
    }

}
