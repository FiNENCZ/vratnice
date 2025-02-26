package cz.dp.share.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.entity.KmenovaData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KmenovaDataDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 100, message = "{sap.id.max.100}")
    @NotBlank(message = "{sap.id.require}")
    private String sapIdZamestnance;

    @NotNull(message = "{datum.predani.informace.require}")
    private Date datumPredaniInformace;

    @Size(max = 20, message = "{cislo.znamky.max.20}")
    private String cisloZnamky;

    @Size(max = 11, message = "{rodne.cislo.max.11}")
    // @NotBlank(message = "{rodne.cislo.require}")
    private String rodneCislo;

    @Size(max = 100, message = "{prijmeni.max.100}")
    @NotBlank(message = "{prijmeni.require}")
    private String prijmeni;

    @Size(max = 100, message = "{jmeno.max.100}")
    @NotBlank(message = "{jmeno.require}")
    private String jmeno;

    @Size(max = 100, message = "{titul.pred.max.100}")
    private String titulPred;

    @Size(max = 100, message = "{titul.za.max.100}")
    private String titulZa;

    private String ulice;

    private String psc;

    private String obec;

    private String cisloPopisne;

    private String tel;

    private String email;

    private String soukromyEmail;

    private Date datumUkonceniPracovnihoPomeru;

    // @NotBlank(message = "{druh.vyneti.require}")
    private String sapIdDruhVyneti;

    // @NotBlank(message = "{druh.pracovniho.pomeru.require}")
    private String sapIdDruhPracovnihoPomeru;

    @NotNull(message = "{denni.uvazek.require}")
    private BigDecimal denniUvazek;

    @Size(max = 100, message = "{sap.id.kategorie.zamestnance.max.100}")
    // @NotBlank(message = "{sap.id.kategorie.zamestnance.require}")
    private String sapIdKategorieZamestnance;

    @Size(max = 100, message = "{sap.id.kalendare.max.100}")
    // @NotBlank(message = "{sap.id.kalendare.require}")
    private String sapIdKalendare;

    // @NotNull(message = "{forma.mzdy.require}")
    private String sapIdFormaMzdy;

    // @NotNull(message = "{skupina.zamestnance.require}")
    private String sapIdSkupinaZamestnance;

    @NotNull(message = "{pruzna.pracovni.doba.require}")
    private Boolean pruznaPracovniDoba = false;

    @Size(max = 100, message = "{sap.id.kmenove.max.100}")
    // @NotBlank(message = "{sap.id.kmenove.zakazky.require}")
    private String sapIdKmenoveZakazky;

    @NotNull(message = "{narok.na.dovolenou.require}")
    private BigDecimal narokNaDovolenou;

    private BigDecimal zbyvajiciDovlenaZMinulehoRoku;

    private BigDecimal dodatkovaDovolena;

    private BigDecimal cerpaniDovolene;

    @NotNull(message = "{prumer.pro.nahrady.require}")
    private BigDecimal prumerProNahrady;

    private String sapIdPersonalniOblasti;

    private String sapIdDilciPersonalniOblasti;

    private String sapIdZauctovaciOkruh;

    private String sapIdOrganizacniJednotka;

    private String sapIdPlanovaneMisto;

    @Size(max = 100, message = "{cip.max.100}")
    private String cip1;

    @Size(max = 100, message = "{cip.max.100}")
    private String cip2;

    @JsonIgnore
    public KmenovaData getKmenovaData() {

        KmenovaData kmenovaData = new KmenovaData();

        kmenovaData.setSapId(getSapIdZamestnance());
        kmenovaData.setDruhPracPomeruSapId(getSapIdDruhPracovnihoPomeru());
        kmenovaData.setDruhVynetiSapId(getSapIdDruhVyneti());
        kmenovaData.setFormaMzdySapId(getSapIdFormaMzdy());
        kmenovaData.setSkupinaZamestnanceSapId(getSapIdSkupinaZamestnance());
        kmenovaData.setPlatnostKeDni(getDatumPredaniInformace());
        kmenovaData.setCisloZnamky(getCisloZnamky());
        kmenovaData.setRodneCislo(getRodneCislo());
        kmenovaData.setPrijmeni(getPrijmeni());
        kmenovaData.setJmeno(getJmeno());
        kmenovaData.setTitulPred(getTitulPred());
        kmenovaData.setTitulZa(getTitulZa());
        kmenovaData.setUlice(getUlice());
        kmenovaData.setPsc(getPsc());
        kmenovaData.setObec(getObec());
        kmenovaData.setCisloPopisne(getCisloPopisne());
        kmenovaData.setTel(getTel());
        kmenovaData.setEmail(getEmail());
        kmenovaData.setSoukromyEmail(getSoukromyEmail());
        kmenovaData.setDatumUkonceniPracPomeru(getDatumUkonceniPracovnihoPomeru());
        kmenovaData.setDenniUvazek(getDenniUvazek());
        kmenovaData.setKategorieSapId(getSapIdKategorieZamestnance());
        kmenovaData.setKalendarSapId(getSapIdKalendare());
        kmenovaData.setZakazkaSapId(getSapIdKmenoveZakazky());
        kmenovaData.setNarokNaDovolenou(getNarokNaDovolenou());
        kmenovaData.setZbytekDovoleneMinulyRok(getZbyvajiciDovlenaZMinulehoRoku());
        kmenovaData.setDodatkovaDovolena(getDodatkovaDovolena());
        kmenovaData.setCerpaniDovolene(getCerpaniDovolene());
        kmenovaData.setPrumerProNahrady(getPrumerProNahrady());
        kmenovaData.setPersonalniOblastSapId(getSapIdPersonalniOblasti());
        kmenovaData.setDilciPersonalniOblastSapId(getSapIdDilciPersonalniOblasti());
        kmenovaData.setZauctovaciOkruhSapId(getSapIdZauctovaciOkruh());
        kmenovaData.setOrgnizacniJednotkaSapId(getSapIdOrganizacniJednotka());
        kmenovaData.setPlanovaneMistoSapId(getSapIdPlanovaneMisto());
        kmenovaData.setPruznaPracDoba(getPruznaPracovniDoba());
        kmenovaData.setZpracovano(false);
        kmenovaData.setCip1(getCip1());
        kmenovaData.setCip2(getCip2());

        // trimování dat
        kmenovaData.setTel(StringUtils.trim(kmenovaData.getTel()));
        kmenovaData.setEmail(StringUtils.trim(kmenovaData.getEmail()));
        kmenovaData.setSoukromyEmail(StringUtils.trim(kmenovaData.getSoukromyEmail()));
        kmenovaData.setCip1(StringUtils.trim(kmenovaData.getCip1()));
        kmenovaData.setCip2(StringUtils.trim(kmenovaData.getCip2()));

        return kmenovaData;
    }

}
