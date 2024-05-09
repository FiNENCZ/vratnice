package cz.diamo.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zastup", schema = Constants.SCHEMA)
@NamedQuery(name = "Zastup.findAll", query = "SELECT s FROM Zastup s")
@Data
@NoArgsConstructor
public class Zastup implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String guid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uzivatel")
    private Uzivatel uzivatel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uzivatel_zastupce")
    private Uzivatel uzivatelZastupce;

    @Column(name = "platnost_od")
    private Date platnostOd;

    @Column(name = "platnost_do")
    private Date platnostDo;

    private Boolean distribuovano = false;

    @Column(name = "chyba_distribuce")
    private String chybaDistribuce;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;
}