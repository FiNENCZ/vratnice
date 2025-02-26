package cz.dp.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Lokalita;
import cz.dp.share.entity.Zavod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="sluzebni_vozidlo", schema = Constants.SCHEMA, uniqueConstraints = {@UniqueConstraint(columnNames = "rz")})
@NamedQuery(name="SluzebniVozidlo.findAll", query = "SELECT s FROM SluzebniVozidlo s")
public class SluzebniVozidlo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name="id_sluzebni_vozidlo")
    private String idSluzebniVozidlo;

    @Column(name = "rz", unique = true)
    private String rz;

    @ManyToOne
    @JoinColumn(name = "id_vozidlo_typ")
    private VozidloTyp typ;

    @ManyToOne
    @JoinColumn(name = "id_sluzebni_vozidlo_kategorie")
    private SluzebniVozidloKategorie kategorie;

    @ManyToOne
    @JoinColumn(name = "id_sluzebni_vozidlo_funkce")
    private SluzebniVozidloFunkce funkce; // pouze u kategorie vozidla manažerské – např. ředitel, náměstek

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_zavod")
    private Zavod zavod;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sluzebni_vozidlo_lokalita",
        joinColumns = @JoinColumn(name = "id_sluzebni_vozidlo"),
        inverseJoinColumns = @JoinColumn(name = "id_lokalita")
    )
    private List<Lokalita> lokality;
    
    @ManyToOne
    @JoinColumn(name = "id_sluzebni_vozidlo_stav")
    private SluzebniVozidloStav stav;

    @Column(name = "datum_od")
    private Date datumOd;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public SluzebniVozidlo(String idSluzebniVozidlo) {
        setIdSluzebniVozidlo(idSluzebniVozidlo);
    }

}
