package cz.dp.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.vratnice.entity.SluzebniVozidloKategorie;
import cz.dp.vratnice.enums.SluzebniVozidloKategorieEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SluzebniVozidloKategorieDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public SluzebniVozidloKategorieDto(SluzebniVozidloKategorie sluzebniVozidloKategorie) {
        if(sluzebniVozidloKategorie == null) {
            return;
        }

        setId(sluzebniVozidloKategorie.getIdSluzebniVozidloKategorie());
        setNazev(sluzebniVozidloKategorie.getNazev());
    }

    @JsonIgnore
    public SluzebniVozidloKategorie toEntity() {
        SluzebniVozidloKategorie sluzebniVozidloKategorie = new SluzebniVozidloKategorie();
        sluzebniVozidloKategorie.setIdSluzebniVozidloKategorie(getId());
        return sluzebniVozidloKategorie;
    }

    public SluzebniVozidloKategorieEnum getSluzebniVozidloKategorieEnum() {
        return SluzebniVozidloKategorieEnum.getSluzebniVozidloKategorieEnum(getId());
    }

}
