package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Zavod;
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
@Table(name="sluzebni_vozidlo", schema = Constants.SCHEMA)
@NamedQuery(name="SluzebniVozidlo.findAll", query = "SELECT s FROM SluzebniVozidlo s")
public class SluzebniVozidlo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name="id_sluzebni_vozidlo")
    private String idSluzebniVozidlo;

    private String typ;

    private String kategorie;

    private String funkce; // pouze u kategorie vozidla manažerské – např. ředitel, náměstek

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_zavod")
    private Zavod zavod; // výběr z číselníku (i vícenásobný), kam může vozidlo jet, manažerské může kamkoliv, ostatní jen závod, jinak se žádá sekretariát

    private String lokalita;
    
    private String stav;

    @Column(name = "datum_od")
    private Date datumOd;

    public SluzebniVozidlo(String idSluzebniVozidlo) {
        setIdSluzebniVozidlo(idSluzebniVozidlo);
    }

}
