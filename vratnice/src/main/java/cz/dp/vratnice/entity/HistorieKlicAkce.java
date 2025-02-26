package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.vratnice.enums.HistorieKlicAkceEnum;
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
@Table(name = "historie_klic_akce", schema = Constants.SCHEMA)
@NamedQuery(name = "HistorieKlicAkce.findAll", query = "SELECT s FROM HistorieKlicAkce s")
public class HistorieKlicAkce implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_historie_klic_akce")
    private Integer idHistorieKlicAkce;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public HistorieKlicAkce(HistorieKlicAkceEnum value) {
        setIdHistorieKlicAkce(value.getValue());
    }

    public HistorieKlicAkceEnum getHistorieKlicAkceEnum() {
        return HistorieKlicAkceEnum.getHistorieKlicAkceEnum(getIdHistorieKlicAkce());
    }

}
