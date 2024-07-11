package cz.diamo.vratnice.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import cz.diamo.vratnice.enums.StatEnum;
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
@Table(name = "stat", schema = Constants.SCHEMA)
@NamedQuery(name = "Stat.findAll", query = "SELECT s FROM Stat s")
public class Stat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_stat")
    private Integer idStat;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public Stat(StatEnum value) {
        setIdStat(value.getValue());
    }

    public StatEnum getStatEnum() {
        return StatEnum.getStatEnum(getIdStat());
    }

}
