package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Uzivatel;
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
@Table(name = "povoleni_vjezdu_vozidla_zmena_stavu", schema = Constants.SCHEMA)
@NamedQuery(name = "PovoleniVjezduVozidlaZmenaStavu.findAll", query = "SELECT s FROM PovoleniVjezduVozidlaZmenaStavu s")
public class PovoleniVjezduVozidlaZmenaStavu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_povoleni_vjezdu_vozidla_zmena_stavu")
    private BigInteger idZadostZmenaStavu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_povoleni_vjezdu_vozidla")
    private PovoleniVjezduVozidla povoleniVjezduVozidla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_povoleni_vjezdu_vozidla_stav_novy")
    private ZadostStav stavNovy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_povoleni_vjezdu_vozidla_stav_puvodni")
    private ZadostStav stavPuvodni;

    @Column(name = "aktivita_novy")
    private Boolean aktivitaNovy;

    @Column(name = "aktivita_puvodni")
    private Boolean aktivitaPuvodni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uzivatel")
    private Uzivatel uzivatel;

    private Date cas;

}
