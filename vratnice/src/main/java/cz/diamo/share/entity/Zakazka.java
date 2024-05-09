package cz.diamo.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Zakázka
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "zakazka", schema = Constants.SCHEMA)
@NamedQuery(name = "Zakazka.findAll", query = "SELECT s FROM Zakazka s")
public class Zakazka implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_zakazka")
    private String idZakazka;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zavod")
    private Zavod zavod;

    private String nazev;

    @Column(name = "sap_id")
    private String sapId;

    @Column(name = "platnost_od")
    private Date platnostOd;

    @Column(name = "platnost_do")
    private Date platnostDo;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public Zakazka(String idZakazka) {
        setIdZakazka(idZakazka);
    }
}