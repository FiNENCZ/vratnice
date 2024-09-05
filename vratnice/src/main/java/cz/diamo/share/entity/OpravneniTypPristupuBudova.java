package cz.diamo.share.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.enums.OpravneniTypPristupuBudovaEnum;
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
@Table(name = "opravneni_typ_pristupu_budova", schema = Constants.SCHEMA)
@NamedQuery(name = "OpravneniTypPristupuBudova.findAll", query = "SELECT s FROM OpravneniTypPristupuBudova s")
public class OpravneniTypPristupuBudova implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_opravneni_typ_pristupu_budova")
    private Integer idOpravneniTypPristupuBudova;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public OpravneniTypPristupuBudova(OpravneniTypPristupuBudovaEnum opravneniTypPristupuBudovaEnum) {
        setIdOpravneniTypPristupuBudova(opravneniTypPristupuBudovaEnum.getValue());
    }

    public OpravneniTypPristupuBudovaEnum getOpravneniTypPristupuBudovaEnum() {
        return OpravneniTypPristupuBudovaEnum.getOpravneniTypPristupuBudovaEnum(getIdOpravneniTypPristupuBudova());
    }
}