package cz.dp.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
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
    @GenericGenerator(name = "id", strategy = "cz.dp.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_budova")
    private String idBudova;

    @Column(name = "id_externi")
    private String idExterni;

    private String nazev;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lokalita")
    private Lokalita lokalita;

    private Boolean aktivita = true;

    private String poznamka;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public Budova(String idBudova) {
        setIdBudova(idBudova);
    }
}
