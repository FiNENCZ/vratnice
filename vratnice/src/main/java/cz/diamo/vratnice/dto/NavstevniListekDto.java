package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevaUzivatelStav;
import cz.diamo.vratnice.entity.NavstevniListek;
import jakarta.validation.Valid;
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

    private VratniceDto vratnice;

    @NotNull(message = "{navstevni_listek.navsteva_osoba.require}")
    @Valid
    private List<NavstevaOsobaDto> navstevaOsoba;

    @NotNull(message = "{navstevni_listek.uzivatel.require}")
    private List<NavstevaUzivatelStavDto> uzivateleStav;

    //@NotNull(message = "{navstevni_listek.typ.require}")
    private NavstevniListekTypDto typ;

    @NotBlank(message = "{navstevni_listek.stav.require}")
    @Size(max = 30, message = "{navstevni_listek.stav.max.30}")
    private String stav = "vyžádáno";

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public NavstevniListekDto(NavstevniListek navstevniListek) {
        if (navstevniListek == null) {
            return;
        }

        this.idNavstevniListek = navstevniListek.getIdNavstevniListek();

        if(navstevniListek.getVratnice() != null)
            this.vratnice = new VratniceDto(navstevniListek.getVratnice());

        List<NavstevaOsobaDto> navstevaOsobaDtos = new ArrayList<>();
        if(navstevniListek.getNavstevaOsoba() != null) {
            for(NavstevaOsoba navstevaOsoba: navstevniListek.getNavstevaOsoba()) {
                navstevaOsobaDtos.add(new NavstevaOsobaDto(navstevaOsoba));
            }
        }
        this.setNavstevaOsoba(navstevaOsobaDtos);
        
        List<NavstevaUzivatelStavDto> uzivatelDtos = new ArrayList<>();
        if(navstevniListek.getUzivateleStav() != null) {
            for(NavstevaUzivatelStav uzivatel: navstevniListek.getUzivateleStav()) {
                uzivatelDtos.add(new NavstevaUzivatelStavDto(uzivatel));
            }
        }
        this.setUzivateleStav(uzivatelDtos);

        this.typ = new NavstevniListekTypDto(navstevniListek.getTyp());
        this.stav = navstevniListek.getStav();
        this.aktivita = navstevniListek.getAktivita();
    }

    public NavstevniListek toEntity() {
        NavstevniListek navstevniListek = new NavstevniListek();

        navstevniListek.setIdNavstevniListek(this.idNavstevniListek);

        if(getVratnice() != null)
            navstevniListek.setVratnice(this.getVratnice().toEntity());
    
        List<NavstevaOsoba> navstevaOsobas = new ArrayList<>();
        if (this.getNavstevaOsoba() != null) {
            for (NavstevaOsobaDto navstevaOsobaDto : this.getNavstevaOsoba()) {
                navstevaOsobas.add(navstevaOsobaDto.toEntity());
            }
        }
        navstevniListek.setNavstevaOsoba(navstevaOsobas);

        List<NavstevaUzivatelStav> uzivatels = new ArrayList<>();
        if (getUzivateleStav() != null) {
            for (NavstevaUzivatelStavDto uzivatelDto : this.getUzivateleStav()) {
                uzivatels.add(uzivatelDto.toEntity());
            }
        }
        navstevniListek.setUzivateleStav(uzivatels);

        navstevniListek.setTyp(getTyp().toEntity());
        navstevniListek.setStav(this.stav);
        navstevniListek.setAktivita(this.aktivita);

        return navstevniListek;
    }

}
