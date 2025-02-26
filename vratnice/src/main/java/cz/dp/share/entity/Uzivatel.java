package cz.dp.share.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "uzivatel", schema = Constants.SCHEMA)
@NamedQuery(name = "Uzivatel.findAll", query = "SELECT s FROM Uzivatel s")
public class Uzivatel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Column(name = "sap_id")
    private String sapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zavod")
    private Zavod zavod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zavod_vyber")
    private Zavod zavodVyber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zakazka")
    private Zakazka zakazka;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kmenova_data")
    private KmenovaData kmenovaData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pracovni_pozice")
    private PracovniPozice pracovniPozice;

    private String nazev;

    private String jmeno;

    private String prijmeni;

    @Column(name = "titul_pred")
    private String titulPred;

    @Column(name = "titul_za")
    private String titulZa;

    private String ulice;

    private String psc;

    private String obec;

    @Column(name = "cislo_popisne")
    private String cisloPopisne;

    @Column(name = "dilci_personalni_oblast")
    private String dilciPersonalniOblast;

    private String email;

    @Column(name = "soukromy_email")
    private String soukromyEmail;

    private String tel;

    @Column(name = "datum_od")
    private Date datumOd;

    @Column(name = "datum_do")
    private Date datumDo;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Date casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    @Column(name = "platnost_ke_dni")
    private Date platnostKeDni;

    @Column(name = "cas_aktualizace")
    private Date casAktualizace;

    private Boolean ukonceno = false;

    @Column(name = "id_zastup")
    private String idZastup;

    @Column(name = "pruzna_prac_doba")
    private Boolean pruznaPracDoba;

    @Column(name = "cip_1")
    private String cip1;

    @Column(name = "cip_2")
    private String cip2;

    @Transient
    private Uzivatel zastup;

    private Boolean externi = false;

    @Transient
    private List<Role> role;

    @Transient
    private List<Opravneni> opravneni;

    @Transient
    private List<Zavod> ostatniZavody;

    @Transient
    private List<String> moduly;

    @Transient
    private boolean zmena = false;

    public Uzivatel(String idUzivatel) {
        setIdUzivatel(idUzivatel);
    }
}