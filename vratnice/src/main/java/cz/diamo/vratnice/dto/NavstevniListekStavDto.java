package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.NavstevniListekStav;
import cz.diamo.vratnice.enums.NavstevniListekStavEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NavstevniListekStavDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public NavstevniListekStavDto(NavstevniListekStav navstevniListekStav) {
        if(navstevniListekStav == null) {
            return;
        }

        setId(navstevniListekStav.getIdNavstevniListekStav());
        setNazev(navstevniListekStav.getNazev());
    }

    @JsonIgnore
    public NavstevniListekStav toEntity() {
        NavstevniListekStav navstevniListekStav = new NavstevniListekStav();
        navstevniListekStav.setIdNavstevniListekStav(getId());
        return navstevniListekStav;
    }

    public NavstevniListekStavEnum getStavEnum() {
        return NavstevniListekStavEnum.getNavstevniListekStavEnum(getId());
    }

}
