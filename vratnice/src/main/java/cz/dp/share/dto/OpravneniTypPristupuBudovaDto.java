package cz.dp.share.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.entity.OpravneniTypPristupuBudova;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpravneniTypPristupuBudovaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public OpravneniTypPristupuBudovaDto(OpravneniTypPristupuBudova opravneniTypPristupuBudova) {
        if (opravneniTypPristupuBudova == null)
            return;
        setId(opravneniTypPristupuBudova.getIdOpravneniTypPristupuBudova());
        setNazev(opravneniTypPristupuBudova.getNazev());
    }

    @JsonIgnore
    public OpravneniTypPristupuBudova getOpravneniTypPristupuBudova(OpravneniTypPristupuBudova opravneniTypPristupuBudova) {
        if (opravneniTypPristupuBudova == null)
            opravneniTypPristupuBudova = new OpravneniTypPristupuBudova();
        opravneniTypPristupuBudova.setIdOpravneniTypPristupuBudova(getId());
        return opravneniTypPristupuBudova;

    }
}
