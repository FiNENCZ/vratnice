package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Uzivatel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "uzivatel_vratnice", schema = Constants.SCHEMA)
@NamedQuery(name="UzivatelVratnice.findAll", query = "SELECT s FROM UzivatelVratnice s")
public class UzivatelVratnice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_uzivatel_vratnice")
    private String idUzivatelVratnice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uzivatel")
    private Uzivatel uzivatel;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "uzivatel_vratnice_mapovani",
        joinColumns = @JoinColumn(name = "id_uzivatel_vratnice"),
        inverseJoinColumns = @JoinColumn(name = "id_vratnice")
    )
    private List<Vratnice> vratnice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nastavena_vratnice")
    private Vratnice nastavenaVratnice;

    private String poznamka;

    @Column(name = "cas_zmn")
    private Date casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    @Column(name = "aktivita")
    private Boolean aktivita = true;

    public UzivatelVratnice(String idUzivatelVratnice) {
        setIdUzivatelVratnice(idUzivatelVratnice);
    }
}
