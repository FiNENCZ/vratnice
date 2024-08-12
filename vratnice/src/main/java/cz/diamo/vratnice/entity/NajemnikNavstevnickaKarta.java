package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
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
@Table(name = "najemnik_navstevnicka_karta", schema = Constants.SCHEMA)
@NamedQuery(name = "NajemnikNavstevnickaKarta.findAll", query = "SELECT s from NajemnikNavstevnickaKarta s")
public class NajemnikNavstevnickaKarta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_najemnik_navstevnicka_karta")
    private String idNajemnikNavstevnickaKarta;

    private String jmeno;

    private String prijmeni;

    @Column(name = "cislo_op", unique = true)
    private String cisloOp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_spolecnost")
    private Spolecnost spolecnost;

    @Column(name = "cislo_najemni_smlouvy")
    private String cisloNajemniSmlouvy;

    @Column(name = "cislo_karty")
    private String cisloKarty;

    @Column(name = "duvod_vydani")
    private String duvodVydani;

    @Column(name = "vydano_od")
    private Date vydanoOd;

    @Column(name = "vydano_do")
    private Date vydanoDo;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public NajemnikNavstevnickaKarta(String idNajemnikNavstevnickaKarta) {
        setIdNajemnikNavstevnickaKarta(idNajemnikNavstevnickaKarta);
    }
}


