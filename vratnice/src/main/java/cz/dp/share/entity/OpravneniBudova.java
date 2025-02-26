package cz.dp.share.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@IdClass(OpravneniBudova.class)
@Table(name = "opravneni_budova", schema = Constants.SCHEMA)
@NamedQuery(name = "OpravneniBudova.findAll", query = "SELECT s FROM OpravneniBudova s")
public class OpravneniBudova implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_opravneni")
    private String idOpravneni;

    @Id
    @Column(name = "id_budova")
    private String idBudova;

    public OpravneniBudova(Opravneni opravneni, Budova budova) {
        setIdOpravneni(opravneni.getIdOpravneni());
        setIdBudova(budova.getIdBudova());
    }

}