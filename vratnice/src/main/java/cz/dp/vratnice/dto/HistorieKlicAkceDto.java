package cz.dp.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.vratnice.entity.HistorieKlicAkce;
import cz.dp.vratnice.enums.HistorieKlicAkceEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistorieKlicAkceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public HistorieKlicAkceDto(HistorieKlicAkce historieKlicAkce) {
        if(historieKlicAkce == null) {
            return;
        }

        setId(historieKlicAkce.getIdHistorieKlicAkce());
        setNazev(historieKlicAkce.getNazev());
    }

    @JsonIgnore
    public HistorieKlicAkce toEntity() {
        HistorieKlicAkce historieKlicAkce = new HistorieKlicAkce();
        historieKlicAkce.setIdHistorieKlicAkce(getId());
        return historieKlicAkce;
    }

    public HistorieKlicAkceEnum getHistorieKlicAkceEnum() {
        return HistorieKlicAkceEnum.getHistorieKlicAkceEnum(getId());
    }

}
