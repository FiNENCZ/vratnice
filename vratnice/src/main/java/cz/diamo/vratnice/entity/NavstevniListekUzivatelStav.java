package cz.diamo.vratnice.entity;


import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.enums.NavstevniListekStavEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "navstevni_listek_uzivatel_stav", schema = Constants.SCHEMA)
@NamedQuery(name = "NavstevniListekUzivatelStav.findAll", query = "SELECT s FROM NavstevniListekUzivatelStav s")
public class NavstevniListekUzivatelStav implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_navstevni_listek_uzivatel_stav")
    private String idNavstevniListekUzivatelStav;

    @ManyToOne
    @JoinColumn(name = "id_navstevni_listek")
    private NavstevniListek navstevniListek;

    @ManyToOne
    @JoinColumn(name = "id_uzivatel")
    private Uzivatel uzivatel;

    @ManyToOne
    @JoinColumn(name = "id_navstevni_listek_stav")
    private NavstevniListekStav stav = new NavstevniListekStav(NavstevniListekStavEnum.KE_ZPRACOVANI);

    private String poznamka;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;


    public NavstevniListekUzivatelStav(Uzivatel uzivatel, NavstevniListekStav stav) {
        this.uzivatel = uzivatel;
        this.stav = stav;
    }

}
