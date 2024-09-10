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
        where.append(" " + Constants.SCHEMA + ".tmp_opravneni_vse tmp_opravneni_vse ");
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
        where.append(" " + Constants.SCHEMA + ".tmp_opravneni_vyber tmp_opravneni_vyber");
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

    public String getNativeBudovaWhere(String columnIdBudova) {
        StringBuilder where = new StringBuilder();
        where.append("( ");
        // přístup ke všem
        where.append("exists ");
        where.append("( ");
        where.append("select uzivatel_opravneni.id_opravneni ");
        where.append("from " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni ");
        where.append("join " + Constants.SCHEMA
                + ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni and opravneni.aktivita = true) ");
        where.append("join " + Constants.SCHEMA
                + ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = opravneni.id_opravneni and opravneni_role.authority in (" + getRoleIn() + ")) ");
        where.append("where opravneni.id_opravneni_typ_pristupu_budova = 2 and uzivatel_opravneni.id_uzivatel = :idVedouci ");
        where.append(") ");
        // dle závodu
        where.append("or exists ");
        where.append(" (");
        where.append("select budova.id_budova ");
        where.append("from " + Constants.SCHEMA + ".uzivatel uzivatel ");
        where.append("join " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni ON (uzivatel_opravneni.id_uzivatel = uzivatel.id_uzivatel) ");
        where.append("join " + Constants.SCHEMA
                + ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni and opravneni.aktivita = true) ");
        where.append(
                "join " + Constants.SCHEMA + ".lokalita lokalita on (lokalita.id_zavod = uzivatel.id_zavod OR lokalita.id_zavod IN (SELECT uz.id_zavod FROM "
                        + Constants.SCHEMA + ".uzivatel_zavod uz WHERE uz.id_uzivatel = uzivatel.id_uzivatel)) ");
        where.append(
                "join " + Constants.SCHEMA + ".budova budova ON (budova.id_lokalita = lokalita.id_lokalita and budova.id_budova = " + columnIdBudova + ") ");
        where.append("join " + Constants.SCHEMA
                + ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = opravneni.id_opravneni and opravneni_role.authority in (" + getRoleIn() + ")) ");
        where.append("where opravneni.id_opravneni_typ_pristupu_budova = 4 and uzivatel.id_uzivatel = :idVedouci ");
        where.append(") ");
        // výběrem
        where.append("or exists ");
        where.append(" (");
        where.append("select budova.id_budova ");
        where.append("from " + Constants.SCHEMA + ".uzivatel uzivatel ");
        where.append("join " + Constants.SCHEMA + ".uzivatel_opravneni uzivatel_opravneni ON (uzivatel_opravneni.id_uzivatel = uzivatel.id_uzivatel) ");
        where.append("join " + Constants.SCHEMA
                + ".opravneni opravneni on (opravneni.id_opravneni = uzivatel_opravneni.id_opravneni and opravneni.aktivita = true) ");
        where.append("join " + Constants.SCHEMA + ".opravneni_budova opravneni_budova on (opravneni_budova.id_opravneni = opravneni.id_opravneni) ");
        where.append("join " + Constants.SCHEMA + ".budova budova ON (budova.id_budova = opravneni_budova.id_budova and budova.id_budova = " + columnIdBudova
                + ") ");
        where.append("join " + Constants.SCHEMA
                + ".opravneni_role opravneni_role on (opravneni_role.id_opravneni = opravneni_budova.id_opravneni and opravneni_role.authority in (" + getRoleIn()
                + ")) ");
        where.append("where opravneni.id_opravneni_typ_pristupu_budova = 3 and uzivatel.id_uzivatel = :idVedouci ");
        where.append(") ");
        where.append(") ");
        return where.toString();
    }

    public String getHqlBudovaWhere(String columnIdBudova) {
        StringBuilder where = new StringBuilder();

        where.append("( ");
        // přístup ke všem
        where.append("exists ");
        where.append("( ");
        where.append("select uzivatelOpravneni.idOpravneni ");
        where.append("from UzivatelOpravneni uzivatelOpravneni ");
        where.append("join uzivatelOpravneni.opravneni opravneni ");
        where.append("join Uzivatel uzivatel on uzivatel.idUzivatel = uzivatelOpravneni.idUzivatel ");
        where.append(
                "join OpravneniRole opravneniRole on opravneniRole.idOpravneni = opravneni.idOpravneni and opravneniRole.authority in (" + getRoleIn() + ") ");
        where.append("where opravneni.opravneniTypPristupuBudova.idOpravneniTypPristupuBudova = 2 and opravneni.aktivita = true ");
        where.append("and uzivatel.idUzivatel = :idVedouci ");
        where.append(") ");
        // dle závodu
        where.append("or exists ");
        where.append("( ");
        where.append("select uzivatelOpravneni.idOpravneni ");
        where.append("from UzivatelOpravneni uzivatelOpravneni ");
        where.append("join uzivatelOpravneni.opravneni opravneni ");
        where.append("join Uzivatel uzivatel on uzivatel.idUzivatel = uzivatelOpravneni.idUzivatel ");
        where.append(
                "join OpravneniRole opravneniRole on opravneniRole.idOpravneni = opravneni.idOpravneni and opravneniRole.authority in (" + getRoleIn() + ") ");
        where.append(
                "join Lokalita lokalita on lokalita.zavod.idZavod = uzivatel.zavod.idZavod or lokalita.zavod.idZavod in (select uz.idZavod from UzivatelZavod uz where uz.idUzivatel = uzivatel.idUzivatel) ");
        where.append("join Budova budova on budova.likalita.idLokalita = lokalita.idLokalita and budova.idBudova = " + columnIdBudova + " ");
        where.append("where opravneni.opravneniTypPristupuBudova.idOpravneniTypPristupuBudova = 4 and opravneni.aktivita = true ");
        where.append("and uzivatel.idUzivatel = :idVedouci ");
        where.append(") ");
        // výběrem
        where.append("or exists ");
        where.append("( ");
        where.append("select uzivatelOpravneni.idOpravneni ");
        where.append("from UzivatelOpravneni uzivatelOpravneni ");
        where.append("join uzivatelOpravneni.opravneni opravneni ");
        where.append("join Uzivatel uzivatel on uzivatel.idUzivatel = uzivatelOpravneni.idUzivatel ");
        where.append(
                "join OpravneniRole opravneniRole on opravneniRole.idOpravneni = opravneni.idOpravneni and opravneniRole.authority in (" + getRoleIn() + ") ");
        where.append("join OpravneniBudova opravneniBudova on opravneniBudova.idOpravneni = opravneni.idOpravneni and opravneniBudova.idBudova = "
                + columnIdBudova + " ");
        where.append("join Budova budova on budova.idBudova = opravneniBudova.idBudova ");
        where.append("where opravneni.opravneniTypPristupuBudova.idOpravneniTypPristupuBudova = 3 and opravneni.aktivita = true ");
        where.append("and uzivatel.idUzivatel = :idVedouci ");
        where.append(") ");
        where.append(") ");
        return where.toString();
    }

}
