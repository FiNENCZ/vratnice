package cz.diamo.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pracovni_pozice", schema = Constants.SCHEMA)
@NamedQuery(name = "PracovniPozice.findAll", query = "SELECT s FROM PracovniPozice s")
public class PracovniPozice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_pracovni_pozice")
    private String idPracovniPozice;

    @Column(name = "sap_id")
    private String sapId;

    @Column(name = "sap_id_nadrizeny")
    private String sapIdNadrizeny;

    @Column(name = "platnost_od")
    private Date platnostOd;

    @Column(name = "platnost_do")
    private Date platnostDo;

    @Column(name = "cas_aktualizace")
    private Date casAktualizace;

    private String nazev;

    private String zkratka;

    private Boolean dohoda = false;

    @Column(name = "sap_id_dohodar")
    private String sapIdDohodar;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public PracovniPozice(String idPracovniPozice) {
        setIdPracovniPozice(idPracovniPozice);
    }
}