package cz.diamo.share.dto.opravneni;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String nazev;

    public RoleDto(Role role) {
        if (role == null)
            return;
        setId(role.getAuthority());
        setNazev(role.getNazev());
    }

    @JsonIgnore
    public Role getRole(Role role) {
        if (role == null)
            role = new Role();
        role.setAuthority(getId());
        return role;
    }
}
