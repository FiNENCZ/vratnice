package cz.dp.share.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.entity.OpravneniTypPristupu;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpravneniTypPristupuDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String nazev;

    public OpravneniTypPristupuDto(OpravneniTypPristupu opravneniTypPristupu) {
        if (opravneniTypPristupu == null)
            return;
        setId(opravneniTypPristupu.getIdOpravneniTypPristupu());
        setNazev(opravneniTypPristupu.getNazev());
    }

    @JsonIgnore
    public OpravneniTypPristupu getOpravneniTypPristupu(OpravneniTypPristupu opravneniTypPristupu) {
        if (opravneniTypPristupu == null)
            opravneniTypPristupu = new OpravneniTypPristupu();
        opravneniTypPristupu.setIdOpravneniTypPristupu(getId());
        return opravneniTypPristupu;

    }
}
