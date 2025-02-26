package cz.dp.vratnice.dto;

import cz.dp.vratnice.rest.dto.KonfiguraceVratniceKameryNgDto;
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
