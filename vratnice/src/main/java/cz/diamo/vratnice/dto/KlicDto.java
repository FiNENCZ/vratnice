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

    private String id;

    @NotNull(message = "{klic.specialni.require}")
    private Boolean specialni = false;

    @NotBlank(message = "{klic.nazev.require}")
    @Size(max = 50, message = "{klic.nazev.max.50}")
    private String nazev;

    @NotBlank(message = "{klic.rfid.require}")
    @Size(max = 50, message = "{klic.rfid.max.50}")
    private String kodCipu;

    //@NotBlank(message = "{klic.lokace.require}")
    //@Size(max = 50, message = "{klic.lokace.max.50}")
    private LokalitaDto lokalita;

    //@NotBlank(message = "{klic.budova.require}")
    //@Size(max = 50, message = "{klic.budova.max.50}")
    private BudovaDto budova;

    //@NotNull(message = "{klic.podlazi.require}")
    private PoschodiDto poschodi;

    @NotBlank(message = "{klic.mistnost.require}")
    @Size(max = 50, message = "{klic.mistnost.max.50}")
    private String mistnost;

    //@NotBlank(message = "{klic.typ_klice.require}")
    //@Size(max = 50, message = "{klic.typ_klice.max.50}")
    private KlicTypDto typ;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public KlicDto(Klic key) {
        if (key == null) {
            return;
        }

        setId(key.getIdKlic());
        setSpecialni(key.isSpecialni());
        setNazev(key.getNazev());
        setKodCipu(key.getKodCipu());
        setMistnost(key.getMistnost());
        setLokalita(new LokalitaDto(key.getLokalita()));
        setTyp(new KlicTypDto(key.getTyp()));
        setAktivita(key.getAktivita());

        if (key.getBudova() != null)
            setBudova(new BudovaDto(key.getBudova()));

        if (key.getPoschodi() != null)    
            setPoschodi(new PoschodiDto(key.getPoschodi()));
    }

    @JsonIgnore
    public Klic toEntity() {
        Klic key = new Klic();
        key.setIdKlic(this.id);
        key.setSpecialni(this.specialni);
        key.setNazev(this.nazev);
        key.setKodCipu(this.kodCipu);
        key.setMistnost(this.mistnost);
        key.setTyp(getTyp().toEntity());
        key.setAktivita(this.aktivita);

        key.setLokalita(new Lokalita(getLokalita().getId()));

        if (getBudova() != null)
            key.setBudova(new Budova(getBudova().getId()));

        if (getPoschodi() != null)
            key.setPoschodi(new Poschodi(getPoschodi().getId()));

        return key;
    }
}
