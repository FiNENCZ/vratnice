package cz.diamo.vratnice.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import cz.diamo.vratnice.enums.HistorieSluzebniVozidloAkceEnum;
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
@Table(name = "historie_sluzebni_vozidlo_akce", schema = Constants.SCHEMA)
@NamedQuery(name = "HistorieSluzebniVozidloAkce.findAll", query = "SELECT s FROM HistorieSluzebniVozidloAkce s")
public class HistorieSluzebniVozidloAkce implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_historie_sluzebni_vozidlo_akce")
    private Integer idHistorieSluzebniVozidloAkce;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public HistorieSluzebniVozidloAkce(HistorieSluzebniVozidloAkceEnum value) {
        setIdHistorieSluzebniVozidloAkce(value.getValue());
    }

    public HistorieSluzebniVozidloAkceEnum getHistorieSluzebniVozidloAkceEnum() {
        return HistorieSluzebniVozidloAkceEnum.getHistorieSluzebniVozidloAkceEnum(getIdHistorieSluzebniVozidloAkce());
    }
}

