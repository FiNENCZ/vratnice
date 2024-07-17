package cz.diamo.share.enums;


public enum ShareTableEnum {
    Uzivatel("UZ"), ExterniUzivatel("EU"), Opravneni("OP"), PracovniPozice("PP"), Zavod("ZA"), 
    Zakazka("ZK"), KmenovaData("KD"), PracovniPozicePodrizene("PO"), UzivatelskeNastaveni("UN"), PracovniPoziceLog("PL"), 
    Klic("KL"), Lokalita("LK"), Budova("BK"), Poschodi("PK"), ZadostKlic("ZD"), HistorieVypujcek("HV"),
    SluzebniVozidlo("SV"), HistorieSluzebniVozidlo("HS"), Ridic("RD"), 
    PovoleniVjezduVozidla("PV"), VjezdVozidla("VI"), VyjezdVozidla("VO"), 
    NavstevaOsoba("NO"), NavstevniListek("NL"), NajemnikNavstevnickaKarta("NK"), JmenoKorektura("JK");

    private String prefix;

    ShareTableEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}