package cz.dp.vratnice.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Uzivatel;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "specialni_klic_oznameni_vypujcky", schema = Constants.SCHEMA)
@NamedQuery(name="SpecialniKlicOznameniVypujcky.findAll", query = "SELECT s FROM SpecialniKlicOznameniVypujcky s")
public class SpecialniKlicOznameniVypujcky implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_specialni_klic_oznameni_vypujcky")
    private String idSpecialniKlicOznameniVypujcky;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_klic")
    private Klic klic;

    @ManyToMany
    @JoinTable(
        name = "specialni_klic_oznameni_uzivatel",
        joinColumns = @JoinColumn(name = "id_specialni_klic_oznameni_vypujcky"),
        inverseJoinColumns = @JoinColumn(name = "id_uzivatel")
    )
    private List<Uzivatel> uzivatele;

    private String poznamka;

    @Column(name = "cas_zmn")
    private Date casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    @Column(name = "aktivita")
    private Boolean aktivita = true;

    public SpecialniKlicOznameniVypujcky(String idSpecialniKlicOznameniVypujcky) {
        setIdSpecialniKlicOznameniVypujcky(idSpecialniKlicOznameniVypujcky);
    }

}
