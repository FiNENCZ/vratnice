package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.HistorieKlicAkce;

public enum HistorieKlicAkceEnum {
    HISTORIE_KLIC_AKCE_VYTVOREN(1), HISTORIE_KLIC_AKCE_UPRAVEN(2), HISTORIE_KLIC_AKCE_ODSTRANEN(3), 
    HISTORIE_KLIC_AKCE_BLOKOVAN(4), HISTORIE_KLIC_AKCE_OBNOVEN(5), HISTORIE_KLIC_AKCE_VYMENA(6);

    private Integer value;

    HistorieKlicAkceEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static HistorieKlicAkceEnum getHistorieKlicAkceEnum(HistorieKlicAkce historieKlicAkce) {
        return getHistorieKlicAkceEnum(historieKlicAkce.getIdHistorieKlicAkce());
    }

    public static HistorieKlicAkceEnum getHistorieKlicAkceEnum(int value) {
        switch (value) {
            case 1:
                return HISTORIE_KLIC_AKCE_VYTVOREN;
            case 2:
                return HISTORIE_KLIC_AKCE_UPRAVEN;
            case 3:
                return HISTORIE_KLIC_AKCE_ODSTRANEN;
            case 4:
                return HISTORIE_KLIC_AKCE_BLOKOVAN;
            case 5:
                return HISTORIE_KLIC_AKCE_OBNOVEN;
            case 6:
                return HISTORIE_KLIC_AKCE_VYMENA;
            default:
                return HISTORIE_KLIC_AKCE_VYTVOREN;
        }
    }

}
