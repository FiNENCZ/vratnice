package cz.diamo.vratnice.base;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class VratniceUtils {

    public static Boolean isDateInInterval(Date intervalOd, Date intervalDo, Date datumOd) {
        // Převod Date na LocalDate (což je pouze datum bez času)
        LocalDate intervalOdDate = intervalOd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate intervalDoDate = intervalDo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate datumOdDate = datumOd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Porovnání LocalDate objektů
        return (datumOdDate.equals(intervalOdDate) || datumOdDate.isAfter(intervalOdDate)) && 
               (datumOdDate.equals(intervalDoDate) || datumOdDate.isBefore(intervalDoDate));
    }

}
