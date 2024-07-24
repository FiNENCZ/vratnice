package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.entity.Budova;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Data
@NoArgsConstructor
@Entity
@Table(name = "poschodi", schema = Constants.SCHEMA)
@NamedQuery(name = "Poschodi.findAll", query = "SELECT s from Poschodi s")
public class Poschodi implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_poschodi")
    private String idPoschodi;

    private String nazev;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_budova")
    private Budova budova;

    private Boolean aktivita = true;

    @Column(name = "cas_zmn")
    private Timestamp casZmn;

    @Column(name = "zmenu_provedl")
    private String zmenuProvedl;

    public Poschodi(String idPoschodi) {
        setIdPoschodi(idPoschodi);
    }
}
