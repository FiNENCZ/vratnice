package cz.diamo.vratnice.rest.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import cz.diamo.vratnice.dto.VratniceDto;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KonfiguraceVratniceKameryNgDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    
    @NotNull(message = "{konfigurace_vratnice_kamery_ng.vratnice.require}")
    private VratniceDto vratnice;

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

    public KonfiguraceVratniceKameryNgDto(KonfiguraceVratniceKameryDto konfiguraceVratniceKameryDto) {
        if (konfiguraceVratniceKameryDto == null) {
            return;
        }

        this.id = konfiguraceVratniceKameryDto.getId();
        this.vratniceApiUrl = konfiguraceVratniceKameryDto.getVratniceApiUrl();
        this.casOdeslaniNevyporadanychZaznamuVSekundach = konfiguraceVratniceKameryDto.getCasOdeslaniNevyporadanychZaznamuVSekundach();
        this.casInicializaceVSekundach = konfiguraceVratniceKameryDto.getCasInicializaceVSekundach();
    }

}
