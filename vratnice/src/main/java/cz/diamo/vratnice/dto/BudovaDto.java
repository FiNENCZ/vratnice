package cz.diamo.vratnice.dto;

import java.io.Serializable;

import cz.diamo.vratnice.entity.Budova;
import cz.diamo.vratnice.entity.Lokalita;
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

    @NotBlank(message = "{budova.nazev.require}")
    @Size(message = "{budova.nazev.max.80}")
    private String nazev;
    
    @NotNull(message = "{budova.lokalita.require}")
    private LokalitaDto lokalita;

    public BudovaDto(Budova budova) {
        if (budova == null) {
            return;
        }

        this.id = budova.getIdBudova();
        this.nazev = budova.getNazev();
        this.lokalita = new LokalitaDto(budova.getLokalita());

    }

    public Budova toEntity() {
        Budova budova = new Budova();

        budova.setIdBudova(this.id);
        budova.setNazev(this.nazev);
        budova.setLokalita(new Lokalita(getLokalita().getId()));

        return budova;
    }



}
