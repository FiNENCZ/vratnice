package cz.diamo.vratnice.dto;

import java.io.Serializable;

import cz.diamo.vratnice.entity.Spolecnost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpolecnostDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank(message = "{spolecnost.nazev.require}")
    @Size(max = 80, message = "{spolecnost.nazev.max.120}")
    private String nazev; 

    public SpolecnostDto(Spolecnost spolecnost) {
        if (spolecnost == null) {
            return;
        }

        this.id = spolecnost.getIdSpolecnost();
        this.nazev = spolecnost.getNazev();
    }

    public Spolecnost toEntity() {
        Spolecnost spolecnost = new Spolecnost();
        
        spolecnost.setIdSpolecnost(this.id);
        spolecnost.setNazev(this.nazev);

        return spolecnost;
    }

}
