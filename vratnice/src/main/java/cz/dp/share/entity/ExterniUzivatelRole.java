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
@IdClass(ExterniUzivatelRole.class)
@Table(name = "externi_uzivatel_role", schema = Constants.SCHEMA)
@NamedQuery(name = "ExterniUzivatelRole.findAll", query = "SELECT s FROM ExterniUzivatelRole s")
public class ExterniUzivatelRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_externi_uzivatel")
    private String idExterniUzivatel;

    @Id
    private String authority;

    public ExterniUzivatelRole(ExterniUzivatel externiUzivatel, ExterniRole externiRole) {
        setIdExterniUzivatel(externiUzivatel.getIdExterniUzivatel());
        setAuthority(externiRole.getAuthority());
    }
}