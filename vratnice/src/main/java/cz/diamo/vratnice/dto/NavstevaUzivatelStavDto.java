package cz.diamo.vratnice.dto;

import java.io.Serializable;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.vratnice.entity.NavstevaUzivatelStav;
import cz.diamo.vratnice.entity.NavstevniListekStav;
import cz.diamo.vratnice.enums.NavstevniListekStavEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NavstevaUzivatelStavDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private UzivatelDto uzivatel;

    private NavstevniListekStavDto stav = new NavstevniListekStavDto(new NavstevniListekStav(NavstevniListekStavEnum.KE_ZPRACOVANI));

    public NavstevaUzivatelStavDto(NavstevaUzivatelStav navstevaUzivatelStav) {
        if (navstevaUzivatelStav == null) {
            return;
        }

        this.id = navstevaUzivatelStav.getIdNavstevaUzivatelStav();
        this.uzivatel = new UzivatelDto(navstevaUzivatelStav.getUzivatel());

        if (navstevaUzivatelStav.getStav() != null)
            this.stav = new NavstevniListekStavDto(navstevaUzivatelStav.getStav());
    }


    public NavstevaUzivatelStav toEntity() {
        NavstevaUzivatelStav navstevaUzivatelStav = new NavstevaUzivatelStav();

        navstevaUzivatelStav.setIdNavstevaUzivatelStav(this.id);
        navstevaUzivatelStav.setUzivatel(this.getUzivatel().getUzivatel(null, false));

        if (getStav() != null)
            navstevaUzivatelStav.setStav(getStav().toEntity());

        return navstevaUzivatelStav;
    }

}
