package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.KlicTyp;

public enum HistorieSluzebniVozidloAkceEnum {
    HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO(1), HISTORIE_SLUZEBNI_VOZIDLO_AKCE_UPRAVENO(2), HISTORIE_SLUZEBNI_VOZIDLO_AKCE_ODSTRANENO(3), 
    HISTORIE_SLUZEBNI_VOZIDLO_AKCE_BLOKOVANO(4), HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO(5);

        private Integer value;

    HistorieSluzebniVozidloAkceEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static HistorieSluzebniVozidloAkceEnum getHistorieSluzebniVozidloAkceEnum(KlicTyp klicTyp) {
        return getHistorieSluzebniVozidloAkceEnum(klicTyp.getIdKlicTyp());
    }

    public static HistorieSluzebniVozidloAkceEnum getHistorieSluzebniVozidloAkceEnum(int value) {
        switch (value) {
            case 1:
                return HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO;
            case 2:
                return HISTORIE_SLUZEBNI_VOZIDLO_AKCE_UPRAVENO;
            case 3:
                return HISTORIE_SLUZEBNI_VOZIDLO_AKCE_ODSTRANENO;
            case 4:
                return HISTORIE_SLUZEBNI_VOZIDLO_AKCE_BLOKOVANO;
            case 5:
                return HISTORIE_SLUZEBNI_VOZIDLO_AKCE_OBNOVENO;
            default:
                return HISTORIE_SLUZEBNI_VOZIDLO_AKCE_VYTVORENO;
        }
    }
}
