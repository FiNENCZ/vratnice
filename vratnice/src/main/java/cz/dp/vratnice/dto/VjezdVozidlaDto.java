package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import cz.dp.vratnice.entity.VjezdVozidla;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VjezdVozidlaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idVjezdVozidla;

    private VratniceDto vratnice;

    @Valid
    private RidicDto ridic;

    @NotBlank(message = "{vjezd_vozidla.rz_vozidla.require}")
    @Size(max = 30, message = "{vjezd_vozidla.tz_vozidla.max.30}")
    private String rzVozidla;

    @NotNull(message = "{vjezd_vozidla.typ_vozidla.require}")
    private VozidloTypDto typVozidla;

    private Integer opakovanyVjezd;

    @NotNull(message = "{vjezd_vozidla.cas_prijezdu.require}")
    private ZonedDateTime casPrijezdu;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public VjezdVozidlaDto(VjezdVozidla vjezdVozidla) {
        if (vjezdVozidla == null){
            return;
        }

        this.idVjezdVozidla = vjezdVozidla.getIdVjezdVozidla();

        if (vjezdVozidla.getVratnice() != null) {
            this.vratnice = new VratniceDto(vjezdVozidla.getVratnice());
        }

        this.ridic = new RidicDto(vjezdVozidla.getRidic());
        this.rzVozidla = vjezdVozidla.getRzVozidla();

        if (vjezdVozidla.getTypVozidla() != null)
            this.typVozidla =  new VozidloTypDto(vjezdVozidla.getTypVozidla());

        this.opakovanyVjezd = vjezdVozidla.getOpakovanyVjezd();
        this.casPrijezdu = vjezdVozidla.getCasPrijezdu();
        this.aktivita = vjezdVozidla.getAktivita();
    }

    public VjezdVozidla toEntity() {
        VjezdVozidla vjezdVozidla = new VjezdVozidla();

        vjezdVozidla.setIdVjezdVozidla(this.idVjezdVozidla);

        if (getVratnice() != null) {
            vjezdVozidla.setVratnice(this.vratnice.toEntity());
        }

        vjezdVozidla.setRidic(this.ridic != null ? this.ridic.toEntity() : null);
        vjezdVozidla.setRzVozidla(this.rzVozidla);

        if (getTypVozidla() != null)
            vjezdVozidla.setTypVozidla(getTypVozidla().toEntity());
            
        vjezdVozidla.setOpakovanyVjezd(this.opakovanyVjezd);
        vjezdVozidla.setCasPrijezdu(this.casPrijezdu);
        vjezdVozidla.setAktivita(this.aktivita);

        return vjezdVozidla;
    }

    @AssertTrue(message = "{vjezd_vozidla.typ_vozidla.require}")
    public boolean isVozidloTypEntered() {
        return typVozidla != null && typVozidla.getId() != null;
    }
}
