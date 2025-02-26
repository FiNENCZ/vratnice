package cz.dp.vratnice.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.dp.share.constants.Constants;
import cz.dp.share.entity.Uzivatel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "zadost_klic", schema = Constants.SCHEMA)
@NamedQuery(name = "ZadostKlic.findAll", query = "SELECT s from ZadostKlic s")
public class ZadostKlic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.dp.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_zadost_klic")
    private String idZadostKlic;

    //Jaký klíč
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_klic")
    private Klic klic;

    //Kdo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uzivatel")
    private Uzivatel uzivatel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zadost_stav")
    private ZadostStav zadostStav;
    
    @Column(name = "trvala")
    private Boolean trvala = true;


    @Column(name = "datum_od")
    private Date datumOd;

    @Column(name = "datum_do")
    private Date datumDo;

    @Column(name = "duvod")
    private String duvod;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public ZadostKlic(String idZadostKlic) {
        setIdZadostKlic(idZadostKlic);
    }
    

}
