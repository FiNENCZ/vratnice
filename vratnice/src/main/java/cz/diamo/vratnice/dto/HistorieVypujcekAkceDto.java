package cz.diamo.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.vratnice.entity.HistorieVypujcekAkce;
import cz.diamo.vratnice.enums.HistorieVypujcekAkceEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistorieVypujcekAkceDto implements Serializable{

        private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public HistorieVypujcekAkceDto(HistorieVypujcekAkce historieVypujcekAkce) {
        if(historieVypujcekAkce == null) {
            return;
        }

        setId(historieVypujcekAkce.getIdHistorieVypujcekAkce());
        setNazev(historieVypujcekAkce.getNazev());
    }

    @JsonIgnore
    public HistorieVypujcekAkce toEntity() {
        HistorieVypujcekAkce historieVypujcekAkce = new HistorieVypujcekAkce();
        historieVypujcekAkce.setIdHistorieVypujcekAkce(getId());
        return historieVypujcekAkce;
    }

    public HistorieVypujcekAkceEnum getHistorieVypujcekAkceEnum() {
        return HistorieVypujcekAkceEnum.getHistorieVypujcekAkceEnum(getId());
    }
}
