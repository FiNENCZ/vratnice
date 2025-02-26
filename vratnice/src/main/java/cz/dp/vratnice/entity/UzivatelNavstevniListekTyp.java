package cz.dp.vratnice.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Uzivatel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@IdClass(UzivatelNavstevniListekTyp.class)
@Table(name = "uzivatel_navstevni_listek_typ", schema = Constants.SCHEMA)
@NamedQuery(name = "UzivatelNavstevniListekTyp.findAll", query = "SELECT s FROM UzivatelNavstevniListekTyp s")
public class UzivatelNavstevniListekTyp implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Id
    @Column(name = "id_navstevni_listek_typ")
    private Integer idNavstevniListekTyp;

    public UzivatelNavstevniListekTyp(Uzivatel uzivatel, NavstevniListekTyp navstevniListekTyp) {
        setIdUzivatel(uzivatel.getIdUzivatel());
        setIdNavstevniListekTyp(navstevniListekTyp.getIdNavstevniListekTyp());
    }
}
