package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.diamo.vratnice.entity.Budova;
import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.entity.Poschodi;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BudovaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank(message = "{budova.typ.require}")
    @Size(message = "{budova.nazev.max.80}")
    private String nazev;
    
    @NotNull(message = "Lokalita budovy musí být vyplněna")
    private LokalitaDto lokalita;
    
    /* 
    @NotNull(message = "Alespoň jedno poschodí budovy musí být zadáno")
    private List<PoschodiDto> poschodi;*/

    public BudovaDto(Budova budova) {
        if (budova == null) {
            return;
        }

        this.id = budova.getIdBudova();
        this.nazev = budova.getNazev();
        this.lokalita = new LokalitaDto(budova.getLokalita());

        /* 
        List<PoschodiDto> poschodiDtos = new ArrayList<>();
        if(budova.getPoschodi() != null) {
            for(Poschodi poschodi: budova.getPoschodi()){
                poschodiDtos.add(new PoschodiDto(poschodi));
            }
        }
        this.setPoschodi(poschodiDtos);*/
    }

    public Budova toEntity() {
        Budova budova = new Budova();

        budova.setIdBudova(this.id);
        budova.setNazev(this.nazev);
        budova.setLokalita(new Lokalita(getLokalita().getId()));

        /* 
        List<Poschodi> poschodi = new ArrayList<>();
        if (this.getPoschodi() != null) {
            for(PoschodiDto poschodiDto: this.getPoschodi()){
                poschodi.add(new Poschodi(poschodiDto.getId()));
            }
        }
        budova.setPoschodi(poschodi);*/

        return budova;
    }



}
