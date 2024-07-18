package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.Budova;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.entity.Poschodi;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KlicDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idKlic;

    @NotNull(message = "{klic.specialni.require}")
    private Boolean specialni = false;

    @NotBlank(message = "{klic.nazev.require}")
    @Size(max = 50, message = "{klic.nazev.max.50}")
    private String nazev;

    @NotBlank(message = "{klic.rfid.require}")
    @Size(max = 50, message = "{klic.rfid.max.50}")
    private String kodCipu;

    @NotNull(message = "{klic.lokace.require}")
    private LokalitaDto lokalita;

    @NotNull(message = "{klic.budova.require}")
    private BudovaDto budova;

    @NotNull(message = "{klic.podlazi.require}")
    private PoschodiDto poschodi;

    @NotBlank(message = "{klic.mistnost.require}")
    @Size(max = 50, message = "{klic.mistnost.max.50}")
    private String mistnost;

    @NotNull(message = "{klic.typ_klice.require}")
    private KlicTypDto typ;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public KlicDto(Klic key) {
        if (key == null) {
            return;
        }
        this.idKlic = key.getIdKlic();
        this.specialni = key.isSpecialni();
        this.nazev = key.getNazev();
        this.kodCipu = key.getKodCipu();
        this.lokalita = new LokalitaDto(key.getLokalita());
        this.budova = new BudovaDto(key.getBudova());
        this.poschodi = new PoschodiDto(key.getPoschodi());
        this.mistnost = key.getMistnost();

        if (key.getTyp() != null)
            this.typ = new KlicTypDto(key.getTyp());

        this.aktivita = key.getAktivita();
    }

    @JsonIgnore
    public Klic toEntity() {
        Klic key = new Klic();
        key.setIdKlic(this.idKlic);
        key.setSpecialni(this.specialni);
        key.setNazev(this.nazev);
        key.setKodCipu(this.kodCipu);
        key.setLokalita(new Lokalita(getLokalita().getId()));
        key.setBudova(new Budova(getBudova().getId()));
        key.setPoschodi(new Poschodi(getPoschodi().getId()));
        key.setMistnost(this.mistnost);

        if (getTyp() != null)
            key.setTyp(getTyp().toEntity());
            
        key.setAktivita(this.aktivita);
        return key;
    }
}
