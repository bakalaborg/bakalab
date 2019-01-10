package michaelbrabec.bakalab.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.LoginFilter;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BakaTools {

    private static String token = "";
    private static String url = "";

    public static String getToken(Context context) {
        if (token.isEmpty()) {
            String prefToken = SharedPrefHandler.getString(context, "tokenBase");
            String loginJmeno = SharedPrefHandler.getString(context, "loginJmeno");
            String loginSkola = SharedPrefHandler.getString(context, "loginStrtyp");
            String bakalariUrl = SharedPrefHandler.getString(context, "bakalariUrl");
            if (prefToken.equals("") || loginJmeno.equals("") || loginSkola.equals("") || bakalariUrl.equals("")) {
                return null;
            }
            try {
                token = generateToken(prefToken);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }
        return token;
    }

    public static String getUrl(Context context) {
        if (url.isEmpty()) {
            url = SharedPrefHandler.getString(context, "bakalariUrl");
        }
        return url;
    }

    @SuppressLint("SimpleDateFormat")
    public static String generateToken(String tokenBase) throws NoSuchAlgorithmException {
        Calendar calendar = Calendar.getInstance();
         SimpleDateFormat mdformat = new SimpleDateFormat("YYYYMMDD");
        String strDate = mdformat.format(calendar.getTime());
        String token = getSha512(tokenBase + strDate);
        token = token.replace("/", "_");
        token = token.replace("+", "-");
        return token;
    }

    public static String getSha512(String hashPasswd) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(hashPasswd.getBytes());
        byte[] bytes = md.digest();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

}
