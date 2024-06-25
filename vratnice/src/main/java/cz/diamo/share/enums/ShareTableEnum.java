package cz.diamo.share.enums;


public enum ShareTableEnum {
    Uzivatel("UZ"), ExterniUzivatel("EU"), Opravneni("OP"), PracovniPozice("PP"),
    Zavod("ZA"), Zakazka("ZK"), KmenovaData("KD"), PracovniPozicePodrizene("PO"), UzivatelskeNastaveni("UN"),
    PracovniPoziceLog("PL"), Klic("KL"), ZadostKlic("ZD"), HistorieVypujcek("HV"), SluzebniVozidlo("SV"),
    HistorieSluzebniVozidlo("HS"), Ridic("RD"), PovoleniVjezduVozidla("PV"), VjezdVozidla("VI");
;
    private String prefix;

    ShareTableEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}