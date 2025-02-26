package cz.dp.share.edos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UzivatelPristupovaKartaEdosDto {

    private String sapId;

    private String primarniKartaRfid;

    private String sekundarniKartaRfid;
}
