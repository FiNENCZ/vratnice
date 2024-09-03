package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.ZadostStav;

public enum ZadostStavEnum {
    SCHVALENO(1), POZASTAVENO(2), UKONCENO(3);

    private Integer value;

    ZadostStavEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static ZadostStavEnum getZadostStavEnum(ZadostStav zadostStav) {
        return getZadostStavEnum(zadostStav.getIdZadostStav());
    }

    public static ZadostStavEnum getZadostStavEnum(int value) {
        switch (value) {
            case 1:
                return SCHVALENO;
            case 2:
                return POZASTAVENO;
            case 3:
                return UKONCENO;
            default:
                return SCHVALENO;
        }
    }
}