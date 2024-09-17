package cz.diamo.vratnice.dto;

import java.io.Serializable;

import cz.diamo.share.dto.LokalitaDto;
import cz.diamo.share.dto.ZavodDto;
import cz.diamo.vratnice.entity.Vratnice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VratniceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank(message = "{vratnice.nazev.require}")
    @Size(max = 50, message = "{vratnice.nazev.max.50}")
    private String nazev;

    @NotNull(message = "{vratnice.zavod.require}")
    private ZavodDto zavod;

    @NotNull(message = "{vratnice.lokalita.require}")
    private LokalitaDto lokalita;

    private Boolean osobni = false;

    private Boolean navstevni = false;

    private Boolean vjezdova = false;

    @NotNull(message = "{vratnice.navstevni_listek_typ.require}")
    private NavstevniListekTypDto vstupniKartyTyp;

    private Boolean odchoziTurniket = false;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public VratniceDto(Vratnice vratnice) {
        if (vratnice == null) {
            return;
        }

        this.id = vratnice.getIdVratnice();
        this.nazev = vratnice.getNazev();
        this.zavod = new ZavodDto(vratnice.getZavod());
        this.lokalita = new LokalitaDto(vratnice.getLokalita());
        this.osobni = vratnice.getOsobni();
        this.navstevni = vratnice.getNavstevni();
        this.vjezdova = vratnice.getVjezdova();

        if (vratnice.getVstupniKartyTyp() != null)
            this.vstupniKartyTyp = new NavstevniListekTypDto(vratnice.getVstupniKartyTyp());

        this.odchoziTurniket = vratnice.getOdchoziTurniket();
        this.aktivita = vratnice.getAktivita();
    }

    public Vratnice toEntity() {
        Vratnice vratnice = new Vratnice();

        vratnice.setIdVratnice(this.id);
        vratnice.setNazev(this.nazev);
        vratnice.setZavod(getZavod().getZavod(null, false));
        vratnice.setLokalita(getLokalita().getLokalita(null, false));
        vratnice.setOsobni(this.osobni);
        vratnice.setNavstevni(this.navstevni);
        vratnice.setVjezdova(this.vjezdova);

        if (getVstupniKartyTyp() != null)
            vratnice.setVstupniKartyTyp(getVstupniKartyTyp().toEntity());

        vratnice.setOdchoziTurniket(this.odchoziTurniket);
        vratnice.setAktivita(this.aktivita);

        return vratnice;
    }

}
