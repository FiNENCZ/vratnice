package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.vratnice.enums.SluzebniVozidloKategorieEnum;
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
@Table(name = "sluzebni_vozidlo_kategorie", schema = Constants.SCHEMA)
@NamedQuery(name = "SluzebniVozidloKategorie.findAll", query = "SELECT s FROM SluzebniVozidloKategorie s")
public class SluzebniVozidloKategorie implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_sluzebni_vozidlo_kategorie")
    private Integer idSluzebniVozidloKategorie;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public SluzebniVozidloKategorie(SluzebniVozidloKategorieEnum value) {
        setIdSluzebniVozidloKategorie(value.getValue());
    }

    public SluzebniVozidloKategorieEnum getSluzebniVozidloKategorieEnum() {
        return SluzebniVozidloKategorieEnum.getSluzebniVozidloKategorieEnum(getIdSluzebniVozidloKategorie());
    }

}
