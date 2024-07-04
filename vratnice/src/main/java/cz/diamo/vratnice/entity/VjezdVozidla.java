package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

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
@Table(name = "vjezd_vozidla", schema = Constants.SCHEMA)
@NamedQuery(name = "VjezdVozidla.findAll", query = "SELECT s from VjezdVozidla s")
public class VjezdVozidla implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_vjezd_vozidla")
    private String idVjezdVozidla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ridic")
    private Ridic ridic;

    @Column(name = "rz_vozidla")
    private String rzVozidla;

    @Column(name = "typ_vozidla")
    private String typVozidla;

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

// Jméno (povinná nebo nepovinná položka dle konfigurace vrátnice – doplněno z rozhraní čtečky OP nebo ručním vstupem z klávesnice)
// Příjmení (povinná nebo nepovinná položka dle konfigurace vrátnice – doplněno z rozhraní čtečky OP nebo ručním vstupem z klávesnice)
// Číslo OP (povinná nebo nepovinná položka dle konfigurace vrátnice – doplněno z rozhraní čtečky OP nebo ručním vstupem z klávesnice)
// Firma (nepovinná položka) – ruční vstup, volné textové pole
// Datum poučení – barevně (červeně nebo zeleně) bude vysvíceno políčko s datem posledního podpisu poučení pro vjezd (bezpečnost, PO/BOZP apod.). Platnost poučení (změna podbarvení) bude konfigurovatelná. V současnosti je platnost 1 rok (12 měsíců). Bude zadáván počet měsíců.
// RZ (povinná položka) – doplněno z rozhraní kamery, případně ruční vstup
// Druh vozidla (povinná položka) – u schválených vjezdů předvyplněno ze schválení
// Opakovaný vjezd – Pro případ tzv. koloběhu se vyplňuje číslo přidělené tabule.
// Čas příjezdu


}
