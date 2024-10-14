package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.entity.Zavod;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "povoleni_vjezdu_vozidla", schema = Constants.SCHEMA)
@NamedQuery(name = "PovoleniVjezduVozidla.findAll", query = "SELECT s from PovoleniVjezduVozidla s")
public class PovoleniVjezduVozidla implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_povoleni_vjezdu_vozidla")
    private String idPovoleniVjezduVozidla;

    @Column(name = "jmeno_zadatele")
    private String jmenoZadatele;

    @Column(name = "prijmeni_zadatele")
    private String prijmeniZadatele;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spolecnost_zadatele")
    private Spolecnost spolecnostZadatele;

    @Column(name = "ico_zadatele")
    private String icoZadatele;

    @Column(name = "email_zadatele")
    private String emailZadatele;

    @Column(name = "duvod_zadosti")
    private String duvodZadosti;

    @ElementCollection
    @CollectionTable(name = "povoleni_vjezdu_vozidla_rz_vozidla", joinColumns = @JoinColumn(name = "id_povoleni_vjezdu_vozidla"))
    @Column(name = "rz_vozidla")
    private List<String> rzVozidla;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "povoleni_vjezdu_vozidla_typ_vozidla",
        joinColumns = @JoinColumn(name = "id_povoleni_vjezdu_vozidla"),
        inverseJoinColumns = @JoinColumn(name = "id_vozidlo_typ")
    )
    private List<VozidloTyp> typVozidla;

    @ManyToOne
    @JoinColumn(name = "id_stat")
    private Stat zemeRegistraceVozidla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ridic")
    private Ridic ridic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spolecnost_vozidla")
    private Spolecnost spolecnostVozidla;

    @Column(name = "datum_od")
    private Date datumOd;

    @Column(name = "datum_do")
    private Date datumDo;

    @Column(name = "datum_vytvoreni")
    private Date datumVytvoreni;

    @ManyToOne
    @JoinColumn(name = "id_zavod")
    private Zavod zavod;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "povoleni_vjezdu_vozidla_lokalita",
        joinColumns = @JoinColumn(name = "id_povoleni_vjezdu_vozidla"),
        inverseJoinColumns = @JoinColumn(name = "id_lokalita")
    )
    private List<Lokalita> lokality;

    @Column(name = "opakovany_vjezd")
    private Boolean opakovanyVjezd = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zadost_stav")
    private ZadostStav stav;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public PovoleniVjezduVozidla(String idPovoleniVjezduVozidla) {
        setIdPovoleniVjezduVozidla(idPovoleniVjezduVozidla);
    }
}
