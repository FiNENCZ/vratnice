package cz.diamo.share.dto.keycloak;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeyCloakRealmAccessDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> roles;

}
