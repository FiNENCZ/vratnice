package cz.dp.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
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
@Table(name = "navsteva_osoba", schema = Constants.SCHEMA)
@NamedQuery(name = "NavstevaOsoba.findAll", query = "SELECT s from NavstevaOsoba s")
public class NavstevaOsoba implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_navsteva_osoba")
    private String idNavstevaOsoba;

    private String jmeno;

    private String prijmeni;

    @Column(name = "cislo_op")
    private String cisloOp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_spolecnost")
    private Spolecnost spolecnost;

    @Column(name = "datum_pouceni")
    private Date datumPouceni;

    public NavstevaOsoba(String idNavstevaOsoba) {
        setIdNavstevaOsoba(idNavstevaOsoba);
    }

}
