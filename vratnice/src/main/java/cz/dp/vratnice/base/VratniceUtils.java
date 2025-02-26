package cz.dp.vratnice.base;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    public static Boolean isDateRangeOverlapping(Date intervalOd, Date intervalDo, Date datumOd, Date datumDo) {
        // Prevod Date na LocalDate
        LocalDate intervalOdDate = intervalOd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate intervalDoDate = intervalDo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate datumOdDate = datumOd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate datumDoDate = datumDo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Kontrola, zda je datumOd starsi nez intervalDo a datumDo novejsi nez intervalOd
        return datumOdDate.isBefore(intervalDoDate) && datumDoDate.isAfter(intervalOdDate);
    }

    // Funkce pro získání aktuálního času ve formátu "HH:mm dd.MM.yyyy"
    public static String getCurrentFormattedDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        return now.format(formatter);
    }

}
