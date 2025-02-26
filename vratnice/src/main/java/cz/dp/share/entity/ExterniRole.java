package cz.dp.share.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.share.enums.ExterniRoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "externi_role", schema = Constants.SCHEMA)
@NamedQuery(name = "ExterniRole.findAll", query = "SELECT s FROM ExterniRole s")
public class ExterniRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String authority;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public ExterniRole(ExterniRoleEnum externiRoleEnum) {
        setAuthority(externiRoleEnum.toString());
    }

    public ExterniRoleEnum getExterniRoleEnum() {
        return ExterniRoleEnum.getExterniRoleEnum(getAuthority());
    }
}