package cz.diamo.vratnice.entity;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "spolecnost", schema = Constants.SCHEMA)
@NamedQuery(name = "Spolecnost.findAll", query = "SELECT s from Spolecnost s")
public class Spolecnost implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.vratnice.base.VratniceIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name = "id_spolecnost")
    private String idSpolecnost;

    private String nazev;

    public Spolecnost(String idSpolecnost) {
        setIdSpolecnost(idSpolecnost);
    }
}
