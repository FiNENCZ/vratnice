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
@Table(name = "vyjezd_vozidla", schema = Constants.SCHEMA)
@NamedQuery(name = "VyjezdVozidla.findAll", query = "SELECT s from VyjezdVozidla s")
public class VyjezdVozidla implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_vyjezd_vozidla")
    private String idVyjezdVozidla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vratnice")
    private Vratnice vratnice;

    @Column(name = "rz_vozidla")
    private String rzVozidla;

    private Boolean naklad = false;

    @Column(name = "cislo_pruchodky")
    private String cisloPruchodky;

    @Column(name = "opakovany_vjezd")
    private Boolean opakovanyVjezd;

    @Column(name = "cas_odjezdu")
    private ZonedDateTime casOdjezdu;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public VyjezdVozidla(String idVyjezdVozidla){
        setIdVyjezdVozidla(idVyjezdVozidla);
    }
}
