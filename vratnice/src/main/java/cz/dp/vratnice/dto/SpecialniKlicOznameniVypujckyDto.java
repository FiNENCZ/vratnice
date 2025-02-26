package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.dp.share.dto.UzivatelDto;
import cz.dp.share.entity.Uzivatel;
import cz.dp.vratnice.entity.Klic;
import cz.dp.vratnice.entity.SpecialniKlicOznameniVypujcky;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpecialniKlicOznameniVypujckyDto implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;

    @NotNull(message = "{specialni_klic_oznameni_vypujcky.klic.require}")
    private KlicDto klic;

    @NotNull(message = "{specialni_klic_oznameni_vypujcky.uzivatel.require}")
    private List<UzivatelDto> uzivatele;

    @NotNull(message = "{aktivita.require}")
    private Boolean aktivita = true;

    public SpecialniKlicOznameniVypujckyDto(SpecialniKlicOznameniVypujcky specialniKlicOznameniVypujcky) {
        if (specialniKlicOznameniVypujcky == null) {
            return;
        }

        this.id = specialniKlicOznameniVypujcky.getIdSpecialniKlicOznameniVypujcky();
        this.klic = new KlicDto(specialniKlicOznameniVypujcky.getKlic());

        List<UzivatelDto> uzivateleDtos = new ArrayList<>();
        if (specialniKlicOznameniVypujcky.getUzivatele() != null) {
            for (Uzivatel uzivatel: specialniKlicOznameniVypujcky.getUzivatele()) {
                uzivateleDtos.add(new UzivatelDto(uzivatel));
            }
        }
        this.uzivatele = uzivateleDtos;

        this.aktivita = specialniKlicOznameniVypujcky.getAktivita();
    }

    public SpecialniKlicOznameniVypujcky toEntity() {
        SpecialniKlicOznameniVypujcky specialniKlicOznameniVypujcky = new SpecialniKlicOznameniVypujcky();

        specialniKlicOznameniVypujcky.setIdSpecialniKlicOznameniVypujcky(this.id);
        specialniKlicOznameniVypujcky.setKlic(new Klic(getKlic().getId()));

        List<Uzivatel> uzivatele = new ArrayList<>();
        if (getUzivatele() != null) {
            for (UzivatelDto uzivatelDto: this.getUzivatele()) {
                uzivatele.add(new Uzivatel(uzivatelDto.getId()));
            }
        }
        specialniKlicOznameniVypujcky.setUzivatele(uzivatele);

        specialniKlicOznameniVypujcky.setAktivita(this.aktivita);

        return specialniKlicOznameniVypujcky;
    }

}
