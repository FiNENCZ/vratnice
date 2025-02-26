package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.dp.vratnice.entity.NavstevaOsoba;
import cz.dp.vratnice.entity.NavstevniListek;
import cz.dp.vratnice.entity.NavstevniListekUzivatelStav;

import java.sql.Timestamp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    @Valid
    private List<NavstevniListekUzivatelStavDto> uzivateleStav;

    private Timestamp casVytvoreni;

    private NavstevniListekTypDto typ;

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
        
        List<NavstevniListekUzivatelStavDto> uzivatelDtos = new ArrayList<>();
        if(navstevniListek.getUzivateleStav() != null) {
            for(NavstevniListekUzivatelStav uzivatel: navstevniListek.getUzivateleStav()) {
                uzivatelDtos.add(new NavstevniListekUzivatelStavDto(uzivatel));
            }
        }
        this.setUzivateleStav(uzivatelDtos);

        if (navstevniListek.getTyp() != null)
            this.typ = new NavstevniListekTypDto(navstevniListek.getTyp());

        this.casVytvoreni = navstevniListek.getCasVytvoreni();

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

        List<NavstevniListekUzivatelStav> uzivatels = new ArrayList<>();
        if (getUzivateleStav() != null) {
            for (NavstevniListekUzivatelStavDto uzivatelDto : this.getUzivateleStav()) {
                uzivatels.add(uzivatelDto.toEntity());
            }
        }
        navstevniListek.setUzivateleStav(uzivatels);

        if (getTyp() != null)
            navstevniListek.setTyp(getTyp().toEntity());

        navstevniListek.setAktivita(this.aktivita);

        return navstevniListek;
    }

}
