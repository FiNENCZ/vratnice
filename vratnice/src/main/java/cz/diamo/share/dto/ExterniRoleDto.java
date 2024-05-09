package cz.diamo.share.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.entity.ExterniRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExterniRoleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String nazev;

    public ExterniRoleDto(ExterniRole externiRole) {
        if (externiRole == null)
            return;
        setId(externiRole.getAuthority());
        setNazev(externiRole.getNazev());
    }

    @JsonIgnore
    public ExterniRole getExterniRole(ExterniRole externiRole) {
        if (externiRole == null)
            externiRole = new ExterniRole();
        externiRole.setAuthority(getId());
        return externiRole;
    }
}
