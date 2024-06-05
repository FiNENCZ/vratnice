package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQuery;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Uzivatel;
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
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_historie_vypujcek")
    private String idHistorieVypujcek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zadost_klic")
    private ZadostKlic zadostKlic;
    
    @Column(name = "stav")
    private String stav;

    @Column(name = "datum")
    private Date datum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vratny")
    private Uzivatel vratny;

    public HistorieVypujcek(String idHistorieVypujcek) {
        setIdHistorieVypujcek(idHistorieVypujcek);
    }


}
