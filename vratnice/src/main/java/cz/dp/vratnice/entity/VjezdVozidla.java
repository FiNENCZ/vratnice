package cz.dp.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

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
@Table(name = "vjezd_vozidla", schema = Constants.SCHEMA)
@NamedQuery(name = "VjezdVozidla.findAll", query = "SELECT s from VjezdVozidla s")
public class VjezdVozidla implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_vjezd_vozidla")
    private String idVjezdVozidla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vratnice")
    private Vratnice vratnice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ridic")
    private Ridic ridic;

    @Column(name = "rz_vozidla")
    private String rzVozidla;

    @ManyToOne
    @JoinColumn(name = "id_vozidlo_typ")
    private VozidloTyp typVozidla;

    @Column(name = "opakovany_vjezd")
    private Integer opakovanyVjezd;

    @Column(name = "cas_prijezdu")
    private ZonedDateTime casPrijezdu;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;


    public VjezdVozidla(String idVjezdVozidla){
        setIdVjezdVozidla(idVjezdVozidla);
    }
}
