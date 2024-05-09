package cz.diamo.share.dto.keycloak;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeyCloakPayloadDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // private Long exp;

    private String sapid;

    // private KeyCloakRealmAccessDto realm_access;

    private List<String> roles;

    @JsonIgnore
    private List<String> role;

    @JsonIgnore
    private List<String> moduly;

    // public Date getExpDate() {
    // return new Date(getExp() * 1000);
    // }

    private Date expDate;

    private Boolean expire = false;

    @SuppressWarnings("unchecked")
    public KeyCloakPayloadDto(Claims claims) {
        setExpDate(claims.getExpiration());
        setSapid(claims.get("sapid", String.class));
        setRoles(claims.get("roles", ArrayList.class));
    }

    // @JsonIgnore
    // public List<String> getRoles() {
    // if (getRealm_access() != null && getRealm_access().getRoles() != null
    // && getRealm_access().getRoles().size() > 0) {
    // List<String> roles = new ArrayList<String>();
    // for (String role : getRealm_access().getRoles()) {
    // if (role.toUpperCase().contains(Constants.SCHEMA.toUpperCase() + "_"))
    // roles.add(role.toUpperCase());
    // }

    // return roles;
    // } else
    // return null;
    // }

    // @JsonIgnore
    // public List<String> getModules() {
    // if (getRealm_access() != null && getRealm_access().getRoles() != null
    // && getRealm_access().getRoles().size() > 0) {
    // List<String> modules = new ArrayList<String>();
    // for (String role : getRealm_access().getRoles()) {
    // if (role.contains("_")) {
    // String[] array = role.split("_");
    // if (array.length > 0) {
    // if (!modules.contains(array[0]))
    // modules.add(array[0]);
    // }
    // }
    // }

    // return modules;
    // } else
    // return null;
    // }

}
