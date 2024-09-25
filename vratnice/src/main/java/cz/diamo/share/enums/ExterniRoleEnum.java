package cz.diamo.share.enums;

public enum ExterniRoleEnum {
    ROLE_PERSONALISTIKA, ROLE_VRATNICE_PUBLIC, ROLE_VRATNICE_KAMERY;
    
    public static ExterniRoleEnum getExterniRoleEnum(String value) {
        switch (value) {
            case "ROLE_PERSONALISTIKA":
                return ExterniRoleEnum.ROLE_PERSONALISTIKA;
            case "ROLE_VRATNICE_PUBLIC":
                return ExterniRoleEnum.ROLE_VRATNICE_PUBLIC;
            case "ROLE_VRATNICE_KAMERY":
                return ExterniRoleEnum.ROLE_VRATNICE_KAMERY;
            default:
                return ExterniRoleEnum.ROLE_PERSONALISTIKA;
        }
    }
}