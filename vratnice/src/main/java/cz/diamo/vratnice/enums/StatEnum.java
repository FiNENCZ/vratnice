package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.Stat;

public enum StatEnum {
    STAT_CESKA_REPUBLIKA(1), STAT_SLOVENSKO(2), STAT_POLSKO(3), STAT_NEMECKO(4), STAT_RAKOUSKO(5);

     private Integer value;

     StatEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static StatEnum getStatEnum(Stat stat) {
        return getStatEnum(stat.getIdStat());
    }

    public static StatEnum getStatEnum(int value) {
        switch (value) {
            case 1:
                return STAT_CESKA_REPUBLIKA;
            case 2:
                return STAT_SLOVENSKO;
            case 3:
                return STAT_POLSKO;
            case 4:
                return STAT_NEMECKO;
            case 5:
                return STAT_RAKOUSKO;
            default:
                return STAT_CESKA_REPUBLIKA;
        }
    }

}
