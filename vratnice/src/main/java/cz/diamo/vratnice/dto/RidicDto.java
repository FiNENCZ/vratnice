package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.Date;

import cz.diamo.vratnice.entity.Ridic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RidicDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idRidic;

    @NotBlank(message = "{ridic.jmeno.require}")
    @Size(max = 50, message = "{ridic.jmeno.max.50}")
    private String jmeno;

    @NotBlank(message = "{ridic.prijmeni.require}")
    @Size(max = 50, message = "{ridic.prijmeni.max.50}")
    private String prijmeni;

    @NotBlank(message = "{ridic.cisloOp.require}")
    @Size(max = 30, message = "{ridic.cisloOp.max.30}")
    private String cisloOp;

    @Size(max = 120, message = "{ridic.firma.max.120}")
    private String firma;

    private Date datumPouceni;


    public RidicDto(Ridic ridic) {
        if (ridic == null) {
            return;
        }

        this.idRidic = ridic.getIdRidic();
        this.jmeno = ridic.getJmeno();
        this.prijmeni = ridic.getPrijmeni();
        this.cisloOp = ridic.getCisloOp();
        this.firma = ridic.getFirma();
        this.datumPouceni = ridic.getDatumPouceni();
    }

    public Ridic toEntity() {
        Ridic ridic = new Ridic();
        
        ridic.setIdRidic(this.idRidic);
        ridic.setJmeno(this.jmeno);
        ridic.setPrijmeni(this.prijmeni);
        ridic.setCisloOp(this.cisloOp);
        ridic.setFirma(this.firma);
        ridic.setDatumPouceni(this.datumPouceni);

        return ridic;
    }

}
