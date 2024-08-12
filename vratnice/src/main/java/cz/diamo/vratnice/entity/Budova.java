package cz.diamo.vratnice.entity;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "budova", schema = Constants.SCHEMA)
@NamedQuery(name = "Budova.findAll", query = "SELECT s from Budova s")
public class Budova implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_budova")
    private String idBudova;

    private String nazev;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lokalita")
    private Lokalita lokalita;

    public Budova(String idBudova) {
        setIdBudova(idBudova);
    }
}
