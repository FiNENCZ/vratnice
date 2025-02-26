package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import cz.dp.vratnice.entity.VyjezdVozidla;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VyjezdVozidlaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idVyjezdVozidla;

    private VratniceDto vratnice;

    @NotBlank(message = "{vyjezd_vozidla.rz_vozidla.require}")
    @Size(max = 30, message = "{vyjezd_vozidla.rz_vozidla.max.30}")
    private String rzVozidla;

    private Boolean naklad = false;

    private String cisloPruchodky;

    private Boolean opakovanyVjezd;

    @NotNull(message = "{vyjezd_vozidla.cas_odjezdu.require}")
    private ZonedDateTime casOdjezdu;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;


    public VyjezdVozidlaDto(VyjezdVozidla vyjezdVozidla) {
        if (vyjezdVozidla == null) {
            return;
        }

        this.idVyjezdVozidla = vyjezdVozidla.getIdVyjezdVozidla();

        if (vyjezdVozidla.getVratnice() != null) {
            this.vratnice = new VratniceDto(vyjezdVozidla.getVratnice());
        }

        this.rzVozidla = vyjezdVozidla.getRzVozidla();
        this.naklad = vyjezdVozidla.getNaklad();
        this.cisloPruchodky = vyjezdVozidla.getCisloPruchodky();
        this.opakovanyVjezd = vyjezdVozidla.getOpakovanyVjezd();
        this.casOdjezdu = vyjezdVozidla.getCasOdjezdu();
        this.aktivita = vyjezdVozidla.getAktivita();
    }

    public VyjezdVozidla toEntity() {
        VyjezdVozidla vyjezdVozidla = new VyjezdVozidla();

        vyjezdVozidla.setIdVyjezdVozidla(this.idVyjezdVozidla);

        if (getVratnice() != null) {
            vyjezdVozidla.setVratnice(this.vratnice.toEntity());
        }

        vyjezdVozidla.setRzVozidla(this.rzVozidla);
        vyjezdVozidla.setNaklad(this.naklad);
        vyjezdVozidla.setCisloPruchodky(this.cisloPruchodky);
        vyjezdVozidla.setOpakovanyVjezd(this.opakovanyVjezd);
        vyjezdVozidla.setCasOdjezdu(this.casOdjezdu);
        vyjezdVozidla.setAktivita(this.aktivita);

        return vyjezdVozidla;
    }
    
    @AssertTrue(message = "{vyjezd_vozidla.cislo_pruchody.require}")
    private boolean isCisloPruchodkyValid() {
        if (Boolean.TRUE.equals(naklad)) {
            return cisloPruchodky != null && !cisloPruchodky.trim().isEmpty();
        }
        return true;
    }


}
