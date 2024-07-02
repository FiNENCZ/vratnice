package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.NavstevniListekTyp;

public enum NavstevniListekTypEnum {
    NAVSTEVNI_LISTEK_ELEKTRONICKY(1), NAVSTEVNI_LISTEK_PAPIROVY(2);

    private Integer value;

    NavstevniListekTypEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static NavstevniListekTypEnum getNavstevniListekTypEnum(NavstevniListekTyp navstevniListekTyp) {
        return getNavstevniListekTypEnum(navstevniListekTyp.getIdNavstevniListekTyp());
    }

    public static NavstevniListekTypEnum getNavstevniListekTypEnum(int value) {
        switch (value) {
            case 1:
                return NAVSTEVNI_LISTEK_ELEKTRONICKY;
            case 2:
                return NAVSTEVNI_LISTEK_PAPIROVY;
            default:
                return NAVSTEVNI_LISTEK_PAPIROVY;
        }
    }

}
