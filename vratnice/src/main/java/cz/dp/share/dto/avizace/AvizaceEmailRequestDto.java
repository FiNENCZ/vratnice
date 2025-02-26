package cz.dp.share.dto.avizace;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvizaceEmailRequestDto implements Serializable {

    @NotBlank(message = "{predmet.require}")
    @Size(max = 2000, message = "{predmet.max.2000}")
    private String predmet;

    @NotBlank(message = "{telo.require}")
    private String telo;

    @Size(max = 2000, message = "{odesilatel.max.2000}")
    private String odesilatel;

}
