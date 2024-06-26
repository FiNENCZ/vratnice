package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_navsteva_osoba")
    private String idNavstevaOsoba;

    private String jmeno;

    private String prijmeni;

    @Column(name = "cislo_op")
    private String cisloOp;

    private String firma;

    @Column(name = "datum_pouceni")
    private Date datumPouceni;

    public NavstevaOsoba(String idNavstevaOsoba) {
        setIdNavstevaOsoba(idNavstevaOsoba);
    }

}
