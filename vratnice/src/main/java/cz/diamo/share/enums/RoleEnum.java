package cz.diamo.share.enums;

public enum RoleEnum {

    ROLE_ZAMESTNANEC, ROLE_VEDOUCI_ZAMESTNANEC, ROLE_SPRAVA_VOZIDLA, ROLE_SPRAVA_VOZIDEL, ROLE_SPRAVA_VSECH_VOZIDEL,
    ROLE_SPRAVA_OPRAVNENI,
    ROLE_SPRAVA_EXTERNICH_UZIVATELU, ROLE_SPRAVA_ZAVODU, ROLE_SPRAVA_UZIVATELU, ROLE_CISELNIKY_ZAKAZKY,
    ROLE_SPRAVA_RIDICU, ROLE_ZOBRAZIT_ZADOST_VSE,
    ROLE_SPRAVA_KC_UZIVATELE, ROLE_SPRAVA_ZASTUPU, ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE, ROLE_MENU_VEDOUCI,
    ROLE_PRESCAS_SCHVALENI, ROLE_POVOLENI_TPC,
    ROLE_PREHLED_ZADOSTI, ROLE_SPRAVA_CISLENIK_STRAVNE, ROLE_SCHVALENI_FINANCOVANI, ROLE_SCHVALENI_UCETNICTVI,
    ROLE_TESTER, ROLE_KALENDAR_RIDICU,
    ROLE_RPD_ZADOST, ROLE_TPC_ZASTUP, ROLE_VOZIDLO_ZASTUP, ROLE_RPD_ZASTUP, ROLE_ORGANIZACNI_STRUKTURA,
    ROLE_SERVIS_ORG_STR, ROLE_RPD_REDITEL_ZADOST,
    ROLE_HDD_SPRAVCE, ROLE_HDD_ZAMESTNANEC, ROLE_SPRAVA_CISELNIKU, ROLE_SPRAVA_JIDELNY, ROLE_SPRAVA_CISELNIKY,
    ROLE_SPRAVA_JIDLA, ROLE_SPRAVA_SABLON_JIDELNICKU, ROLE_SPRAVA_OBJEDNAVKY, ROLE_SPRAVA_KLICU,
    ROLE_SPRAVA_ZADOSTI_KLICU, ROLE_SPRAVA_VYPUJCEK_KLICU, ROLE_ZADATEL_KLICE, ROLE_SPRAVA_OBJEDNAVKY_KOREKCE,
    ROLE_OBJEDNAVKY_ZASTUP, ROLE_SPRAVA_UZIVATELU_EXT,
    ROLE_SPRAVA_LOKALIT, ROLE_SPRAVA_BUDOV;

    public static RoleEnum getRoleEnum(String value) {
        switch (value) {
            case "ROLE_ZAMESTNANEC":
                return RoleEnum.ROLE_ZAMESTNANEC;
            case "ROLE_SPRAVA_VOZIDLA":
                return RoleEnum.ROLE_SPRAVA_VOZIDLA;
            case "ROLE_SPRAVA_VOZIDEL":
                return RoleEnum.ROLE_SPRAVA_VOZIDEL;
            case "ROLE_SPRAVA_VSECH_VOZIDEL":
                return RoleEnum.ROLE_SPRAVA_VSECH_VOZIDEL;
            case "ROLE_SPRAVA_OPRAVNENI":
                return RoleEnum.ROLE_SPRAVA_OPRAVNENI;
            case "ROLE_SPRAVA_EXTERNICH_UZIVATELU":
                return RoleEnum.ROLE_SPRAVA_EXTERNICH_UZIVATELU;
            case "ROLE_SPRAVA_ZAVODU":
                return RoleEnum.ROLE_SPRAVA_ZAVODU;
            case "ROLE_VEDOUCI_ZAMESTNANEC":
                return RoleEnum.ROLE_VEDOUCI_ZAMESTNANEC;
            case "ROLE_CISELNIKY_ZAKAZKY":
                return RoleEnum.ROLE_CISELNIKY_ZAKAZKY;
            case "ROLE_SPRAVA_UZIVATELU":
                return RoleEnum.ROLE_SPRAVA_UZIVATELU;
            case "ROLE_SPRAVA_RIDICU":
                return RoleEnum.ROLE_SPRAVA_RIDICU;
            case "ROLE_ZOBRAZIT_ZADOST_VSE":
                return RoleEnum.ROLE_ZOBRAZIT_ZADOST_VSE;
            case "ROLE_SPRAVA_KC_UZIVATELE":
                return RoleEnum.ROLE_SPRAVA_KC_UZIVATELE;
            case "ROLE_SPRAVA_ZASTUPU":
                return RoleEnum.ROLE_SPRAVA_ZASTUPU;
            case "ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE":
                return RoleEnum.ROLE_SPRAVA_ZASTUPU_SYNCHRONIZACE;
            case "ROLE_MENU_VEDOUCI":
                return RoleEnum.ROLE_MENU_VEDOUCI;
            case "ROLE_PRESCAS_SCHVALENI":
                return RoleEnum.ROLE_PRESCAS_SCHVALENI;
            case "ROLE_POVOLENI_TPC":
                return RoleEnum.ROLE_POVOLENI_TPC;
            case "ROLE_PREHLED_ZADOSTI":
                return RoleEnum.ROLE_PREHLED_ZADOSTI;
            case "ROLE_SPRAVA_CISLENIK_STRAVNE":
                return RoleEnum.ROLE_SPRAVA_CISLENIK_STRAVNE;
            case "ROLE_SCHVALENI_FINANCOVANI":
                return RoleEnum.ROLE_SCHVALENI_FINANCOVANI;
            case "ROLE_SCHVALENI_UCETNICTVI":
                return RoleEnum.ROLE_SCHVALENI_UCETNICTVI;
            case "ROLE_TESTER":
                return RoleEnum.ROLE_TESTER;
            case "ROLE_KALENDAR_RIDICU":
                return RoleEnum.ROLE_KALENDAR_RIDICU;
            case "ROLE_RPD_ZADOST":
                return RoleEnum.ROLE_RPD_ZADOST;
            case "ROLE_TPC_ZASTUP":
                return RoleEnum.ROLE_TPC_ZASTUP;
            case "ROLE_VOZIDLO_ZASTUP":
                return RoleEnum.ROLE_VOZIDLO_ZASTUP;
            case "ROLE_RPD_ZASTUP":
                return RoleEnum.ROLE_RPD_ZASTUP;
            case "ROLE_ORGANIZACNI_STRUKTURA":
                return RoleEnum.ROLE_ORGANIZACNI_STRUKTURA;
            case "ROLE_SERVIS_ORG_STR":
                return RoleEnum.ROLE_SERVIS_ORG_STR;
            case "ROLE_RPD_REDITEL_ZADOST":
                return RoleEnum.ROLE_RPD_REDITEL_ZADOST;
            case "ROLE_HDD_SPRAVCE":
                return RoleEnum.ROLE_HDD_SPRAVCE;
            case "ROLE_HDD_ZAMESTNANEC":
                return RoleEnum.ROLE_HDD_ZAMESTNANEC;
            case "ROLE_SPRAVA_CISELNIKU":
                return RoleEnum.ROLE_SPRAVA_CISELNIKU;
            case "ROLE_SPRAVA_JIDELNY":
                return RoleEnum.ROLE_SPRAVA_JIDELNY;
            case "ROLE_SPRAVA_CISELNIKY":
                return RoleEnum.ROLE_SPRAVA_CISELNIKY;
            case "ROLE_SPRAVA_JIDLA":
                return RoleEnum.ROLE_SPRAVA_JIDLA;
            case "ROLE_SPRAVA_SABLON_JIDELNICKU":
                return RoleEnum.ROLE_SPRAVA_SABLON_JIDELNICKU;
            case "ROLE_SPRAVA_OBJEDNAVKY":
                return RoleEnum.ROLE_SPRAVA_OBJEDNAVKY;
            case "ROLE_SPRAVA_KLICU":
                return RoleEnum.ROLE_SPRAVA_KLICU;
            case "ROLE_SPRAVA_ZADOSTI_KLICU":
                return RoleEnum.ROLE_SPRAVA_ZADOSTI_KLICU;
            case "ROLE_SPRAVA_VYPUJCEK_KLICU":
                return RoleEnum.ROLE_SPRAVA_VYPUJCEK_KLICU;
            case "ROLE_ZADATEL_KLICE":
                return RoleEnum.ROLE_ZADATEL_KLICE;
            case "ROLE_SPRAVA_LOKALIT":
                return RoleEnum.ROLE_SPRAVA_LOKALIT;
            case "ROLE_SPRAVA_BUDOV":
                return RoleEnum.ROLE_SPRAVA_BUDOV;
            case "ROLE_SPRAVA_OBJEDNAVKY_KOREKCE":
                return ROLE_SPRAVA_OBJEDNAVKY_KOREKCE;
            case "ROLE_OBJEDNAVKY_ZASTUP":
                return ROLE_OBJEDNAVKY_ZASTUP;
            case "ROLE_SPRAVA_UZIVATELU_EXT":
                return ROLE_SPRAVA_UZIVATELU_EXT;
            default:
                return ROLE_ZAMESTNANEC;
        }
    }
}