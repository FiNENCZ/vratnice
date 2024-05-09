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
@IdClass(OpravneniPracovniPozice.class)
@Table(name = "opravneni_pracovni_pozice", schema = Constants.SCHEMA)
@NamedQuery(name = "OpravneniPracovniPozice.findAll", query = "SELECT s FROM OpravneniPracovniPozice s")
public class OpravneniPracovniPozice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_opravneni")
    private String idOpravneni;

    @Id
    @Column(name = "id_pracovni_pozice")
    private String idPracovniPozice;

    public OpravneniPracovniPozice(Opravneni opravneni, PracovniPozice pracovniPozice) {
        setIdOpravneni(opravneni.getIdOpravneni());
        setIdPracovniPozice(pracovniPozice.getIdPracovniPozice());
    }

}