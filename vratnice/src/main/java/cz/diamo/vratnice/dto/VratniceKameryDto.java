package cz.diamo.vratnice.dto;

import cz.diamo.vratnice.rest.dto.KonfiguraceVratniceKameryNgDto;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VratniceKameryDto {
    
    private InicializaceVratniceKameryDto inicializace;

    @Valid
    private KonfiguraceVratniceKameryNgDto konfigurace;

}
