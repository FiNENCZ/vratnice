package cz.dp.share.enums;

public enum OpravneniTypPristupuBudovaEnum {
    TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU(1), TYP_PRIST_BUDOVA_OPR_VSE(2), TYP_PRIST_BUDOVA_OPR_VYBER(3), TYP_PRIST_BUDOVA_OPR_ZAVOD(4);

    private Integer value;

    OpravneniTypPristupuBudovaEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static OpravneniTypPristupuBudovaEnum getOpravneniTypPristupuBudovaEnum(int value) {
        switch (value) {
        case 1:
            return OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU;
        case 2:
            return OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_VSE;
        case 3:
            return OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_VYBER;
        case 4:
            return OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_ZAVOD;
        default:
            return OpravneniTypPristupuBudovaEnum.TYP_PRIST_BUDOVA_OPR_BEZ_PRISTUPU;
        }
    }
}