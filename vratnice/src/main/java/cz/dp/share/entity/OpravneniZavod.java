package cz.dp.share.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
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
@IdClass(OpravneniZavod.class)
@Table(name = "opravneni_zavod", schema = Constants.SCHEMA)
@NamedQuery(name = "OpravneniZavod.findAll", query = "SELECT s FROM OpravneniZavod s")
public class OpravneniZavod implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_opravneni")
    private String idOpravneni;

    @Id
    @Column(name = "id_zavod")
    private String idZavod;

    public OpravneniZavod(Opravneni opravneni, Zavod zavod) {
        setIdOpravneni(opravneni.getIdOpravneni());
        setIdZavod(zavod.getIdZavod());
    }
}
