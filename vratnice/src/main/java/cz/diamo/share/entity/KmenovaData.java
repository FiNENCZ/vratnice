package cz.diamo.share.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "kmenova_data", schema = Constants.SCHEMA)
@NamedQuery(name = "KmenovaData.findAll", query = "SELECT s FROM KmenovaData s")
public class KmenovaData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_kmenova_data")
    private String idKmenovaData;

    @Column(name = "guid_davky")
    private String guidDavky;

    @Column(name = "sap_id")
    private String sapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zavod")
    private Zavod zavod;

    @Column(name = "druh_vyneti_sap_id")
    private String druhVynetiSapId;

    @Column(name = "druh_prac_pomeru_sap_id")
    private String druhPracPomeruSapId;

    @Column(name = "forma_mzdy_sap_id")
    private String formaMzdySapId;

    @Column(name = "platnost_ke_dni")
    private Date platnostKeDni;

    @Column(name = "cislo_znamky")
    private String cisloZnamky;

    @Column(name = "rodne_cislo")
    private String rodneCislo;

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

    private String tel;

    private String email;

    @Column(name = "soukromy_email")
    private String soukromyEmail;

    @Column(name = "datum_ukonceni_prac_pomeru")
    private Date datumUkonceniPracPomeru;

    @Column(name = "denni_uvazek")
    private BigDecimal denniUvazek;

    @Column(name = "kategorie_sap_id")
    private String kategorieSapId;

    @Column(name = "kalendar_sap_id")
    private String kalendarSapId;

    @Column(name = "zakazka_sap_id")
    private String zakazkaSapId;

    @Column(name = "narok_na_dovolenou")
    private BigDecimal narokNaDovolenou;

    @Column(name = "zbytek_dovolene_minuly_rok")
    private BigDecimal zbytekDovoleneMinulyRok;

    @Column(name = "dodatkova_dovolena")
    private BigDecimal dodatkovaDovolena;

    @Column(name = "cerpani_dovolene")
    private BigDecimal cerpaniDovolene;

    @Column(name = "prumer_pro_nahrady")
    private BigDecimal prumerProNahrady;

    @Column(name = "personalni_oblast_sap_id")
    private String personalniOblastSapId;

    @Column(name = "dilci_personalni_oblast_sap_id")
    private String dilciPersonalniOblastSapId;

    @Column(name = "zauctovaci_okruh_sap_id")
    private String zauctovaciOkruhSapId;

    @Column(name = "orgnizacni_jednotka_sap_id")
    private String orgnizacniJednotkaSapId;

    @Column(name = "planovane_misto_sap_id")
    private String planovaneMistoSapId;

    @Column(name = "skupina_zamestnance_sap_id")
    private String skupinaZamestnanceSapId;

    @Column(name = "cip_1")
    private String cip1;

    @Column(name = "cip_2")
    private String cip2;

    @Column(name = "pruzna_prac_doba")
    private Boolean pruznaPracDoba = false;

    @Column(name = "zpracovano")
    private Boolean zpracovano = false;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;
}