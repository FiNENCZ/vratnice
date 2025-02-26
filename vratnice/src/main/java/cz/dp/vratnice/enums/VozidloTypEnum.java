package cz.dp.vratnice.enums;

import cz.dp.vratnice.entity.VozidloTyp;

public enum VozidloTypEnum {
    VOZIDLO_OSOBNI(1), VOZIDLO_DODAVKA(2), VOZIDLO_NAKLADNI(3), VOZIDLO_SPECIALNI(4), VOZIDLO_IZS(5);

    private Integer value;

    VozidloTypEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static VozidloTypEnum getVozidloTypEnum(VozidloTyp vozidloTyp) {
        return getVozidloTypEnum(vozidloTyp.getIdVozidloTyp());
    }

    public static VozidloTypEnum getVozidloTypEnum(int value) {
        switch (value) {
            case 1:
                return VOZIDLO_OSOBNI;
            case 2:
                return VOZIDLO_DODAVKA;
            case 3:
                return VOZIDLO_NAKLADNI;
            case 4:
                return VOZIDLO_SPECIALNI;
            case 5:
                return VOZIDLO_IZS;
            default:
                return VOZIDLO_OSOBNI;
        }
    }

    //(osobní, dodávka, nákladní, speciální, IZS)
}
