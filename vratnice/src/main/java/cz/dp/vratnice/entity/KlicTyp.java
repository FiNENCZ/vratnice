package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.vratnice.enums.KlicTypEnum;
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
@Table(name = "klic_typ", schema = Constants.SCHEMA)
@NamedQuery(name = "KlicTyp.findAll", query = "SELECT s FROM KlicTyp s")
public class KlicTyp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_klic_typ")
    private Integer idKlicTyp;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public KlicTyp(KlicTypEnum value) {
        setIdKlicTyp(value.getValue());
    }

    public KlicTypEnum getKlicTypEnum() {
        return KlicTypEnum.getKlicTypEnum(getIdKlicTyp());
    }
}
