package cz.diamo.vratnice.dto;

import java.io.Serializable;

import cz.diamo.vratnice.entity.Klic;
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

    @NotBlank(message = "{klic.lokace.require}")
    @Size(max = 50, message = "{klic.lokace.max.50}")
    private String lokace;

    @NotBlank(message = "{klic.budova.require}")
    @Size(max = 50, message = "{klic.budova.max.50}")
    private String budova;

    @NotNull(message = "{klic.podlazi.require}")
    private Integer poschodi;

    @NotBlank(message = "{klic.mistnost.require}")
    @Size(max = 50, message = "{klic.mistnost.max.50}")
    private String mistnost;

    @NotBlank(message = "{klic.typ_klice.require}")
    @Size(max = 50, message = "{klic.typ_klice.max.50}")
    private String typKlice;

    @NotBlank(message = "{klic.stav.require}")
    @Size(max = 20, message = "{klic.stav.max.20}")
    private String state = "dostupný";

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
        this.lokace = key.getLokalita();
        this.budova = key.getBudova();
        this.poschodi = key.getPoschodi();
        this.mistnost = key.getMistnost();
        this.typKlice = key.getTypKlice();
        this.state = key.getStav();
        this.aktivita = key.getAktivita();
    }

    public Klic toEntity() {
        Klic key = new Klic();
        key.setIdKlic(this.idKlic);
        key.setSpecialni(this.specialni);
        key.setNazev(this.nazev);
        key.setKodCipu(this.kodCipu);
        key.setLokalita(this.lokace);
        key.setBudova(this.budova);
        key.setPoschodi(this.poschodi);
        key.setMistnost(this.mistnost);
        key.setTypKlice(this.typKlice);
        key.setStav(this.state);
        key.setAktivita(this.aktivita);
        return key;
    }
}
