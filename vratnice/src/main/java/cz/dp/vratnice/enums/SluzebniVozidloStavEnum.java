package cz.dp.vratnice.enums;

import cz.dp.vratnice.entity.SluzebniVozidloStav;

public enum SluzebniVozidloStavEnum {
   SLUZEBNI_VOZIDLO_STAV_AKTIVNI(1), SLUZEBNI_VOZIDLO_STAV_BLOKOVANE(2);

   private Integer value;

   SluzebniVozidloStavEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static SluzebniVozidloStavEnum getSluzebniVozidloStavEnum(SluzebniVozidloStav sluzebniVozidloStav) {
        return getSluzebniVozidloStavEnum(sluzebniVozidloStav.getIdSluzebniVozidloStav());
    }

    public static SluzebniVozidloStavEnum getSluzebniVozidloStavEnum(int value) {
        switch (value) {
            case 1:
                return SLUZEBNI_VOZIDLO_STAV_AKTIVNI;
            case 2:
                return SLUZEBNI_VOZIDLO_STAV_BLOKOVANE;
            default:
                return SLUZEBNI_VOZIDLO_STAV_AKTIVNI;
        }
    }
}
