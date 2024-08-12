package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
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

    @Column(name = "spolecnost_zadatele")
    private String spolecnostZadatele;

    @Column(name = "ico_zadatele")
    private String icoZadatele;

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

    @Column(name = "spolecnost_vozidla")
    private String spolecnostVozidla;

    @Column(name = "datum_od")
    private Date datumOd;

    @Column(name = "datum_do")
    private Date datumDo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "povoleni_vjezdu_vozidla_zavod",
        joinColumns = @JoinColumn(name = "id_povoleni_vjezdu_vozidla"),
        inverseJoinColumns = @JoinColumn(name = "id_zavod")
    )
    private List<Zavod> zavod;

    @Column(name = "opakovany_vjezd")
    private Boolean opakovanyVjezd = false;

    private String stav = "vyžádáno";

    public PovoleniVjezduVozidla(String idPovoleniVjezduVozidla) {
        setIdPovoleniVjezduVozidla(idPovoleniVjezduVozidla);
    }




// Jméno žadatele (povinná položka)
// Příjmení žadatele (povinná položka)
// Společnost žadatele (povinná položka)
// IČO žadatele (nepovinná položka)
// Na základě čeho se žádá (nepovinná položka)
// RZ vozidla – je možné zadat RZ více vozidel a současně i typ vozidel (také možné importovat ze souboru CSV) 
// Typ vozidla (osobní, dodávka, nákladní, speciální)

// Země registrace vozidla (číselník)
// Řidič (nepovinné) – pokud bude řidič zadán, bude předvyplněn při identifikaci dané RZ s možností změny a bude možnost přiložit scan podepsaného prohlášení o dodržování pravidel pro vjezd.
// Společnost, které vozidlo patři (nepovinné)
// Období od - do (u jednorázového vjezdu budou obě data stejná)
// Lokalita (závod) – výběr z číselníku, i vícenásobný
// Důvod vjezdu (volný text)
// Opakovaný vjezd (koloběh) – zatržítko pro zvláštní druh opakovaného vjezdu
// Informaci, že žádost musí být odeslána 3 dny předem (její odeslání ale nebude blokováno, i když nebude termín dodržen)

}
