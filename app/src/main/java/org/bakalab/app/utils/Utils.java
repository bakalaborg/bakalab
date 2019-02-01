package org.bakalab.app.utils;

import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {

    public static String parseDate(String rawDate, String inputFormat, String outputFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat, Locale.US);
        SimpleDateFormat readable = new SimpleDateFormat(outputFormat, Locale.US);

        try {
            Date date = sdf.parse(rawDate);
            return readable.format(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String parseDate(String rawDate) {
        return parseDate(rawDate, "yyMMddHHmm", "dd. MM. yyyy HH:mm");
    }

    public static String getCurrentMonday(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        System.out.println();
        DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return df.format(c.getTime());
    }

    public static String getWebContent(URL url) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
            return null;
        }
    }

    public static int minutesOfDay(int h, int m) {
        return m+h*60;
    }

    public static int minutesOfDay(String t) {
        String time[] = t.split(":");
        int hours = Integer.valueOf(time[0]);
        int minutes = Integer.valueOf(time[1]);
        return minutes+hours*60;
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        //THIS METHOD SHOULD ONLY BE USED FOR DEBUGGING, DO NOT REFERENCE IT IN PRODUCTION CODE!!! - bypasses SSL certificate verification
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
