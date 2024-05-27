package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.sql.Date;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.enums.StavZadostKlic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@NamedQuery(name = "ZadostKlic.findAll", query = "SELECT s from Uzivatel s")
public class ZadostKlic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "stav")
    private StavZadostKlic stav; // vyžádán/schválen

    
    @Column(name = "trvala")
    private Boolean trvala = true;


    @Column(name = "datum_od")
    private Date datumOd;

    @Column(name = "datum_do")
    private Date datumDo;

    public ZadostKlic(String idZadostKlic) {
        setIdZadostKlic(idZadostKlic);
    }

}
