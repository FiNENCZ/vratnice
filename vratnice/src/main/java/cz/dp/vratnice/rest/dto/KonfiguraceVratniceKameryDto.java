package cz.dp.vratnice.rest.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KonfiguraceVratniceKameryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank(message = "{konfigurace_vratnice_kamery_ng.vratnice.require}")
    private String idVratnice;

    @NotBlank(message = "{konfigurace_vratnice_kamery_ng.vratnice_api_url.require}")
    @Size(max = 50, message = "{konfigurace_vratnice_kamery_ng.vratnice_api_url.max.50}")
    private String vratniceApiUrl;

    @NotNull(message = "{konfigurace_vratnice_kamery_ng.interval_odeslani_nevyporadanych_zaznamu.require}")
    @Digits(message = "{konfigurace_vratnice_kamery_ng.interval_odeslani_nevyporadanych_zaznamu.integer}", fraction = 0, integer = 10)
    private Integer casOdeslaniNevyporadanychZaznamuVSekundach;

    @NotNull(message = "{konfigurace_vratnice_kamery_ng.interval_inicializace.require}")
    @Digits(message = "{konfigurace_vratnice_kamery_ng.interval_inicializace.integer}", fraction = 0, integer = 10)
    private Integer casInicializaceVSekundach;

    private String poznamka;

    private Timestamp casZmn;

    private String zmenuProvedl;

    public KonfiguraceVratniceKameryDto(KonfiguraceVratniceKameryNgDto kofiguraceNg) {
        if (kofiguraceNg == null) {
            return;
        }

        this.id = 0;
        this.idVratnice = kofiguraceNg.getVratnice().getId();
        this.vratniceApiUrl = kofiguraceNg.getVratniceApiUrl();
        this.casOdeslaniNevyporadanychZaznamuVSekundach = kofiguraceNg.getCasOdeslaniNevyporadanychZaznamuVSekundach();
        this.casInicializaceVSekundach = kofiguraceNg.getCasInicializaceVSekundach();
    }

}
