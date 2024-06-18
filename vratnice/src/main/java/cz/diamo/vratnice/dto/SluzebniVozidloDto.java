package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Zavod;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import jakarta.persistence.Column;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SluzebniVozidloDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idSluzebniVozidlo;

    @NotBlank(message = "{klic.nazev.povinny}")
    @Size(max = 50, message = "{klic.nazev.max.50}")
    private String typ;

    @NotBlank(message = "{klic.nazev.povinny}")
    @Size(max = 50, message = "{klic.nazev.max.50}")
    private String kategorie;

    @Size(max = 50, message = "{klic.nazev.max.50}")
    private String funkce; // pouze u kategorie vozidla manažerské – např. ředitel, náměstek

    private ZavodDto zavod; // výběr z číselníku (i vícenásobný), kam může vozidlo jet, manažerské může kamkoliv, ostatní jen závod, jinak se žádá sekretariát

    private String lokalita;
    
    @NotBlank(message = "{klic.nazev.povinny}")
    @Size(max = 50, message = "{klic.nazev.max.50}")
    private String stav;

    @Column(name = "datum_od")
    private Date datumOd;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public SluzebniVozidloDto(SluzebniVozidlo sluzebniVozidlo) {
        if (sluzebniVozidlo == null) {
            return;
        }

        this.idSluzebniVozidlo = sluzebniVozidlo.getIdSluzebniVozidlo();
        this.typ = sluzebniVozidlo.getTyp();
        this.kategorie = sluzebniVozidlo.getKategorie();
        this.funkce = sluzebniVozidlo.getFunkce();
        this.zavod = new ZavodDto(sluzebniVozidlo.getZavod());
        this.stav = sluzebniVozidlo.getStav();
        this.lokalita = sluzebniVozidlo.getLokalita();
        this.datumOd = sluzebniVozidlo.getDatumOd();
        this.aktivita = sluzebniVozidlo.getAktivita();
    }

    public SluzebniVozidlo toEntity() {
        SluzebniVozidlo sluzebniVozidlo = new SluzebniVozidlo();
        
        sluzebniVozidlo.setIdSluzebniVozidlo(this.idSluzebniVozidlo);
        sluzebniVozidlo.setTyp(this.typ);
        sluzebniVozidlo.setKategorie(this.kategorie);
        sluzebniVozidlo.setFunkce(this.funkce);
        sluzebniVozidlo.setZavod(new Zavod(getZavod().getId()));
        sluzebniVozidlo.setLokalita(this.lokalita);
        sluzebniVozidlo.setStav(this.stav);
        sluzebniVozidlo.setDatumOd(this.datumOd);
        sluzebniVozidlo.setAktivita(this.aktivita);
        return sluzebniVozidlo;
    }

    @AssertTrue(message = "Funkce is required if kategorie is 'manažerské'")
    public boolean isFunkceValid() {
        return !(kategorie.equals("manažerské") && (funkce == null || funkce.trim().isEmpty()));
    }

    @AssertTrue(message = "DatumOd is required if stav is 'blokované'")
    public boolean isDatumOdValid() {
        return !(stav.equals("blokované") && datumOd == null);
    }

    
}
