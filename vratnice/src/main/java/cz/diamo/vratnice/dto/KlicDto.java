package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.dto.BudovaDto;
import cz.diamo.share.dto.LokalitaDto;
import cz.diamo.vratnice.entity.Klic;
import jakarta.validation.constraints.AssertTrue;
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

    @NotNull(message = "{klic.vratnice.require}")
    private VratniceDto vratnice;

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

        setId(key.getIdKlic());
        setSpecialni(key.isSpecialni());
        setNazev(key.getNazev());
        setKodCipu(key.getKodCipu());
        setMistnost(key.getMistnost());
        setVratnice(new VratniceDto(key.getVratnice()));
        setLokalita(new LokalitaDto(key.getLokalita()));

        if (key.getTyp() != null)
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
        key.setVratnice(getVratnice().toEntity());
        key.setMistnost(this.mistnost);

        if (getTyp() != null)
            key.setTyp(getTyp().toEntity());
            
        key.setAktivita(this.aktivita);

        key.setLokalita(getLokalita().getLokalita(null, false));

        if (getBudova() != null)
            key.setBudova(getBudova().getBudova(null, false));

        if (getPoschodi() != null)
            key.setPoschodi(getPoschodi().getPoschodi(null, false));

        return key;
    }

    @AssertTrue(message = "{klic.vratnice.require")
    public boolean isVratniceValid() {
        return vratnice != null && vratnice.getId() != null && !vratnice.getId().isEmpty();
    }

    @AssertTrue(message = "{klic.budoba.require")
    public boolean isBudovaValid() {
        return budova != null && budova.getId() != null && !budova.getId().isEmpty();
    }

    @AssertTrue(message = "{klic.poschodi.require")
    public boolean isPoschodiValid() {
        return poschodi != null && poschodi.getId() != null && !poschodi.getId().isEmpty();
    }

    @AssertTrue(message = "{klic.vratnice.invalid}")
    private boolean isVratniceHaveSameLokalita() {
        if (lokalita == null || vratnice == null) 
            return true; // null není validace anotace, použití @NotNull to vyřeší

        if (!lokalita.getId().equals(vratnice.getLokalita().getId())) 
            return false;

        return true;
    }

    @AssertTrue(message = "{klic.budova.invalid}")
    private boolean isBudovaHaveSameLokalita() {
        if (lokalita == null || budova == null) 
            return true; // null není validace anotace, použití @NotNull to vyřeší
        
        if (!lokalita.getId().equals(budova.getLokalita().getId())) 
            return false;
        
        return true;
    }

    @AssertTrue(message = "{klic.poschodi.invalid}")
    private boolean isPoschodiHaveSameBudova() {
        if (budova == null || poschodi == null) 
            return true; // null není validace anotace, použití @NotNull to vyřeší
        
        if (!budova.getId().equals(poschodi.getBudova().getId())) 
            return false;
        
        return true;
    }

}

