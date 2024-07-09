package cz.diamo.vratnice.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import cz.diamo.vratnice.entity.Budova;
import cz.diamo.vratnice.entity.Lokalita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LokalitaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @NotBlank(message = "{lokalita.typ.require}")
    @Size(message = "{lokalita.nazev.max.80}")
    private String nazev;
    
    /* 
    @NotNull(message = "Je třeba doplnit alespoň jednu budovu")
    private List<BudovaDto> budovy; */

    public LokalitaDto(Lokalita lokalita) {
        if (lokalita == null) {
            return;
        }

        this.id = lokalita.getIdLokalita();
        this.nazev = lokalita.getNazev();

        /* 
        List<BudovaDto> budovaDtos = new ArrayList<>();
        if(lokalita.getBudovy() != null) {
            for(Budova budova: lokalita.getBudovy()) {
                budovaDtos.add(new BudovaDto(budova));
            }
        }
        this.setBudovy(budovaDtos);*/
    }

    public Lokalita toEntity() {
        Lokalita lokalita = new Lokalita();

        lokalita.setIdLokalita(this.id);
        lokalita.setNazev(this.nazev);

        /* 
        List<Budova> budovy = new ArrayList<>();
        if(this.getBudovy() != null) {
            for (BudovaDto budovaDto : this.getBudovy()) {
                budovy.add(new Budova(budovaDto.getId()));
            }
        }
        lokalita.setBudovy(budovy); */

        return lokalita;
    }

}
