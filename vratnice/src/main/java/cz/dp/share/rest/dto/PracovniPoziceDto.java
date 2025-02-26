package cz.dp.share.rest.dto;

import java.io.Serializable;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pracovní pozice
 */
@NoArgsConstructor
@Data
public class PracovniPoziceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 100, message = "{sap.id.max.100}")
    @NotBlank(message = "{sap.id.require}")
    private String sapIdPracovniPozice;

    @Size(max = 100, message = "{sap.id.max.100}")
    private String sapIdPracovniPoziceNadrizene;

    @Size(max = 100, message = "{zkratka.max.100}")
    @NotBlank(message = "{zkratka.require}")
    private String zkratka;

    @Size(max = 1000, message = "{nazev.max.1000}")
    @NotBlank(message = "{nazev.require}")
    private String nazev;

    private Date platnostOd;

    private Date platnostDo;

}
