package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
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
@Table(name = "klic", schema= Constants.SCHEMA)
@NamedQuery(name = "Klic.findAll", query = "SELECT s FROM Klic s")
public class Klic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name="id_klic")
    private String idKlic;

    @Column(name = "specialni")
    private boolean specialni;

    @Column(name = "nazev")
    private String nazev;

    @Column(name = "kod_cipu")
    private String kodCipu;

    @Column(name = "lokalita")
    private String lokalita;

    @Column(name = "budova")
    private String budova;

    @Column(name = "poschodi")
    private Integer poschodi;

    @Column(name = "mistnost")
    private String mistnost;

    @Column(name = "typ_klice")
    private String typKlice;

    @Column(name = "status")
    private String stav = "dostupný";

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public Klic(String idKlic) {
        setIdKlic(idKlic);
    }
}
