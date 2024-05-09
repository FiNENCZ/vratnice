package cz.diamo.share.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.enums.OpravneniTypPristupuEnum;
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
@Table(name = "opravneni_typ_pristupu", schema = Constants.SCHEMA)
@NamedQuery(name = "OpravneniTypPristupu.findAll", query = "SELECT s FROM OpravneniTypPristupu s")
public class OpravneniTypPristupu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_opravneni_typ_pristupu")
    private Integer idOpravneniTypPristupu;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public OpravneniTypPristupu(OpravneniTypPristupuEnum opravneniTypPristupuEnum) {
        setIdOpravneniTypPristupu(opravneniTypPristupuEnum.getValue());
    }

    public OpravneniTypPristupuEnum getOpravneniTypPristupuEnum() {
        return OpravneniTypPristupuEnum.getOpravneniTypPristupuEnum(getIdOpravneniTypPristupu());
    }
}