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
@IdClass(UzivatelZavod.class)
@Table(name = "uzivatel_zavod", schema = Constants.SCHEMA)
@NamedQuery(name = "UzivatelZavod.findAll", query = "SELECT s FROM UzivatelZavod s")
public class UzivatelZavod implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Id
    @Column(name = "id_zavod")
    private String idZavod;

    public UzivatelZavod(Uzivatel uzivatel, Zavod zavod) {
        setIdUzivatel(uzivatel.getIdUzivatel());
        setIdZavod(zavod.getIdZavod());
    }
}