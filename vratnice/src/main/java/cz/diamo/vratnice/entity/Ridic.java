package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
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
@Table(name = "ridic", schema = Constants.SCHEMA)
@NamedQuery(name = "Ridic.findAll", query = "SELECT s from Ridic s")
public class Ridic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_ridic")
    private String idRidic;

    private String jmeno;

    private String prijmeni;

    @Column(name = "cislo_op", unique = true)
    private String cisloOp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_spolecnost")
    private Spolecnost spolecnost;
    
    @Column(name = "datum_pouceni")
    private Date datumPouceni;

    public Ridic(String idRidic) {
        setIdRidic(idRidic);
    }
}
