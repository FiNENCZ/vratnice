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

    @NotNull(message = "{klic.specialni.povinny}")
    private Boolean special = false;

    @NotBlank(message = "{klic.nazev.povinny}")
    @Size(max = 50, message = "{klic.nazev.max.50}")
    private String name;

    @NotBlank(message = "{klic.rfid.povinny}")
    @Size(max = 50, message = "{klic.rfid.max.50}")
    private String chipCode;

    @NotBlank(message = "{klic.lokace.povinny}")
    @Size(max = 50, message = "{klic.lokace.max.50}")
    private String location;

    @NotBlank(message = "{klic.budova.povinny}")
    @Size(max = 50, message = "{klic.budova.max.50}")
    private String building;

    @NotNull(message = "{klic.podlazi.povinny}")
    private Integer floor;

    @NotBlank(message = "{klic.mistnost.povinny}")
    @Size(max = 50, message = "{klic.mistnost.max.50}")
    private String room;

    @NotBlank(message = "{klic.typ_klice.povinny}")
    @Size(max = 50, message = "{klic.typ_klice.max.50}")
    private String keyType;

    @NotBlank(message = "{klic.stav.povinny}")
    @Size(max = 20, message = "{klic.stav.max.20}")
    private String state = "dostupný";

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public KlicDto(Klic key) {
        if (key == null) {
            return;
        }
        this.idKey = key.getIdKey();
        this.special = key.isSpecial();
        this.name = key.getName();
        this.chipCode = key.getChipCode();
        this.location = key.getLocation();
        this.building = key.getBuilding();
        this.floor = key.getFloor();
        this.room = key.getRoom();
        this.keyType = key.getKeyType();
        this.state = key.getState();
        this.aktivita = key.getAktivita();
    }

    public Klic toEntity() {
        Klic key = new Klic();
        key.setIdKey(this.idKey);
        key.setSpecial(this.special);
        key.setName(this.name);
        key.setChipCode(this.chipCode);
        key.setLocation(this.location);
        key.setBuilding(this.building);
        key.setFloor(this.floor);
        key.setRoom(this.room);
        key.setKeyType(this.keyType);
        key.setState(this.state);
        key.setAktivita(this.aktivita);
        return key;
    }
}
