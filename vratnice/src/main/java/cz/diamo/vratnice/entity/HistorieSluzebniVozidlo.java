package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Uzivatel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "historie_sluzebni_vozidlo", schema = Constants.SCHEMA)
@NamedQuery(name = "HistorieSluzebniVozidlo.findAll", query = "SELECT s from HistorieSluzebniVozidlo s")
public class HistorieSluzebniVozidlo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_historie_sluzebni_vozidlo")
    private String idHistorieSluzebniAuto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sluzebni_vozidlo")
    private SluzebniVozidlo sluzebniVozidlo;

    @Column(name = "akce")
    private String akce;

    @Column(name = "datum")
    private Date datum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_uzivatel")
    private Uzivatel uzivatel;

    public HistorieSluzebniVozidlo(String idHistorieSluzebniVozidlo) {
        setIdHistorieSluzebniAuto(idHistorieSluzebniVozidlo);
    }


}
