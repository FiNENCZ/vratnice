package cz.diamo.vratnice.enums;

import cz.diamo.vratnice.entity.SluzebniVozidloKategorie;

public enum SluzebniVozidloKategorieEnum {
    SLUZEBNI_VOZIDLO_KATEGORIE_REFERENTSKE(1), SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE(2), SLUZEBNI_VOZIDLO_KATEGORIE_JINE(3);

    //(referentské, manažerské, jiné) 

    private Integer value;

    SluzebniVozidloKategorieEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static SluzebniVozidloKategorieEnum getSluzebniVozidloKategorieEnum(SluzebniVozidloKategorie sluzebniVozidloKategorie) {
        return getSluzebniVozidloKategorieEnum(sluzebniVozidloKategorie.getIdSluzebniVozidloKategorie());
    }

    public static SluzebniVozidloKategorieEnum getSluzebniVozidloKategorieEnum(int value) {
        switch (value) {
            case 1:
                return SLUZEBNI_VOZIDLO_KATEGORIE_REFERENTSKE;
            case 2:
                return SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE;
            case 3:
                return SLUZEBNI_VOZIDLO_KATEGORIE_JINE;
            default:
                return SLUZEBNI_VOZIDLO_KATEGORIE_REFERENTSKE;
        }
    }
}
