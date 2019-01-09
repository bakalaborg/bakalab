package michaelbrabec.bakalab.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String parseDate(String rawDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm", Locale.US);
        SimpleDateFormat readable = new SimpleDateFormat("dd. MM. yyyy HH:mm", Locale.US);

        try {
            Date date = sdf.parse(rawDate);
            return readable.format(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
