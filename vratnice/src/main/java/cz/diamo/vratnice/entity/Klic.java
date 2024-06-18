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
@Table(name = "klic", schema= Constants.SCHEMA)
@NamedQuery(name = "Klic.findAll", query = "SELECT s FROM Klic s")
public class Klic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "cz.diamo.share.base.ShareIdentifierGenerator")
    @GeneratedValue(generator = "id")
    @Column(name="id_klic")
    private String idKey;

    @Column(name = "specialni")
    private boolean special;

    @Column(name = "nazev")
    private String name;

    @Column(name = "kod_cipu")
    private String chipCode;

    @Column(name = "lokalita")
    private String location;

    @Column(name = "budova")
    private String building;

    @Column(name = "poschodi")
    private Integer floor;

    @Column(name = "mistnost")
    private String room;

    @Column(name = "typ_klice")
    private String keyType;

    @Column(name = "status")
    private String state = "dostupný";

    private Boolean aktivita = true;

    public Klic(String idKey) {
        setIdKey(idKey);
    }
}
