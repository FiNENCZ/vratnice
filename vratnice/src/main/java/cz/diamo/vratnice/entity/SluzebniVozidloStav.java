package cz.diamo.vratnice.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import cz.diamo.vratnice.enums.SluzebniVozidloStavEnum;
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
@Table(name = "sluzebni_vozidlo_stav", schema = Constants.SCHEMA)
@NamedQuery(name = "SluzebniVozidloStav.findAll", query = "SELECT s FROM SluzebniVozidloStav s")
public class SluzebniVozidloStav implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_sluzebni_vozidlo_stav")
    private Integer idSluzebniVozidloStav;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public SluzebniVozidloStav(SluzebniVozidloStavEnum value) {
        setIdSluzebniVozidloStav(value.getValue());
    }

    public SluzebniVozidloStavEnum getSluzebniVozidloStavEnum() {
        return SluzebniVozidloStavEnum.getSluzebniVozidloStavEnum(getIdSluzebniVozidloStav());
    }

}
