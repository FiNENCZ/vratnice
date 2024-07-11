package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.SluzebniVozidloFunkce;

public enum SluzebniVozidloFunkceEnum {
    SLUZEBNI_VOZIDLO_FUNKCE_REDITEL(1), SLUZEBNI_VOZIDLO_FUNKCE_NAMESTEK(2);

    //Funkce – pouze u kategorie vozidla manažerské – např. ředitel, náměstek


    private Integer value;

    SluzebniVozidloFunkceEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static SluzebniVozidloFunkceEnum getSluzebniVozidloFunkceEnum(SluzebniVozidloFunkce sluzebniVozidloFunkce) {
        return getSluzebniVozidloFunkceEnum(sluzebniVozidloFunkce.getIdSluzebniVozidloFunkce());
    }

    public static SluzebniVozidloFunkceEnum getSluzebniVozidloFunkceEnum(int value) {
        switch (value) {
            case 1:
                return SLUZEBNI_VOZIDLO_FUNKCE_REDITEL;
            case 2:
                return SLUZEBNI_VOZIDLO_FUNKCE_NAMESTEK;
            default:
                return SLUZEBNI_VOZIDLO_FUNKCE_REDITEL;
        }
    }
}
