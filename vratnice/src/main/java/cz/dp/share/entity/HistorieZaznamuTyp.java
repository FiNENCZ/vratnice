package cz.dp.share.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.share.enums.HistorieZaznamuTypEnum;
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
@Table(name = "historie_zaznamu_typ", schema = Constants.SCHEMA)
@NamedQuery(name = "HistorieZaznamuTyp.findAll", query = "SELECT s FROM HistorieZaznamuTyp s")
public class HistorieZaznamuTyp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_historie_zaznamu_typ")
    private Integer idHistorieZaznamuTyp;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public HistorieZaznamuTyp(HistorieZaznamuTypEnum historieZaznamuTypEnum) {
        setIdHistorieZaznamuTyp(historieZaznamuTypEnum.getValue());
    }

    public HistorieZaznamuTypEnum getHistorieZaznamuTypEnum() {
        return HistorieZaznamuTypEnum.getHistorieZaznamuTypEnum(getIdHistorieZaznamuTyp());
    }
}