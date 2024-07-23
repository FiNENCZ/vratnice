package cz.diamo.share.enums;


public enum ShareTableEnum {
    Uzivatel("UZ"), ExterniUzivatel("EU"), Opravneni("OP"), PracovniPozice("PP"),
    Zavod("ZA"), Zakazka("ZK"), KmenovaData("KD"), PracovniPozicePodrizene("PO"), UzivatelskeNastaveni("UN"),
    PracovniPoziceLog("PL"), Budova("BK"), Lokalita("LK"), Poschodi("PK"), HistorieSluzebniVozidlo("HS"), 
    HistorieVypujcek("HV"), PovoleniVjezduVozidla("PV"), Ridic("RD"), SluzebniVozidlo("SV"), VjezdVozidla("VI"), 
    VyjezdVozidla("VO"), ZadostKlic("ZD"),
    JmenoKorektura("JK"), Klic("KL"), NajemnikNavstevnickaKarta("NK"), NavstevaOsoba("NO"), NavstevniListek("NL");

    private String prefix;

    ShareTableEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}