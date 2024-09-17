package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.diamo.share.dto.LokalitaDto;
import cz.diamo.share.dto.ZavodDto;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.enums.SluzebniVozidloKategorieEnum;
import cz.diamo.vratnice.enums.SluzebniVozidloStavEnum;
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

    @NotBlank(message = "{sluzebni_vozidlo.rz.require}")
    @Size(max = 30, message = "{sluzebni_vozidlo.rz.max.30}")
    private String rz;

    @NotNull(message = "{sluzebni_vozidlo.typ.require}")
    private VozidloTypDto typ;

    @NotNull(message = "{sluzebni_vozidlo.kategorie.require}")
    private SluzebniVozidloKategorieDto kategorie;

    private SluzebniVozidloFunkceDto funkce; // pouze u kategorie vozidla manažerské – např. ředitel, náměstek

    private ZavodDto zavod; // výběr z číselníku (i vícenásobný), kam může vozidlo jet, manažerské může kamkoliv, ostatní jen závod, jinak se žádá sekretariát

    private List<LokalitaDto> lokality;
    
    @NotNull(message = "{sluzebni_vozidlo.stav.require}")
    private SluzebniVozidloStavDto stav;

    private Date datumOd;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public SluzebniVozidloDto(SluzebniVozidlo sluzebniVozidlo) {
        if (sluzebniVozidlo == null) {
            return;
        }

        this.idSluzebniVozidlo = sluzebniVozidlo.getIdSluzebniVozidlo();
        this.rz = sluzebniVozidlo.getRz();
        this.typ = new VozidloTypDto(sluzebniVozidlo.getTyp());
        this.kategorie = new SluzebniVozidloKategorieDto(sluzebniVozidlo.getKategorie());

        if (sluzebniVozidlo.getFunkce() != null)
            this.funkce = new SluzebniVozidloFunkceDto(sluzebniVozidlo.getFunkce());

        if (sluzebniVozidlo.getZavod() != null)
            this.zavod = new ZavodDto(sluzebniVozidlo.getZavod());

        this.stav = new SluzebniVozidloStavDto(sluzebniVozidlo.getStav());


        List<LokalitaDto> lokalitaDtos = new ArrayList<>();
        if (sluzebniVozidlo.getLokality() != null) {
            for (Lokalita lokalita : sluzebniVozidlo.getLokality()) {
                lokalitaDtos.add(new LokalitaDto(lokalita));
            }
        }
        this.setLokality(lokalitaDtos);


        this.datumOd = sluzebniVozidlo.getDatumOd();
        this.aktivita = sluzebniVozidlo.getAktivita();
    }

    public SluzebniVozidlo toEntity() {
        SluzebniVozidlo sluzebniVozidlo = new SluzebniVozidlo();
        
        sluzebniVozidlo.setIdSluzebniVozidlo(this.idSluzebniVozidlo);
        sluzebniVozidlo.setRz(this.rz);
        sluzebniVozidlo.setTyp(getTyp().toEntity());
        sluzebniVozidlo.setKategorie(getKategorie().toEntity());

        if (getKategorie().getSluzebniVozidloKategorieEnum().equals(SluzebniVozidloKategorieEnum.SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE)) {
            if (getFunkce() != null) 
                sluzebniVozidlo.setFunkce(getFunkce().toEntity());
                
        } else {
            if (getZavod()!= null)
                sluzebniVozidlo.setZavod(getZavod().getZavod(null, false ));

            List<Lokalita> lokality = new ArrayList<>();
            if (getLokality() != null) {
                for (LokalitaDto lokalitaDto : this.getLokality()) {
                    lokality.add(new Lokalita(lokalitaDto.getId()));
                }
            }
            sluzebniVozidlo.setLokality(lokality);
        }
       
        sluzebniVozidlo.setStav(getStav().toEntity());
        sluzebniVozidlo.setDatumOd(this.datumOd);
        sluzebniVozidlo.setAktivita(this.aktivita);
        return sluzebniVozidlo;
    }

    @AssertTrue(message = "{sluzebni_vozidlo.funkce.require}")
    public boolean isFunkceValid() {
        if (kategorie == null) {
            return false; // nebo true podle vašich požadavků
        }
        return !(kategorie.getSluzebniVozidloKategorieEnum().equals(SluzebniVozidloKategorieEnum.SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE) && (funkce == null));
    }

    @AssertTrue(message = "{sluzebni_vozidlo.datum_od.require}")
    public boolean isDatumOdValid() {
        if (stav == null) {
            return false; // nebo true podle vašich požadavků
        }
        return !(stav.getSluzebniVozidloStavEnum().equals(SluzebniVozidloStavEnum.SLUZEBNI_VOZIDLO_STAV_BLOKOVANE) && datumOd == null);
    }

    @AssertTrue(message = "{sluzebni_vozidlo.zavod.require}")
    public boolean isZavodValid() {
        if (kategorie == null) {
            return false; // nebo true podle vašich požadavků
        }
        return !(!kategorie.getSluzebniVozidloKategorieEnum().equals(SluzebniVozidloKategorieEnum.SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE) && (zavod == null));
    }

    @AssertTrue(message = "{sluzebni_vozidlo.lokality.require}")
    public boolean isLokalityValid() {
        if (kategorie == null) {
            return false; // nebo true podle vašich požadavků
        }
        return !(!kategorie.getSluzebniVozidloKategorieEnum().equals(SluzebniVozidloKategorieEnum.SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE) && (lokality == null));
    }
    
}
