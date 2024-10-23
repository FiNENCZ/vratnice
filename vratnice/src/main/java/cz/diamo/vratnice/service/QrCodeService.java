package cz.diamo.vratnice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.NavstevniListek;
import cz.diamo.vratnice.entity.NavstevniListekUzivatelStav;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    /**
     * Generuje QR kód jako obrazový byte array na základě informací z navštěvního
     * lístku.
     *
     * @param navstevniListek Objekt {@link NavstevniListek}, který obsahuje
     *                        informace pro generování QR kódu.
     * @return Byte array reprezentující QR kód ve formátu PNG.
     * @throws WriterException Pokud dojde k chybě při generování QR kódu.
     * @throws IOException     Pokud dojde k chybě při zápisu obrazových dat do byte
     *                         array.
     */
    public byte[] generateQRCodeImage(NavstevniListek navstevniListek) throws WriterException, IOException {
        String content = generateQrNavstevniListek(navstevniListek);
        int width = 300;
        int height = 300;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    /**
     * Generuje textový obsah pro QR kód na základě informací z navštěvního lístku.
     *
     * @param navstevniListek Objekt {@link NavstevniListek}, který obsahuje
     *                        informace pro generování textu QR kódu.
     * @return Formátovaný řetězec obsahující informace o navštěvním lístku a
     *         navštěvovaných osobách.
     */
    private String generateQrNavstevniListek(NavstevniListek navstevniListek) {
        StringBuilder sb = new StringBuilder();

        // Informace o navštěvním lístku
        sb.append("ID navštěvního lístku: ").append(navstevniListek.getIdNavstevniListek()).append("\n");

        // Informace o navštěvovaných osobách
        sb.append("Návštěva: \n");
        int osobaIndex = 1;

        for (NavstevaOsoba navstevaOsoba : navstevniListek.getNavstevaOsoba()) {
            sb.append("Osoba ").append(osobaIndex).append(": \n");
            sb.append("Jméno: ").append(navstevaOsoba.getJmeno()).append("\n");
            sb.append("Příjmení: ").append(navstevaOsoba.getPrijmeni()).append("\n");
            sb.append("Číslo OP: ").append(navstevaOsoba.getCisloOp()).append("\n");
            sb.append("Společnost: ").append(navstevaOsoba.getSpolecnost().getNazev()).append("\n\n");
            osobaIndex++;
        }

        // Informace o uživateli
        sb.append("Navštívený zaměstnanec: \n");
        int uzivatelIndex = 1;

        for (NavstevniListekUzivatelStav uzivatelStav : navstevniListek.getUzivateleStav()) {
            Uzivatel uzivatel = uzivatelStav.getUzivatel();
            sb.append("Uživatel ").append(uzivatelIndex).append(": \n");
            sb.append("Název: ").append(uzivatel.getNazev()).append("\n");
            sb.append("Závod: ").append(uzivatel.getZavod().getNazev());
            sb.append(" (").append(uzivatel.getZavod().getSapId()).append(")").append("\n\n");
            uzivatelIndex++;
        }

        return sb.toString();
    }

}
