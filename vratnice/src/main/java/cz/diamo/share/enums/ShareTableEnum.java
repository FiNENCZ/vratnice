package cz.diamo.share.enums;

public enum ShareTableEnum {
    Uzivatel("UZ"), ExterniUzivatel("EU"), Opravneni("OP"), PracovniPozice("PP"),
    Zavod("ZA"), Zakazka("ZK"), KmenovaData("KD"), PracovniPozicePodrizene("PO"), UzivatelskeNastaveni("UN"),
    PracovniPoziceLog("PL"), Budova("BD"), Lokalita("LK");

    private String prefix;

    ShareTableEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}