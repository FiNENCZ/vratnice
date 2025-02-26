package cz.dp.vratnice.enums;

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
    public static StavZadostKlic convertToStavZadostKlic(String stavValue) {
        if ("vyžádáno".equals(stavValue)) {
            return StavZadostKlic.VYZADANO;
        } else if ("schváleno".equals(stavValue)) {
            return StavZadostKlic.SCHVALENO;
        } else {
            throw new IllegalArgumentException("Nepodporovaná hodnota stavu: " + stavValue);
        }
    }

}
