package cz.diamo.vratnice.enums;

public enum TableEnum {
    Vratnice("SU"), Klic("KL"), HistorieKlic("HK"), Lokalita("LK"), Budova("BK"), Poschodi("PK"), ZadostKlic("ZD"),
    HistorieVypujcek("HV"), SluzebniVozidlo("SV"), HistorieSluzebniVozidlo("HS"), Ridic("RD"), 
    PovoleniVjezduVozidla("PV"), VjezdVozidla("VI"), VyjezdVozidla("VO"), 
    NavstevaOsoba("NO"), NavstevniListek("NL"), NajemnikNavstevnickaKarta("NK"), JmenoKorektura("JK"),
    InicializaceVratniceKamery("VK"), UzivatelVratnice("UV"), Spolecnost("SP");

    private String prefix;

    TableEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}