package cz.diamo.vratnice.entity;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
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
@Table(name = "lokalita", schema = Constants.SCHEMA)
@NamedQuery(name = "Lokalita.findAll", query = "SELECT s from Lokalita s")
public class Lokalita implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_lokalita")
    private String idLokalita;
    
    private String nazev;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zavod")
    private Zavod zavod;

    public Lokalita(String idLokalita) {
        setIdLokalita(idLokalita);
    }
}