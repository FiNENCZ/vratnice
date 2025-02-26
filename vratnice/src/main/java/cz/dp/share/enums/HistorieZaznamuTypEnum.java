package cz.dp.share.enums;

public enum HistorieZaznamuTypEnum {
    TYP_HISTORIE_ZAZNAMU_NOVY(0), TYP_HISTORIE_ZAZNAMU_ZMENA(1), TYP_HISTORIE_ZAZNAMU_SMAZANI(2);

    private Integer value;

    HistorieZaznamuTypEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static HistorieZaznamuTypEnum getHistorieZaznamuTypEnum(int value) {
        switch (value) {
            case 1:
                return HistorieZaznamuTypEnum.TYP_HISTORIE_ZAZNAMU_NOVY;
            case 2:
                return HistorieZaznamuTypEnum.TYP_HISTORIE_ZAZNAMU_ZMENA;
            case 3:
                return HistorieZaznamuTypEnum.TYP_HISTORIE_ZAZNAMU_SMAZANI;
            default:
                return HistorieZaznamuTypEnum.TYP_HISTORIE_ZAZNAMU_NOVY;
        }
    }
}