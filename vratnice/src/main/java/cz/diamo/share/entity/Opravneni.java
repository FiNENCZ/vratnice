package cz.diamo.share.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

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
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "opravneni", schema = Constants.SCHEMA)
@NamedQuery(name = "Opravneni.findAll", query = "SELECT s FROM Opravneni s")
public class Opravneni implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_opravneni")
    private String idOpravneni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_opravneni_typ_pristupu")
    private OpravneniTypPristupu opravneniTypPristupu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_opravneni_typ_pristupu_budova")
    private OpravneniTypPristupuBudova opravneniTypPristupuBudova;

    private String kod;

    private String nazev;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    @Transient
    private List<Role> role;

    @Transient
    private List<PracovniPozicePrehled> pracovniPozice;

    @Transient
    private List<Budova> budovy;

    @Transient
    private List<Zavod> zavody;

    public Opravneni(String idOpravneni) {
        setIdOpravneni(idOpravneni);
    }
}