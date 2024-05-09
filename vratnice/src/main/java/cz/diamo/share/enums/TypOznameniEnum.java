package cz.diamo.share.enums;

public enum TypOznameniEnum {
    INFO(1), DULEZITE_INFO(2), VAROVANI(3), CHYBA(4);

    private Integer value;

    TypOznameniEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static TypOznameniEnum getAvizaceOznameniTypEnum(int value) {
        switch (value) {
            case 1:
                return INFO;
            case 2:
                return DULEZITE_INFO;
            case 3:
                return VAROVANI;
            case 4:
                return CHYBA;
            default:
                return INFO;
        }
    }

}