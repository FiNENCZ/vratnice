package cz.dp.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQuery;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Uzivatel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "historie_vypujcek", schema = Constants.SCHEMA)
@NamedQuery(name = "HistorieVypujce.findAll", query = "SELECT s from HistorieVypujcek s")
public class HistorieVypujcek  implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_historie_vypujcek")
    private String idHistorieVypujcek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zadost_klic")
    private ZadostKlic zadostKlic;
    
    @ManyToOne
    @JoinColumn(name = "id_historie_vypujcek_akce")
    private HistorieVypujcekAkce akce;

    @Column(name = "datum")
    private Date datum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vratny")
    private Uzivatel vratny;

    public HistorieVypujcek(String idHistorieVypujcek) {
        setIdHistorieVypujcek(idHistorieVypujcek);
    }


}
