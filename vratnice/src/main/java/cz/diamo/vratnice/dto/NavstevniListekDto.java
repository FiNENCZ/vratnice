package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NavstevniListekDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String idNavstevniListek;

    @NotNull(message = "{navstevni_listek.navsteva_osoba.require}")
    private List<NavstevaOsobaDto> navstevaOsoba;

    @NotNull(message = "{navstevni_listek.uzivatel.require}")
    private List<UzivatelDto> uzivatel;

    private String stav;

    public NavstevniListekDto(NavstevniListek navstevniListek) {
        if (navstevniListek == null) {
            return;
        }

        this.idNavstevniListek = navstevniListek.getIdNavstevniListek();

        List<NavstevaOsobaDto> navstevaOsobaDtos = new ArrayList<>();
        if(navstevniListek.getNavstevaOsoba() != null) {
            for(NavstevaOsoba navstevaOsoba: navstevniListek.getNavstevaOsoba()) {
                navstevaOsobaDtos.add(new NavstevaOsobaDto(navstevaOsoba));
            }
        }

        List<UzivatelDto> uzivatelDtos = new ArrayList<>();
        if(navstevniListek.getNavstevaOsoba() != null) {
            for(Uzivatel uzivatel: navstevniListek.getUzivatel()) {
                uzivatelDtos.add(new UzivatelDto(uzivatel));
            }
        }

        this.stav = navstevniListek.getStav();
    }

    public NavstevniListek toEntity() {
        NavstevniListek navstevniListek = new NavstevniListek();

        navstevniListek.setIdNavstevniListek(this.idNavstevniListek);
    
        List<NavstevaOsoba> navstevaOsobas = new ArrayList<>();
        if (getNavstevaOsoba() != null) {
            for (NavstevaOsobaDto navstevaOsobaDto : this.getNavstevaOsoba()) {
                navstevaOsobas.add(new NavstevaOsoba(navstevaOsobaDto.getIdNavstevaOsoba()));
            }
        }

        List<Uzivatel> uzivatels = new ArrayList<>();
        if (getUzivatel() != null) {
            for (UzivatelDto uzivatelDto : this.getUzivatel()) {
                uzivatels.add(new Uzivatel(uzivatelDto.getId()));
            }
        }

        navstevniListek.setStav(this.stav);

        return navstevniListek;
    }

}
