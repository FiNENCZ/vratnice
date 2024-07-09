package cz.diamo.vratnice.dto;

import cz.diamo.vratnice.enums.RzDetectedMessageStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RzDetectedMessageDto {
    private String rzVozidla;
    private RzDetectedMessageStatusEnum status;
}
