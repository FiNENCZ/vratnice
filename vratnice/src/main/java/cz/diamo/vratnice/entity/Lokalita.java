package cz.diamo.vratnice.entity;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
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
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_lokalita")
    private String idLokalita;
    
    private String nazev;
    
    /* 
    @OneToMany(mappedBy = "lokalita", fetch = FetchType.LAZY)
    private List<Budova> budovy; */

    public Lokalita(String idLokalita) {
        setIdLokalita(idLokalita);
    }
}