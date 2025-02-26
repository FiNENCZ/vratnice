package cz.dp.vratnice.dto;

import cz.dp.share.entity.Uzivatel;
import cz.dp.vratnice.edos.dto.SnimacAkceVratniceDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DochazkaDto {

    @NotNull(message = "{dochazka.uzivatel.require}")
    private Uzivatel uzivatel;

    @NotNull(message = "{dochazka.snimac_akce.require}")
    private SnimacAkceVratniceDto snimacAkce;

}
