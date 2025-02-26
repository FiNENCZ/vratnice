package cz.dp.share.base;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class Utils {

	final static Logger logger = LogManager.getLogger(Utils.class);

	private static String algorithm = "DES";
	private static byte[] keyValue = new byte[] { '0', '2', '3', '1', '5', '7', '9', '3' };// your
	public final static String fixPassword = "********";
	public final static String sapIdPracovniPoziceNull = "99999999";
	public final static String sapIdPracovniPoziceNull2 = "00000000";

	private static final String[] svatky = { "1.1.", "1.5.", "8.5.", "5.7.", "6.7.", "28.9.", "28.10.", "17.11.",
			"24.12.", "25.12.", "26.12.", }; // Státní svátky a volné dny

	public static String camelToSnake(String str) {

		// Empty String
		String result = "";

		// Append first character(in lower case)
		// to result string
		char c = str.charAt(0);
		result = result + Character.toLowerCase(c);

		// Traverse the string from
		// ist index to last index
		for (int i = 1; i < str.length(); i++) {

			char ch = str.charAt(i);

			// Check if the character is upper case
			// then append '_' and such character
			// (in lower case) to result string
			if (Character.isUpperCase(ch)) {
				result = result + '_';
				result = result + Character.toLowerCase(ch);
			}

			// If the character is lower case then
			// add such character into result string
			else {
				result = result + ch;
			}
		}

		// return the result
		return result;
	}

	public static Integer booleanToInteger(Boolean value) {
		if (value == null)
			return null;
		if (value)
			return Integer.valueOf(1);
		else
			return Integer.valueOf(0);
	}

	public static Boolean integerToBoolean(Integer value) {
		if (value == null)
			return null;
		return value.equals(Integer.valueOf(1));
	}

	/***
	 * Získání hashe Bcrypt
	 * 
	 * @param txt , z kterého bude vytvořen hash
	 * @return hash
	 */
	public static String getBcrypt(String txt) {
		return BCrypt.hashpw(txt, BCrypt.gensalt(12));
	}

	/***
	 * Porovná správně zadané heslo při změně hesla
	 * 
	 * @param txt
	 * @param hashed
	 * @return
	 */
	public static Boolean getBcryptCheck(String txt, String hashed) {
		return BCrypt.checkpw(txt, hashed);
	}

	public static String textEncrypted(String text) {
		try {
			// KeyGenerator keygenerator = KeyGenerator.getInstance(algorithm);
			// SecretKey myDesKey = keygenerator.generateKey();
			Cipher desCipher;
			desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			// desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
			Key key = new SecretKeySpec(keyValue, algorithm);
			desCipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] textByte = text.getBytes();
			byte[] textEncrypted = desCipher.doFinal(textByte);
			String encryptedValue = Base64.getEncoder().encodeToString(textEncrypted);

			// desCipher.init(Cipher.DECRYPT_MODE, myDesKey);

			// Decrypt the text
			// byte[] textDecrypted = desCipher.doFinal(textEncrypted);

			// System.out.println("Text Decryted : " + new
			// String(textDecrypted));

			return encryptedValue;
		} catch (NoSuchAlgorithmException e) {
			logger.error("Chybný algoritmus encrypt", e);
		} catch (NoSuchPaddingException e) {
			logger.error("Chyba při vytváření instance Cipher", e);
		} catch (BadPaddingException e) {
			logger.error("Chyba při desCipher.doFinal - BadPaddingException", e);
		} catch (IllegalBlockSizeException e) {
			logger.error("Chyba při desCipher.doFinal - IllegalBlockSizeException", e);
		} catch (InvalidKeyException e) {
			logger.error("Chyba při inicializaci desCipher", e);
		}
		return null;
	}

	public static String textDecrypted(String text) {
		try {
			// KeyGenerator keygenerator = KeyGenerator.getInstance(algorithm);
			// SecretKey myDesKey = keygenerator.generateKey();
			Cipher desCipher;
			desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			// desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
			Key key = new SecretKeySpec(keyValue, algorithm);

			desCipher.init(Cipher.DECRYPT_MODE, key);

			byte[] encryptedValue = Base64.getDecoder().decode(text);

			byte[] byteDecrypted = desCipher.doFinal(encryptedValue);
			String textDecrypted = new String(byteDecrypted);
			return textDecrypted;
		} catch (NoSuchAlgorithmException e) {
			logger.error("Chybný algoritmus decrypt", e);
		} catch (NoSuchPaddingException e) {
			logger.error("Chyba při vytváření instance Cipher", e);
		} catch (BadPaddingException e) {
			logger.error("Chyba při desCipher.doFinal - BadPaddingException", e);
		} catch (IllegalBlockSizeException e) {
			logger.error("Chyba při desCipher.doFinal - IllegalBlockSizeException", e);
		} catch (InvalidKeyException e) {
			logger.error("Chyba při inicializaci desCipher", e);
		}
		return null;
	}

	public static Timestamp getCasZmn() {
		Timestamp casZmn = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return casZmn;
	}

	public static String getZmenuProv() {
		String username;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		if (authentication.getPrincipal() instanceof User) {
			User user = (User) authentication.getPrincipal();
			username = user.getUsername();
		} else if (authentication.getPrincipal() instanceof AppUserDto) {
			AppUserDto user = (AppUserDto) authentication.getPrincipal();
			// username = user.getUserName();
			username = user.getIdUzivatel();
		} else {
			username = (String) authentication.getPrincipal();
		}

		if ("anonymousUser".equals(username)) {
			return null;
		}

		return username;
	}

	public static String toString(Object object) {
		if (object == null)
			return "";
		else
			return object.toString().trim();
	}

	public static Date setMaxTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date setTime(Date date, Date time) {
		if (date == null || time == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar calTime = Calendar.getInstance();
		calTime.setTime(time);

		cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, calTime.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND));
		return cal.getTime();
	}

	public static Date setMinTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date addDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		date = calendar.getTime();
		return date;
	}

	public static Date addDay(Date date, int pocet) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, pocet);
		date = calendar.getTime();
		return date;
	}

	public static Date addDayPracovni(Date date, int pocet) {

		Integer pridano = 0;
		Date datum = date;
		boolean odecitat = false;
		if (pocet < 0) {
			odecitat = true;
			pocet = Math.abs(pocet);
		}
		while (pridano < pocet) {
			if (odecitat)
				datum = addDay(datum, -1);
			else
				datum = addDay(datum);
			if (!isSvatek(datum) && !isVikend(datum))
				pridano++;
		}

		return datum;
	}

	/**
	 * Získání minimálního data a času
	 * 
	 * @return
	 */
	public static Date getMinDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1900);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Získání minimálního data a času roku 1970
	 * 
	 * @return
	 */
	public static Date getMinDate1970() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1970);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Získání maximálního data a času
	 * 
	 * @return
	 */
	public static Date getMaxDate(boolean odstranitCas) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2999);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		if (!odstranitCas) {
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 99);
		} else {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}

		return calendar.getTime();
	}

	public static Boolean isMaxDate(Date date) {
		Date maxDate = getMaxDate(false);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar calendarMax = Calendar.getInstance();
		calendarMax.setTime(maxDate);

		return calendar.get(Calendar.YEAR) == calendarMax.get(Calendar.YEAR)
				&& calendar.get(Calendar.MONTH) == calendarMax.get(Calendar.MONTH)
				&& calendar.get(Calendar.DAY_OF_MONTH) == calendarMax.get(Calendar.DAY_OF_MONTH);
	}

	public static String vratGuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static void validate(Object object) throws ValidationException {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
		if (constraintViolations != null && constraintViolations.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<Object> validation : constraintViolations) {
				if (sb.length() != 0)
					sb.append("\n");
				sb.append(validation.getMessage());
			}
			throw new ValidationException(sb.toString(), "", true);
		}
	}

	public static Boolean exists(Integer count) {
		if (count == null)
			count = 0;
		return count > 0;
	}

	public static Integer rozdilVSekundach(Date datumOd, Date datumDo) {

		Long diff = datumDo.getTime() - datumOd.getTime();
		Double sekundy = (diff / 1000.0);
		Integer sekundyZaok = (int) (Math.ceil(sekundy));
		return sekundyZaok;
	}

	public static boolean stejnyDen(Date datum1, Date datum2, boolean vcetneNull) {

		if (datum1 == null && datum2 == null)
			return true;
		if (datum1 == null || datum2 == null)
			return false;
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(datum1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(datum2);

		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);

	}

	public static boolean stejnyDen(Date datum1, Date datum2) {

		return Utils.stejnyDen(datum1, datum2, false);

	}

	public static String getWebSocketZmenaOznameniUrl(AppUserDto appUserDto) {
		return getWebSocketZmenaOznameniUrl(appUserDto);
	}

	public static String getWebSocketZmenaOznameniUrl(String sapId) {
		return "/topic/zmena-oznameni/" + sapId;
	}

	/**
	 * Převod datumu na String ve formátu dd.MM.yyyy
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		DateFormat dateFormatKratky = new SimpleDateFormat("dd.MM.yyyy");
		String dateString = "";
		if (date != null && date.compareTo(getMinDate()) != 0 && date.compareTo(getMaxDate(true)) != 0)
			dateString = dateFormatKratky.format(date);
		return dateString;
	}

	/**
	 * Převod datumu na String ve formátu dd.MM.yyyy HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String dateTimeToString(Date date) {
		DateFormat dateFormatKratky = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		String dateString = "";
		if (date != null && date.compareTo(getMinDate()) != 0 && date.compareTo(getMaxDate(true)) != 0)
			dateString = dateFormatKratky.format(date);
		return dateString;
	}

	public static Date odstranitVteriny(Date date) {
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			date = calendar.getTime();
		}

		return date;
	}

	public static Integer rozdilVMinutach(Date datumOd, Date datumDo) {

		return rozdilVMinutach(datumOd, datumDo, true);
	}

	public static Integer rozdilVMinutach(Date datumOd, Date datumDo, boolean odstranitVteriny) {

		if (odstranitVteriny) {
			datumOd = Utils.odstranitVteriny(datumOd);
			datumDo = Utils.odstranitVteriny(datumDo);
		}
		Long diff = datumDo.getTime() - datumOd.getTime();
		Double minuty = (diff / (60.0 * 1000.0));
		Integer minutyZaok = (int) (Math.ceil(minuty));
		return minutyZaok;
	}

	public static Long rozdilVeDnech(Date date1, Date date2) {

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		calendar1.add(Calendar.HOUR, 6);// přidám hodiny kvůli chybě při změně času - raději 6
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);

		Long rozdil = Duration.between(calendar2.toInstant(), calendar1.toInstant()).toDays();

		// Calendar calendar1 = Calendar.getInstance();
		// calendar1.setTime(date1);
		// Calendar calendar2 = Calendar.getInstance();
		// calendar2.setTime(date2);
		// double rozdil = Math.floor((calendar1.getTimeInMillis() -
		// calendar2.getTimeInMillis()) / 1000 / 60 / 60 / 24);
		return rozdil;
	}

	public static String minuteToString(Integer minuty) {

		Integer hod = (int) Math.floor(minuty / 60);
		Integer min = minuty - (hod * 60);

		boolean minus = false;
		if (hod < 0 || min < 0)
			minus = true;
		hod = Math.abs(hod);
		min = Math.abs(min);
		String value = String.format("%02d:%02d", hod, min);
		if (minus)
			value = "-" + value;
		return value;
	}

	public static Integer dateToMinutes(Date date) {
		Integer minutes = 0;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		}

		return minutes;
	}

	public static Date minutesToDate(Integer minutes) {
		if (minutes == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Utils.getMinDate1970());
		calendar.set(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	/***
	 * Získání typu souboru
	 * 
	 * @param soubor
	 * @return
	 */
	public static String getMimeType(String soubor) {
		logger.debug("MIMETYPE pro: " + soubor);
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mimeType = fileNameMap.getContentTypeFor(soubor);
		if (StringUtils.isBlank(mimeType))
			mimeType = "application/octet-stream";
		return mimeType;
	}

	/**
	 * Vrátí menší (starší) z obou datumů
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date getLowerDate(Date date1, Date date2) {
		if (date1 != null && date2 != null) {
			return date1.getTime() <= date2.getTime() ? date1 : date2;
		} else if (date1 != null) {
			return date1;
		} else if (date2 != null) {
			return date2;
		}

		return null;
	}

	/**
	 * Vrátí vyšší (novější) z obou datumů
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date getHigherDate(Date date1, Date date2) {
		if (date1 != null && date2 != null) {
			return date1.getTime() >= date2.getTime() ? date1 : date2;
		} else if (date1 != null) {
			return date1;
		} else if (date2 != null) {
			return date2;
		}

		return null;
	}

	public static Date getZacatekRoku(Integer rok) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(rok, 0, 1);
		Date datumOd = calendar.getTime();
		datumOd = Utils.setMinTime(datumOd);
		return datumOd;
	}

	public static Date getKonecRoku(Integer rok) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(rok + 1, 0, 1);
		calendar.add(Calendar.DATE, -1);
		Date datum = calendar.getTime();
		datum = Utils.setMaxTime(datum);
		return datum;
	}

	public static Date odstranitCas(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		return date;
	}

	/**
	 * Test zda je dané datum státním svátkem
	 * 
	 * @param datum
	 * @return
	 */
	public static boolean isSvatek(Date datum) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(datum);
		Date velikonocePondeli = datumVelikonoc(calendar.get(Calendar.YEAR));
		Calendar calendarVelikonoce = Calendar.getInstance();
		calendarVelikonoce.setTime(velikonocePondeli);
		calendarVelikonoce.add(Calendar.DAY_OF_MONTH, -1);
		// neděle není svátek v ČR
		// Date velikonoceNedele = calendarVelikonoce.getTime();
		calendarVelikonoce.add(Calendar.DAY_OF_MONTH, -2);
		Date velikonocePatek = calendarVelikonoce.getTime();

		if (Utils.stejnyDen(datum, velikonocePondeli))
			return true;
		// if (Utils.stejnyDen(datum, velikonoceNedele))
		// return true;
		if (Utils.stejnyDen(datum, velikonocePatek))
			return true;
		Date svatek;
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		for (String svatekTxt : svatky) {
			try {
				svatek = format.parse(svatekTxt + calendar.get(Calendar.YEAR));
				if (Utils.stejnyDen(datum, svatek))
					return true;
			} catch (ParseException pe) {
				logger.error(pe);
			}
		}

		return false;
	}

	private static Date datumVelikonoc(Integer rok) {

		Calendar velikonoce = Calendar.getInstance();
		int l_nMonth, l_nDay, l_nMoon, l_nEpact, l_nSunday, l_nGold, l_nCent, l_nCorx, l_nCorz;
		// The Golden Number of the year in the 19 year Metonic Cycle:
		l_nGold = rok % 19 + 1;
		// Calculate the Century
		l_nCent = (int) (rok / 100) + 1;
		// Number of years in which leap year was dropped in order to keep in step with
		// the sun:
		l_nCorx = (int) ((3 * l_nCent) / 4) - 12;
		// Special correction to syncronize Easter with moon's orbit:
		l_nCorz = (int) ((8 * l_nCent + 5) / 25) - 5;
		// Find Sunday:
		l_nSunday = (int) ((5 * rok) / 4) - l_nCorx - 10;
		// ^ To prevent overflow at year 6554 must convert int 5 to Long
		// Set Epact - specifies occurrence of full moon:
		l_nEpact = (11 * l_nGold + 20 + l_nCorz - l_nCorx) % 30;
		if (l_nEpact < 0)
			l_nEpact = l_nEpact + 30;
		if (((l_nEpact == 25) && (l_nGold > 11)) || (l_nEpact == 24))
			l_nEpact = l_nEpact + 1;
		// Find Full Moon:
		l_nMoon = 44 - l_nEpact;
		if (l_nMoon < 21)
			l_nMoon = l_nMoon + 30;
		// Advance to Sunday:
		l_nMoon = l_nMoon + 7 - ((l_nSunday + l_nMoon) % 7);
		if (l_nMoon > 31) {
			l_nMonth = 4;
			l_nDay = l_nMoon - 31;
		} else {
			l_nMonth = 3;
			l_nDay = l_nMoon;
		} // end if

		// Složím výsledné datum
		velikonoce.set(rok, l_nMonth - 1, l_nDay);
		velikonoce.add(Calendar.DAY_OF_MONTH, 1);
		return velikonoce.getTime();

	}

	public static boolean isVikend(Date date) {
		Calendar calTmp = Calendar.getInstance();
		calTmp.setTime(date);

		return calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}
}