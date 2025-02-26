package cz.dp.share.dto.opravneni;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cz.dp.share.base.Utils;
import cz.dp.share.entity.OpravneniPrehled;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpravneniPrehledDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String kod;

    private String nazev;

    private String typPristupuKZamestnancum;

    private String zavody = "";

    private String zamestnanci = "";

    private Boolean aktivita = false;

    public OpravneniPrehledDto(OpravneniPrehled opravneniPrehled) {
        setId(opravneniPrehled.getIdOpravneni());
        setKod(opravneniPrehled.getKod());
        setNazev(opravneniPrehled.getNazev());
        setTypPristupuKZamestnancum(opravneniPrehled.getTypPristupu());
        setAktivita(opravneniPrehled.getAktivita());

        // závody
        pridatZavod(opravneniPrehled);

        // zamestnanci
        pridatZamestnance(opravneniPrehled);
    }

    @JsonIgnore
    public void pridatZavod(OpravneniPrehled opravneniPrehled) {
        if (!StringUtils.isBlank(opravneniPrehled.getZavodId())) {
            if (!StringUtils.isBlank(getZavody()))
                setZavody(getZavody() + "\n");
            setZavody(getZavody() + opravneniPrehled.getZavodNazev());
        }
    }

    @JsonIgnore
    public void pridatZamestnance(OpravneniPrehled opravneniPrehled) {
        if (!StringUtils.isBlank(opravneniPrehled.getZamestnanecId())) {
            if (!StringUtils.isBlank(getZamestnanci()))
                setZamestnanci(getZamestnanci() + "\n");
            String zamTxt = Utils.toString(opravneniPrehled.getZamestnanecPrijmeni()) + " "
                    + Utils.toString(opravneniPrehled.getZamestnanecJmeno());
            zamTxt = zamTxt.trim();
            if (!StringUtils.isBlank(opravneniPrehled.getZamestnanecSapId()))
                zamTxt = opravneniPrehled.getZamestnanecSapId() + " " + zamTxt;
            if (StringUtils.isBlank(zamTxt))
                zamTxt = opravneniPrehled.getZamestnanecNazev();
            setZamestnanci(getZamestnanci() + zamTxt);
        }
    }
}
