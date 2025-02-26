package cz.dp.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import cz.dp.share.constants.Constants;
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
@Table(name = "databaze", schema = Constants.SCHEMA)
@NamedQuery(name = "Databaze.findAll", query = "SELECT s FROM Databaze s")
public class Databaze implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_databaze")
    private Integer idDatabaze;

    @Column(name = "db_prefix")
    private String dbPrefix;

    @Column(name = "verze_db")
    private Integer verzeDb;

    @Column(name = "sub_verze_db")
    private Integer subVerzeDb;

    @Column(name = "demo")
    private Boolean demo;

    private String poznamka;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    @Column(name = "kc_uzivatele_jmeno")
    private String kcUzivateleJmeno;

    @Column(name = "kc_uzivatele_heslo")
    private String kcUzivateleHeslo;

    @Transient
    private Boolean povolenaDatabaze = true;

    @Transient
    private String minVerzeDb;

    public String getVerzeTxt() {
        return getVerzeDb() + "." + getSubVerzeDb();
    }
}