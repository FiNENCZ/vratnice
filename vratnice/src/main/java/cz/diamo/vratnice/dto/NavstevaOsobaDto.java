package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.diamo.vratnice.entity.NavstevaOsoba;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NavstevaOsobaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idNavstevaOsoba;

    @NotBlank(message = "{navsteva_osoba.jmeno.require }")
    @Size(max = 50, message = "{navsteva_osoba.jmeno.max.50}")
    private String jmeno;

    @NotBlank(message = "{navsteva_osoba.prijmeni.require}")
    @Size(max = 50, message = "{navsteva_osoba.prijmeni.max.50}")
    private String prijmeni;

    @NotBlank(message = "{navsteva_osoba.cislo_op.require}")
    @Size(max = 9, message = "{navsteva_osoba.cislo_op.max.9}")
    @Pattern(regexp = "\\d+", message = "{navsteva_osoba.cislo_op.pattern}")
    private String cisloOp;

    @Size(max = 120, message = "{navsteva_osoba.firma.max.120}")
    private String firma;

    private Date datumPouceni;

    public NavstevaOsobaDto(NavstevaOsoba navstevaOsoba) {
        if (navstevaOsoba == null) {
            return;
        }

        this.idNavstevaOsoba = navstevaOsoba.getIdNavstevaOsoba();
        this.jmeno = navstevaOsoba.getJmeno();
        this.prijmeni = navstevaOsoba.getPrijmeni();
        this.cisloOp = navstevaOsoba.getCisloOp();
        this.firma = navstevaOsoba.getFirma();
        this.datumPouceni = navstevaOsoba.getDatumPouceni();
    }

    public NavstevaOsoba toEntity() {
        NavstevaOsoba navstevaOsoba = new NavstevaOsoba();

        navstevaOsoba.setIdNavstevaOsoba(this.idNavstevaOsoba);
        navstevaOsoba.setJmeno(this.jmeno);
        navstevaOsoba.setPrijmeni(this.prijmeni);
        navstevaOsoba.setCisloOp(this.cisloOp);
        navstevaOsoba.setFirma(this.firma);
        navstevaOsoba.setDatumPouceni(this.datumPouceni);

        return navstevaOsoba;
    }


}
