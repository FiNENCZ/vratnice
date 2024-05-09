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
@IdClass(OpravneniRole.class)
@Table(name = "opravneni_role", schema = Constants.SCHEMA)
@NamedQuery(name = "OpravneniRole.findAll", query = "SELECT s FROM OpravneniRole s")
public class OpravneniRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_opravneni")
    private String idOpravneni;

    @Id
    private String authority;

    public OpravneniRole(Opravneni opravneni, Role role) {
        setIdOpravneni(opravneni.getIdOpravneni());
        setAuthority(role.getAuthority());
    }
}
