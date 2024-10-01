package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.NavstevniListekStav;

public enum NavstevniListekStavEnum {
    PROBEHLA(1), NEPROBEHLA(2), KE_ZPRACOVANI(3);

    private Integer value;

    NavstevniListekStavEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static NavstevniListekStavEnum getNavstevniListekStavEnum(NavstevniListekStav NavstevniListekStav) {
        return getNavstevniListekStavEnum(NavstevniListekStav.getIdNavstevniListekStav());
    }

    public static NavstevniListekStavEnum getNavstevniListekStavEnum(int value) {
        switch (value) {
            case 1:
                return PROBEHLA;
            case 2:
                return NEPROBEHLA;
            default:
                return KE_ZPRACOVANI;
        }
    }

}
