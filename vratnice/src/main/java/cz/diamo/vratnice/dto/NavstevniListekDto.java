package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.diamo.share.dto.UzivatelDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    //@NotBlank(message = "{navstevni_listek.typ.require}")
    //@Size(message = "{navstevni_listek.typ.max.30}")
    private NavstevniListekTypDto typ;

    @NotBlank(message = "{navstevni_listek.stav.require}")
    @Size(message = "{navstevni_listek.stav.max.30}")
    private String stav = "vyžádáno";

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
        this.setNavstevaOsoba(navstevaOsobaDtos);

        List<UzivatelDto> uzivatelDtos = new ArrayList<>();
        if(navstevniListek.getNavstevaOsoba() != null) {
            for(Uzivatel uzivatel: navstevniListek.getUzivatel()) {
                uzivatelDtos.add(new UzivatelDto(uzivatel));
            }
        }
        this.setUzivatel(uzivatelDtos);

        this.typ = new NavstevniListekTypDto(navstevniListek.getTyp());
        this.stav = navstevniListek.getStav();
    }

    public NavstevniListek toEntity() {
        NavstevniListek navstevniListek = new NavstevniListek();

        navstevniListek.setIdNavstevniListek(this.idNavstevniListek);
    
        List<NavstevaOsoba> navstevaOsobas = new ArrayList<>();
        if (this.getNavstevaOsoba() != null) {
            for (NavstevaOsobaDto navstevaOsobaDto : this.getNavstevaOsoba()) {
                navstevaOsobas.add(new NavstevaOsoba(navstevaOsobaDto.getIdNavstevaOsoba()));
            }
        }
        navstevniListek.setNavstevaOsoba(navstevaOsobas);

        List<Uzivatel> uzivatels = new ArrayList<>();
        if (getUzivatel() != null) {
            for (UzivatelDto uzivatelDto : this.getUzivatel()) {
                uzivatels.add(new Uzivatel(uzivatelDto.getId()));
            }
        }
        navstevniListek.setUzivatel(uzivatels);

        navstevniListek.setTyp(getTyp().toEntity());
        navstevniListek.setStav(this.stav);

        return navstevniListek;
    }

}
