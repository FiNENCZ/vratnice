package cz.diamo.share.enums;

public enum ExterniRoleEnum {
    ROLE_PERSONALISTIKA;
    
    public static ExterniRoleEnum getExterniRoleEnum(String value) {
        switch (value) {
            case "ROLE_PERSONALISTIKA":
                return ExterniRoleEnum.ROLE_PERSONALISTIKA;
            default:
                return ExterniRoleEnum.ROLE_PERSONALISTIKA;
        }
    }
}