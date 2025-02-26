package cz.dp.share.entity;

import java.io.Serializable;

import cz.dp.share.constants.Constants;
import cz.dp.share.enums.RoleEnum;
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
@Table(name = "role", schema = Constants.SCHEMA)
@NamedQuery(name = "Role.findAll", query = "SELECT s FROM Role s")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String authority;

    @Column(name = "nazev_resx")
    private String nazevResx;

    @Transient
    private String nazev;

    public Role(RoleEnum roleEnum) {
        setAuthority(roleEnum.toString());
    }

    public RoleEnum getRoleEnum() {
        return RoleEnum.getRoleEnum(getAuthority());
    }
}