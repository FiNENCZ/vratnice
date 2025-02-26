package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.vratnice.enums.VozidloTypEnum;
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
@Table(name = "vozidlo_typ", schema = Constants.SCHEMA)
@NamedQuery(name = "VozidloTyp.findAll", query = "SELECT s FROM VozidloTyp s")
public class VozidloTyp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_vozidlo_typ")
    private Integer idVozidloTyp;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public VozidloTyp(VozidloTypEnum value) {
        setIdVozidloTyp(value.getValue());
    }

    public VozidloTypEnum getVozidloTypEnum() {
        return VozidloTypEnum.getVozidloTypEnum(getIdVozidloTyp());
    }
}
