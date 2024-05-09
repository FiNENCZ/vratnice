package cz.diamo.share.rest.dto;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.diamo.share.entity.PracovniPozice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Pracovní pozice
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class PracovniPoziceDohodaDto extends PracovniPoziceDto {

    @Size(max = 100, message = "{sap.id.max.100}")
    @NotBlank(message = "{sap.id.dohodar.require}")
    private String sapIdDohodar;

    @JsonIgnore
    public PracovniPozice getPracovniPozice() {
        PracovniPozice pracovniPozice = new PracovniPozice();
        pracovniPozice.setSapId(getSapIdPracovniPozice());
        pracovniPozice.setSapIdNadrizeny(getSapIdPracovniPoziceNadrizene());
        pracovniPozice.setZkratka(getZkratka());
        pracovniPozice.setNazev(getNazev());
        pracovniPozice.setPlatnostOd(getPlatnostOd());
        pracovniPozice.setPlatnostDo(getPlatnostDo());
        pracovniPozice.setSapIdDohodar(getSapIdDohodar());
        pracovniPozice.setDohoda(true);
        pracovniPozice.setCasAktualizace(Calendar.getInstance().getTime());
        return pracovniPozice;

    }

}
