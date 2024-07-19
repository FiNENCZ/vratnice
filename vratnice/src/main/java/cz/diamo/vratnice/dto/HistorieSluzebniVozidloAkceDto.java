package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.HistorieSluzebniVozidloAkce;
import cz.diamo.vratnice.enums.HistorieSluzebniVozidloAkceEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistorieSluzebniVozidloAkceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public HistorieSluzebniVozidloAkceDto(HistorieSluzebniVozidloAkce historieSluzebniVozidloAkce) {
        if(historieSluzebniVozidloAkce == null) {
            return;
        }

        setId(historieSluzebniVozidloAkce.getIdHistorieSluzebniVozidloAkce());
        setNazev(historieSluzebniVozidloAkce.getNazev());
    }

    @JsonIgnore
    public HistorieSluzebniVozidloAkce toEntity() {
        HistorieSluzebniVozidloAkce historieSluzebniVozidloAkce = new HistorieSluzebniVozidloAkce();
        historieSluzebniVozidloAkce.setIdHistorieSluzebniVozidloAkce(getId());
        return historieSluzebniVozidloAkce;
    }

    public HistorieSluzebniVozidloAkceEnum getHistorieSluzebniVozidloAkceEnum() {
        return HistorieSluzebniVozidloAkceEnum.getHistorieSluzebniVozidloAkceEnum(getId());
    }
}
