package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Uzivatel;
import jakarta.persistence.Column;
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
@Table(name = "navstevni_listek", schema = Constants.SCHEMA)
@NamedQuery(name = "NavstevniListek.findAll", query = "SELECT s from NavstevniListek s")
public class NavstevniListek implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_navstevni_listek")
    private String idNavstevniListek;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "navstevni_listek_navsteva_osoba",
        joinColumns = @JoinColumn(name = "id_navstevni_listek"),
        inverseJoinColumns = @JoinColumn(name = "id_navsteva_osoba")
    )
    private List<NavstevaOsoba> navstevaOsoba;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "navstevni_listek_uzivatel",
        joinColumns = @JoinColumn(name = "id_navstevni_listek"),
        inverseJoinColumns = @JoinColumn(name = "id_uzivatel")
    )
    private List<Uzivatel> uzivatel;

    @ManyToOne
    @JoinColumn(name = "id_navstevni_listek_typ")
    private NavstevniListekTyp typ;

    private String stav = "vyžádáno";

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public NavstevniListek(String idNavstevniListek) {
        setIdNavstevniListek(idNavstevniListek);
    }



}
