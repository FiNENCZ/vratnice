package cz.diamo.vratnice.filter;

public class FilterPristupuVratnice {

    public static String filtrujDlePrirazeneVratnice(String parametrVratnice) {
        StringBuilder conditionQuery = new StringBuilder();
        conditionQuery.append("AND (")
            .append("EXISTS (")
                .append("SELECT 1 FROM UzivatelVsechnyVratnice uev ")
                .append("WHERE uev.idUzivatel = :idUzivatel ")
                .append("AND uev.aktivniVsechnyVratnice = true")
            .append(") ")
            .append("OR (")
                .append("EXISTS (")
                    .append("SELECT 1 FROM UzivatelVratnice uv ")
                    .append("WHERE uv.uzivatel.idUzivatel = :idUzivatel ")
                    .append("AND uv.nastavenaVratnice IS NOT NULL ")
                    .append("AND "+ parametrVratnice +" = uv.nastavenaVratnice.idVratnice")
                .append(") ")
                .append("AND NOT EXISTS (")
                    .append("SELECT 1 FROM UzivatelVsechnyVratnice uev2 ")
                    .append("WHERE uev2.idUzivatel = :idUzivatel ")
                    .append("AND uev2.aktivniVsechnyVratnice = true")
                .append(")")
            .append(")")
        .append(")");
        return conditionQuery.toString();
    }
}
