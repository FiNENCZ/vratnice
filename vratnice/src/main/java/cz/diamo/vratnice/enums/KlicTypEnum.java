package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.KlicTyp;

public enum KlicTypEnum {
    KLIC_PRIDELENY(1), KLIC_ZASTUP(2), KLIC_ZALOZNI(3), KLIC_OSTRAHA(4), KLIC_TREZOR(5), KLIC_UKLID(6);

    private Integer value;

    KlicTypEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static KlicTypEnum getKlicTypEnum(KlicTyp klicTyp) {
        return getKlicTypEnum(klicTyp.getIdKlicTyp());
    }

    public static KlicTypEnum getKlicTypEnum(int value) {
        switch (value) {
            case 1:
                return KLIC_PRIDELENY;
            case 2:
                return KLIC_ZASTUP;
            case 3:
                return KLIC_ZALOZNI;
            case 4:
                return KLIC_OSTRAHA;
            case 5:
                return KLIC_TREZOR;
            case 6:
                return KLIC_UKLID;
            default:
                return KLIC_PRIDELENY;
        }
    }

}
