package cz.diamo.vratnice.dto;

import java.io.Serializable;

import cz.diamo.share.dto.BudovaDto;
import cz.diamo.share.entity.Budova;
import cz.diamo.vratnice.entity.Poschodi;
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
    
    @NotNull(message = "{poschodi.budova.require}")
    private BudovaDto budova;

    public PoschodiDto(Poschodi poschodi) {
        if (poschodi == null) {
            return;
        }

        this.id = poschodi.getIdPoschodi();
        this.nazev = poschodi.getNazev();
        this.budova = new BudovaDto(poschodi.getBudova());
    }

    public Poschodi toEntity() {
        Poschodi poschodi = new Poschodi();

        poschodi.setIdPoschodi(this.id);
        poschodi.setNazev(this.nazev);
        poschodi.setBudova(new Budova(getBudova().getId()));

        return poschodi;
    }

}
