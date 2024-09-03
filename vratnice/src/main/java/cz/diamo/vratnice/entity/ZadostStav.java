package cz.diamo.vratnice.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import cz.diamo.vratnice.enums.ZadostStavEnum;
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
@Table(name = "zadost_stav", schema = Constants.SCHEMA)
@NamedQuery(name = "ZadostStav.findAll", query = "SELECT s FROM ZadostStav s")
public class ZadostStav implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_zadost_stav")
    private Integer idZadostStav;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public ZadostStav(ZadostStavEnum value) {
        setIdZadostStav(value.getValue());
    }

    public ZadostStavEnum getZadostStavEnum() {
        return ZadostStavEnum.getZadostStavEnum(getIdZadostStav());
    }
}