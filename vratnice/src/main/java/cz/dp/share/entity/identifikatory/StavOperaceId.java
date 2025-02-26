package cz.dp.share.entity.identifikatory;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StavOperaceId implements Serializable {
    private String idUzivatel;

    private String idOperace;
}
