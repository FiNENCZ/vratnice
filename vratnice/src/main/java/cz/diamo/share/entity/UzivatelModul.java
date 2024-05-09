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
@IdClass(UzivatelModul.class)
@Table(name = "uzivatel_modul", schema = Constants.SCHEMA)
@NamedQuery(name = "UzivatelModul.findAll", query = "SELECT s FROM UzivatelModul s")
public class UzivatelModul implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Id
    private String modul;

    public UzivatelModul(Uzivatel uzivatel, String modul) {
        setIdUzivatel(uzivatel.getIdUzivatel());
        setModul(modul);
    }
}