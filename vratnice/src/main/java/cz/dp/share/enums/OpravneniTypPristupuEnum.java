package cz.dp.share.enums;

public enum OpravneniTypPristupuEnum {
    TYP_PRIST_OPR_BEZ_PRISTUPU(1), TYP_PRIST_OPR_VSE(2), TYP_PRIST_OPR_VYBER(3), TYP_PRIST_OPR_PRIMI_PODRIZENI(4),
    TYP_PRIST_OPR_VSICHNI_PODRIZENI(5);

    private Integer value;

    OpravneniTypPristupuEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static OpravneniTypPristupuEnum getOpravneniTypPristupuEnum(int value) {
        switch (value) {
            case 1:
                return OpravneniTypPristupuEnum.TYP_PRIST_OPR_BEZ_PRISTUPU;
            case 2:
                return OpravneniTypPristupuEnum.TYP_PRIST_OPR_VSE;
            case 3:
                return OpravneniTypPristupuEnum.TYP_PRIST_OPR_VYBER;
            case 4:
                return OpravneniTypPristupuEnum.TYP_PRIST_OPR_PRIMI_PODRIZENI;
            case 5:
                return OpravneniTypPristupuEnum.TYP_PRIST_OPR_VSICHNI_PODRIZENI;
            default:
                return OpravneniTypPristupuEnum.TYP_PRIST_OPR_BEZ_PRISTUPU;
        }
    }
}