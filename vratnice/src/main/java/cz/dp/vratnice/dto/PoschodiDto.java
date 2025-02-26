package cz.dp.vratnice.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.dto.BudovaDto;
import cz.dp.share.entity.Budova;
import cz.dp.vratnice.entity.Poschodi;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PoschodiDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank(message = "{poschodi.nazev.require}")
    @Size(max = 80, message = "{poschodi.nazev.max.80}")
    private String nazev;
    
    private Boolean aktivita;

    @NotNull(message = "{poschodi.budova.require}")
    private BudovaDto budova;

    public PoschodiDto(Poschodi poschodi) {
        if (poschodi == null) {
            return;
        }

        setId(poschodi.getIdPoschodi());
        setNazev(poschodi.getNazev());
        setAktivita(poschodi.getAktivita());
        setBudova(new BudovaDto(poschodi.getBudova()));
    }

    @JsonIgnore
    public Poschodi getPoschodi(Poschodi poschodi, boolean pouzeId) {
        if (poschodi == null)
            poschodi = new Poschodi();

        poschodi.setIdPoschodi(this.id);

        if (!pouzeId) {
            poschodi.setNazev(this.nazev);
            poschodi.setAktivita(getAktivita());
            poschodi.setBudova(new Budova(getBudova().getId()));
        }

        return poschodi;
    }

}
