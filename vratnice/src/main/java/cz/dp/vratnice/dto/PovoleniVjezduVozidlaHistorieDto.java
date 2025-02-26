package cz.dp.vratnice.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import cz.dp.share.dto.UzivatelDto;
import cz.dp.vratnice.entity.PovoleniVjezduVozidlaZmenaStavu;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PovoleniVjezduVozidlaHistorieDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigInteger id;

    private String idPovoleniVjezduVozidel;

    private UzivatelDto uzivatel;

    private ZadostStavDto stavNovy;

    private ZadostStavDto stavPuvodni;

    private Boolean aktivitaNova;

    private Boolean aktivitaPuvodni;

    private Date cas;

    public PovoleniVjezduVozidlaHistorieDto(PovoleniVjezduVozidlaZmenaStavu povoleniZmenaStavu) {
        if (povoleniZmenaStavu == null)
            return;
        setId(povoleniZmenaStavu.getIdZadostZmenaStavu());
        setIdPovoleniVjezduVozidel(povoleniZmenaStavu.getPovoleniVjezduVozidla().getIdPovoleniVjezduVozidla());
        setStavNovy(new ZadostStavDto(povoleniZmenaStavu.getStavNovy()));
        setStavPuvodni(new ZadostStavDto(povoleniZmenaStavu.getStavPuvodni()));
        setAktivitaNova(povoleniZmenaStavu.getAktivitaNovy());
        setAktivitaPuvodni(povoleniZmenaStavu.getAktivitaPuvodni());
        setUzivatel(new UzivatelDto(povoleniZmenaStavu.getUzivatel()));
        setCas(povoleniZmenaStavu.getCas());
    }

}
