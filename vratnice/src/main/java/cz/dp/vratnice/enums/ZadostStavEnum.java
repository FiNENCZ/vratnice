package cz.dp.vratnice.enums;

import cz.dp.vratnice.entity.ZadostStav;

public enum ZadostStavEnum {
    SCHVALENO(1), POZASTAVENO(2), UKONCENO(3), PRIPRAVENO(4), ZAMITNUTO(5);

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
            case 4:
                return PRIPRAVENO;
            case 5:
                return ZAMITNUTO;
            default:
                return SCHVALENO;
        }
    }
}