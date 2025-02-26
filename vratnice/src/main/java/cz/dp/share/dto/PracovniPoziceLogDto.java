package cz.dp.share.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import cz.dp.share.base.Utils;
import cz.dp.share.entity.PracovniPoziceLog;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PracovniPoziceLogDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Date casVolani;

    private Date casZpracovani;

    private Integer pocetZaznamu;

    private boolean ok;

    private String chyba;

    private String jsonLog;

    public boolean getVarovani() {
        if (!isOk())
            return true;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Date date = Utils.odstranitCas(calendar.getTime());
        return getCasVolani().compareTo(date) == -1;
    }

    public PracovniPoziceLogDto(PracovniPoziceLog pracovniPoziceLog) {
        if (pracovniPoziceLog == null)
            return;
        setId(pracovniPoziceLog.getIdPracovniPoziceLog());
        setCasVolani(pracovniPoziceLog.getCasVolani());
        setCasZpracovani(pracovniPoziceLog.getCasZpracovani());
        setPocetZaznamu(pracovniPoziceLog.getPocetZaznamu());
        setOk(pracovniPoziceLog.isOk());
        setChyba(pracovniPoziceLog.getChyba());
        setJsonLog(pracovniPoziceLog.getJsonLog());
    }
}
