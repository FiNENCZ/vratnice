package cz.diamo.share.dto.opravneni;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cz.diamo.share.constants.Constants;
import cz.diamo.share.enums.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilterOpravneniDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idVedouci;

    private List<RoleEnum> role;

    public FilterOpravneniDto(String idVedouci, List<RoleEnum> proRole) {
        setIdVedouci(idVedouci);
        setRole(proRole);
    }

    public List<String> getRoleString() {
        List<String> roleString = new ArrayList<String>();
        for (RoleEnum role : getRole()) {
            roleString.add(role.toString());
        }
        return roleString;
    }

    private String getRoleIn() {
        String roleIn = "";

        for (RoleEnum role : getRole()) {
            if (!StringUtils.isBlank(roleIn))
                roleIn += ", ";
            roleIn += "'" + role.toString() + "'";
        }
        return roleIn;
    }

    public String getNativeWhere(String columnIdUzivatel) {
        StringBuilder where = new StringBuilder();
        where.append(" (");
        where.append(" exists");
        where.append(" (");
        where.append(" select");
        where.append(" tmp_opravneni_vse.id_uzivatel");
        where.append(" from");
        where.append(" " + Constants.SCHEMA  + ".tmp_opravneni_vse tmp_opravneni_vse ");
        where.append(" where");
        where.append(" tmp_opravneni_vse.id_uzivatel = :idVedouci");
        where.append(" and tmp_opravneni_vse.authority in (" + getRoleIn() + ")");
        where.append(" )");
        where.append(" or ");
        where.append(columnIdUzivatel);
        where.append(" in");
        where.append(" (");
        where.append(" select");
        where.append(" tmp_opravneni_vyber.id_uzivatel_podrizeny");
        where.append(" from");
        where.append(" " + Constants.SCHEMA  + ".tmp_opravneni_vyber tmp_opravneni_vyber");
        where.append(" where");
        where.append(" tmp_opravneni_vyber.id_uzivatel = :idVedouci");
        where.append(" and tmp_opravneni_vyber.authority in (" + getRoleIn() + ")");
        where.append(")");
        where.append(")");
        return where.toString();
    }

    public String getHqlWhere(String columnIdUzivatel) {
        StringBuilder where = new StringBuilder();

        where.append(" (");
        where.append(" exists");
        where.append(" (");
        where.append(" select");
        where.append(" tmpOpravneniVse.idUzivatel");
        where.append(" from");
        where.append(" TmpOpravneniVse tmpOpravneniVse");
        where.append(" where");
        where.append(" tmpOpravneniVse.idUzivatel = :idVedouci");
        where.append(" and tmpOpravneniVse.authority in (" + getRoleIn() + ")");
        where.append(" )or ");
        where.append(columnIdUzivatel);
        where.append(" in");
        where.append(" (");
        where.append(" select");
        where.append(" tmpOpravneniVyber.idUzivatelPodrizeny");
        where.append(" from");
        where.append(" TmpOpravneniVyber tmpOpravneniVyber");
        where.append(" where");
        where.append(" tmpOpravneniVyber.idUzivatel = :idVedouci");
        where.append(" and tmpOpravneniVyber.authority in (" + getRoleIn() + ")");
        where.append(" )");
        where.append(" )");
        return where.toString();
    }

}
