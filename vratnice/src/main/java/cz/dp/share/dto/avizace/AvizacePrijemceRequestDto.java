package cz.dp.share.dto.avizace;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AvizacePrijemceRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "{sap.id.prijemce.require}")
    @Size(max = 100, message = "{sap.id.max.100}")
    private String sapId;

    @Size(max = 2000, message = "{email.max.2000}")
    private String email;
}
