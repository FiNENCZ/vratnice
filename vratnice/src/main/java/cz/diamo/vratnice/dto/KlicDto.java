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

    @NotNull(message = "Special flag is required")
    private Boolean special;

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "Chip code is required")
    @Size(max = 50, message = "Chip code cannot exceed 50 characters")
    private String chipCode;

    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location cannot exceed 50 characters")
    private String location;

    @NotBlank(message = "Building is required")
    @Size(max = 50, message = "Building cannot exceed 50 characters")
    private String building;

    @NotNull(message = "Floor is required")
    private Integer floor;

    @NotBlank(message = "Room is required")
    @Size(max = 50, message = "Room cannot exceed 50 characters")
    private String room;

    @NotBlank(message = "Key type is required")
    @Size(max = 50, message = "Key type cannot exceed 50 characters")
    private String keyType;

    @NotBlank(message = "State is required")
    @Size(max = 20, message = "State cannot exceed 20 characters")
    private String state = "aktivní";

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
        return key;
    }
}
