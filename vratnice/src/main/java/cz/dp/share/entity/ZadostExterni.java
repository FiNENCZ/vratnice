package cz.dp.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import cz.dp.share.constants.Constants;
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

@Data
@NoArgsConstructor
@Entity
@Table(name = "zadost_externi", schema = Constants.SCHEMA)
@NamedQuery(name = "ZadostExterni.findAll", query = "SELECT s FROM ZadostExterni s")
public class ZadostExterni implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_zadost_externi")
    private String idZadostExterni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uzivatel")
    private Uzivatel uzivatel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uzivatel_vytvoril")
    private Uzivatel uzivatelVytvoril;

    private Date cas;

    @Column(name = "datum_predani")
    private Date datumPredani;

    private String typ;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;
}