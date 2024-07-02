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

    private String idKey;

    @NotNull(message = "{klic.specialni.require}")
    private Boolean special = false;

    @NotBlank(message = "{klic.nazev.require}")
    @Size(max = 50, message = "{klic.nazev.max.50}")
    private String name;

    @NotBlank(message = "{klic.rfid.require}")
    @Size(max = 50, message = "{klic.rfid.max.50}")
    private String chipCode;

    @NotBlank(message = "{klic.lokace.require}")
    @Size(max = 50, message = "{klic.lokace.max.50}")
    private String location;

    @NotBlank(message = "{klic.budova.require}")
    @Size(max = 50, message = "{klic.budova.max.50}")
    private String building;

    @NotNull(message = "{klic.podlazi.require}")
    private Integer floor;

    @NotBlank(message = "{klic.mistnost.require}")
    @Size(max = 50, message = "{klic.mistnost.max.50}")
    private String room;

    @NotBlank(message = "{klic.typ_klice.require}")
    @Size(max = 50, message = "{klic.typ_klice.max.50}")
    private String keyType;

    @NotBlank(message = "{klic.stav.require}")
    @Size(max = 20, message = "{klic.stav.max.20}")
    private String state = "dostupný";

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public KlicDto(Klic key) {
        if (key == null) {
            return;
        }
        this.idKey = key.getIdKlic();
        this.special = key.isSpecialni();
        this.name = key.getName();
        this.chipCode = key.getKodCipu();
        this.location = key.getLokalita();
        this.building = key.getBudova();
        this.floor = key.getPoschodi();
        this.room = key.getMistnost();
        this.keyType = key.getTypKlice();
        this.state = key.getStav();
        this.aktivita = key.getAktivita();
    }

    public Klic toEntity() {
        Klic key = new Klic();
        key.setIdKlic(this.idKey);
        key.setSpecialni(this.special);
        key.setName(this.name);
        key.setKodCipu(this.chipCode);
        key.setLokalita(this.location);
        key.setBudova(this.building);
        key.setPoschodi(this.floor);
        key.setMistnost(this.room);
        key.setTypKlice(this.keyType);
        key.setStav(this.state);
        key.setAktivita(this.aktivita);
        return key;
    }
}
