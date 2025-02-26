package cz.dp.share.dto.edos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LokalitaEdosDto {

    private String kod;

    private String nazev;

    private String zavodSapId;

    private String zavodNazev;
}
