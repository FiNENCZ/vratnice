package cz.diamo.share.dto;

import java.io.Serializable;

import cz.diamo.share.entity.ExterniUzivatel;
import cz.diamo.share.entity.Uzivatel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VytvorilDto implements Serializable {

    private UzivatelDto uzivatel;

    private ExterniUzivatelDto externiUzivatel;

    public VytvorilDto(Uzivatel uzivatel, ExterniUzivatel externiUzivatel) {
        if (uzivatel != null)
            setUzivatel(new UzivatelDto(uzivatel));
        if (externiUzivatel != null)
            setExterniUzivatel(new ExterniUzivatelDto(externiUzivatel));
    }

    public String getNazev() {
        if (getUzivatel() != null)
            return getUzivatel().getNazev();
        if (getExterniUzivatel() != null)
            return getExterniUzivatel().getNazev();
        return null;
    }

}
