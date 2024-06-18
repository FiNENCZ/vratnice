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

    @NotBlank(message = "Building is required")
    @Size(max = 50, message = "Building cannot exceed 30 characters")
    private String jmeno;

    @NotBlank(message = "Building is required")
    @Size(max = 50, message = "Building cannot exceed 30 characters")
    private String prijmeni;

    @NotBlank(message = "Building is required")
    @Size(max = 30, message = "Building cannot exceed 30 characters")
    private String cisloOp;

    @Size(max = 120, message = "Building cannot exceed 30 characters")
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
