package cz.dp.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Uzivatel;
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
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_historie_sluzebni_vozidlo")
    private String idHistorieSluzebniAuto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sluzebni_vozidlo")
    private SluzebniVozidlo sluzebniVozidlo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historie_sluzebni_vozidlo_akce")
    private HistorieSluzebniVozidloAkce akce;

    @Column(name = "datum")
    private Date datum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_uzivatel")
    private Uzivatel uzivatel;

    public HistorieSluzebniVozidlo(String idHistorieSluzebniVozidlo) {
        setIdHistorieSluzebniAuto(idHistorieSluzebniVozidlo);
    }


}
