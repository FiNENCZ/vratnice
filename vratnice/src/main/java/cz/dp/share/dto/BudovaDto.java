package cz.dp.share.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.entity.Budova;
import cz.dp.share.entity.Lokalita;
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

    private String idExterni;

    @NotBlank(message = "{budova.nazev.require}")
    @Size(max = 80, message = "{budova.nazev.max.80}")
    private String nazev;

    private String poznamka;

    private Boolean aktivita;

    @NotNull(message = "{budova.lokalita.require}")
    private LokalitaDto lokalita;

    public BudovaDto(Budova budova) {
        if (budova == null) {
            return;
        }

        setId(budova.getIdBudova());
        setIdExterni(budova.getIdExterni());
        setNazev(budova.getNazev());
        setPoznamka(budova.getPoznamka());
        setAktivita(budova.getAktivita());
        setLokalita(new LokalitaDto(budova.getLokalita()));
    }

    @JsonIgnore
    public Budova getBudova(Budova budova, boolean pouzeId) {
        if (budova == null)
            budova = new Budova();

        budova.setIdBudova(getId());
        budova.setIdExterni(getIdExterni());

        if (!pouzeId) {
            budova.setNazev(getNazev());
            budova.setPoznamka(getPoznamka());
            budova.setAktivita(getAktivita());
            budova.setLokalita(new Lokalita(getLokalita().getId()));
        }

        return budova;
    }
}
