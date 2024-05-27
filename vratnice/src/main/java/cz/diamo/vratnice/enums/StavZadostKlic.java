package cz.diamo.vratnice.enums;

public enum StavZadostKlic {
    VYZADANO("vyžádáno"),
    SCHVALENO("schváleno");

    private final String value;

    StavZadostKlic(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
