package cz.diamo.vratnice.entity;


import java.io.Serializable;

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
@Table(name = "navsteva_uzivatel_stav", schema = Constants.SCHEMA)
@NamedQuery(name = "NavstevaUzivatelStav.findAll", query = "SELECT s FROM NavstevaUzivatelStav s")
public class NavstevaUzivatelStav implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_navsteva_uzivatel_stav")
    private String idNavstevaUzivatelStav;

    @ManyToOne
    @JoinColumn(name = "id_uzivatel")
    private Uzivatel uzivatel;

    @ManyToOne
    @JoinColumn(name = "id_navstevni_listek_stav")
    private NavstevniListekStav stav = new NavstevniListekStav(NavstevniListekStavEnum.KE_ZPRACOVANI);


    public NavstevaUzivatelStav(Uzivatel uzivatel, NavstevniListekStav stav) {
        this.uzivatel = uzivatel;
        this.stav = stav;
    }

}
