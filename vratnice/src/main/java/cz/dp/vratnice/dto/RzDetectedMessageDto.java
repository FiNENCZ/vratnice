package cz.dp.vratnice.dto;

import cz.dp.vratnice.enums.RzDetectedMessageStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RzDetectedMessageDto {
    private String idVratnice;
    private String rzVozidla;
    private RzDetectedMessageStatusEnum status;
    private Boolean isVjezd;
}
