package cz.dp.share.dto.avizace;

import java.io.Serializable;

import cz.dp.share.enums.TypOznameniEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvizaceOznameniRequestDto implements Serializable {

    @NotNull(message = "{typ.avizace.oznameni.require}")
    private TypOznameniEnum typ;

    @NotBlank(message = "{nadpis.require}")
    @Size(max = 2000, message = "{nadpis.max.2000}")
    private String nadpis;

    @NotBlank(message = "{text.require}")
    private String text;

    @Size(max = 2000, message = "{url.max.2000}")
    private String url;

}
