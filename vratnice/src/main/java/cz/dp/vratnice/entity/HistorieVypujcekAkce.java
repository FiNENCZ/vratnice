package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.vratnice.enums.HistorieVypujcekAkceEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "historieVypujcekAkce", schema = Constants.SCHEMA)
@NamedQuery(name = "HistorieVypujcekAkce.findAll", query = "SELECT s FROM HistorieVypujcekAkce s")
public class HistorieVypujcekAkce implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_historie_vypujcek_akce")
    private Integer idHistorieVypujcekAkce;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public HistorieVypujcekAkce(HistorieVypujcekAkceEnum value) {
        setIdHistorieVypujcekAkce(value.getValue());
    }

    public HistorieVypujcekAkceEnum getHistorieVypujcekAkceEnum() {
        return HistorieVypujcekAkceEnum.getHistorieVypujcekAkceEnum(getIdHistorieVypujcekAkce());
    }
}
