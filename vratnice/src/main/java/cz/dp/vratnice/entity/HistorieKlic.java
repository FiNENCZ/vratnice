package cz.dp.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Uzivatel;
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
@Table(name = "historie_klic", schema = Constants.SCHEMA)
@NamedQuery(name = "HistorieKlic.findAll", query = "SELECT s from HistorieKlic s")
public class HistorieKlic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_historie_klic")
    private String idHistorieKlic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_klic")
    private Klic klic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historie_klic_akce")
    private HistorieKlicAkce akce;

    private String duvod;

    @Column(name = "datum")
    private Date datum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_uzivatel")
    private Uzivatel uzivatel;

    public HistorieKlic(String idHistorieKlic) {
        setIdHistorieKlic(idHistorieKlic);
    }

}
