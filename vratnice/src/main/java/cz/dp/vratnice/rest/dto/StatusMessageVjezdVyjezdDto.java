package cz.dp.vratnice.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatusMessageVjezdVyjezdDto {

    private String message;

    private String error;

    public StatusMessageVjezdVyjezdDto(String message, String error) {
        setMessage(message);
        setError(error);
    }

}
