package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Lokalita;
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
@Table(name="vratnice", schema = Constants.SCHEMA)
@NamedQuery(name="Vratnice.findAll", query = "SELECT s FROM Vratnice s")
public class Vratnice implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name="id_vratnice")
    private String idVratnice;

    private String nazev;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_zavod")
    private Zavod zavod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_lokalita")
    private Lokalita lokalita;

    private Boolean osobni = false;

    private Boolean navstevni = false;
    
    private Boolean vjezdova = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_navstevni_listek_typ")
    private NavstevniListekTyp vstupniKartyTyp;

    @Column(name = "odchozi_turniket")
    private Boolean odchoziTurniket = false;

    private String poznamka;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public Vratnice(String idVratnice) {
        setIdVratnice(idVratnice);
    }

}
