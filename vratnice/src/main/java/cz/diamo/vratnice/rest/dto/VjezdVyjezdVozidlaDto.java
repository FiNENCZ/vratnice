package cz.diamo.vratnice.rest.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VjezdVyjezdVozidlaDto {

    private Long id;

    private String rzVozidla;

    private Date casPrijezdu;

    private Boolean vjezd;

    /* 
    public VjezdVyjezdVozidlaDto(VjezdVyjezdVozidla vjezdVyjezdVozidla) {
        if (vjezdVyjezdVozidla == null) {
            return;
        }

        this.id = vjezdVyjezdVozidla.getIdVjezdVozidla();
        this.rzVozidla = vjezdVyjezdVozidla.getRzVozidla();
        this.casPrijezdu = vjezdVyjezdVozidla.getCasPrijezdu();
        this.vjezd = vjezdVyjezdVozidla.getVjezd();
    }

    public VjezdVyjezdVozidla toEntity() {
        VjezdVyjezdVozidla vjezdVyjezdVozidla = new VjezdVyjezdVozidla();

        vjezdVyjezdVozidla.setIdVjezdVozidla(this.id);
        vjezdVyjezdVozidla.setRzVozidla(this.rzVozidla);
        vjezdVyjezdVozidla.setCasPrijezdu(this.casPrijezdu);
        vjezdVyjezdVozidla.setVjezd(this.vjezd);

        return vjezdVyjezdVozidla;
    }*/
}
