package cz.dp.vratnice.dto;

import java.io.Serializable;

import cz.dp.share.dto.UzivatelDto;
import cz.dp.vratnice.entity.NavstevniListekStav;
import cz.dp.vratnice.entity.NavstevniListekUzivatelStav;
import cz.dp.vratnice.enums.NavstevniListekStavEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NavstevniListekUzivatelStavDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @Valid
    private UzivatelDto uzivatel;

    @NotNull(message = "{navstevni_listek_uzivatel_stav.stav.require}")
    private NavstevniListekStavDto stav = new NavstevniListekStavDto(new NavstevniListekStav(NavstevniListekStavEnum.KE_ZPRACOVANI));

    private String poznamka;

    public NavstevniListekUzivatelStavDto(NavstevniListekUzivatelStav navstevaUzivatelStav) {
        if (navstevaUzivatelStav == null) {
            return;
        }

        this.id = navstevaUzivatelStav.getIdNavstevniListekUzivatelStav();
        this.uzivatel = new UzivatelDto(navstevaUzivatelStav.getUzivatel());

        if (navstevaUzivatelStav.getStav() != null)
            this.stav = new NavstevniListekStavDto(navstevaUzivatelStav.getStav());
        
        this.poznamka = navstevaUzivatelStav.getPoznamka();
    }


    public NavstevniListekUzivatelStav toEntity() {
        NavstevniListekUzivatelStav navstevaUzivatelStav = new NavstevniListekUzivatelStav();

        navstevaUzivatelStav.setIdNavstevniListekUzivatelStav(this.id);
        navstevaUzivatelStav.setUzivatel(this.getUzivatel().getUzivatel(null, true));

        if (getStav() != null)
            navstevaUzivatelStav.setStav(getStav().toEntity());
        
        navstevaUzivatelStav.setPoznamka(this.poznamka);

        return navstevaUzivatelStav;
    }

}
