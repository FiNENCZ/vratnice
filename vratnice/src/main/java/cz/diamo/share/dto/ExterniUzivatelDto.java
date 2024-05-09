package cz.diamo.share.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.ExterniRole;
import cz.diamo.share.entity.ExterniUzivatel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExterniUzivatelDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @Size(max = 1000, message = "{nazev.max.1000}")
    @NotBlank(message = "{nazev.require}")
    private String nazev;

    @Size(max = 100, message = "{username.max.100}")
    @NotBlank(message = "{username.require}")
    private String username;

    @Size(max = 100, message = "{password.max.100}")
    private String password;

    private List<ExterniRoleDto> role;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita;

    public ExterniUzivatelDto(ExterniUzivatel externiUzivatel) {
        if (externiUzivatel == null)
            return;
        setId(externiUzivatel.getIdExterniUzivatel());
        setNazev(externiUzivatel.getNazev());
        setUsername(externiUzivatel.getUsername());

        setAktivita(externiUzivatel.getAktivita());

        setRole(new ArrayList<ExterniRoleDto>());
        if (externiUzivatel.getRole() != null && externiUzivatel.getRole().size() > 0) {
            for (ExterniRole maincextauthorit : externiUzivatel.getRole()) {
                getRole().add(new ExterniRoleDto(maincextauthorit));
            }
        }

    }

    @JsonIgnore
    public ExterniUzivatel getExterniUzivatel(ExterniUzivatel externiUzivatel, boolean pouzeId) {
        if (externiUzivatel == null)
            externiUzivatel = new ExterniUzivatel();

        externiUzivatel.setIdExterniUzivatel(getId());
        if (!pouzeId) {
            externiUzivatel.setNazev(getNazev());
            externiUzivatel.setUsername(getUsername());
            if (!StringUtils.isBlank(getPassword()))
                externiUzivatel.setPassword(Utils.getBcrypt(getPassword()));

            externiUzivatel.setAktivita(getAktivita());
            externiUzivatel.setRole(new ArrayList<ExterniRole>());

            if (getRole() != null && getRole().size() > 0) {
                for (ExterniRoleDto roleDto : getRole()) {
                    externiUzivatel.getRole().add(roleDto.getExterniRole(null));
                }
            }
        }
        return externiUzivatel;

    }
}
