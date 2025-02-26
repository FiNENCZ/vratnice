package cz.dp.vratnice.enums;

import cz.dp.vratnice.entity.HistorieVypujcekAkce;

public enum HistorieVypujcekAkceEnum {

    HISTORIE_VYPUJCEK_VYPUJCEN(1), HISTORIE_VYPUJCEK_VRACEN(2);

    private Integer value;

    HistorieVypujcekAkceEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static HistorieVypujcekAkceEnum getHistorieVypujcekAkceEnum(HistorieVypujcekAkce historieVypujcekAkce) {
        return getHistorieVypujcekAkceEnum(historieVypujcekAkce.getIdHistorieVypujcekAkce());
    }

    public static HistorieVypujcekAkceEnum getHistorieVypujcekAkceEnum(int value) {
        switch (value) {
            case 1:
                return HISTORIE_VYPUJCEK_VYPUJCEN;
            case 2:
                return HISTORIE_VYPUJCEK_VRACEN;
            default:
                return HISTORIE_VYPUJCEK_VYPUJCEN;
        }
    }
}
