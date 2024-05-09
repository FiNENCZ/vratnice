package cz.diamo.share.entity;

import java.io.Serializable;

import cz.diamo.share.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@IdClass(ZadostExterniZaznam.class)
@Table(name = "zadost_externi_zaznam", schema = Constants.SCHEMA)
@NamedQuery(name = "ZadostExterniZaznam.findAll", query = "SELECT s FROM ZadostExterniZaznam s")
public class ZadostExterniZaznam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_zadost_externi")
    private String idZadostExterni;

    @Id
    @Column(name = "id_zaznam")
    private String idZaznam;

    public ZadostExterniZaznam(ZadostExterni zadostExterni, String idZaznam) {
        setIdZadostExterni(zadostExterni.getIdZadostExterni());
        setIdZaznam(idZaznam);
    }
}