package cz.dp.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "zavod", schema = Constants.SCHEMA)
@NamedQuery(name = "Zavod.findAll", query = "SELECT s FROM Zavod s")
public class Zavod implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_zavod")
    private String idZavod;

    @Column(name = "sap_id")
    private String sapId;

    private String nazev;

    @Column(name = "barva_pozadi")
    private String barvaPozadi;

    @Column(name = "barva_pisma")
    private String barvaPisma;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public Zavod(String idZavod) {
        setIdZavod(idZavod);
    }
}