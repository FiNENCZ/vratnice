package cz.diamo.vratnice.enums;

public enum TableEnum {
    Vratnice("SU");

    private String prefix;

    TableEnum(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}