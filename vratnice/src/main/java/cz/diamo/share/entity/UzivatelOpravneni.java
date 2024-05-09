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
@IdClass(UzivatelOpravneni.class)
@Table(name = "uzivatel_opravneni", schema = Constants.SCHEMA)
@NamedQuery(name = "UzivatelOpravneni.findAll", query = "SELECT s FROM UzivatelOpravneni s")
public class UzivatelOpravneni implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_uzivatel")
    private String idUzivatel;

    @Id
    @Column(name = "id_opravneni")
    private String idOpravneni;

    public UzivatelOpravneni(Uzivatel uzivatel, Opravneni opravneni) {
        setIdUzivatel(uzivatel.getIdUzivatel());
        setIdOpravneni(opravneni.getIdOpravneni());
    }
}