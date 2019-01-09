package michaelbrabec.bakalab.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import michaelbrabec.bakalab.Interfaces.Callback;

public class Login {

    private static String result = "", url;
    private String jmeno, heslo;
    private Context context;
    private Callback ActCallback;

    public Login(String url, String jmeno, String heslo, Context context, Callback callback) {
        Login.url = url;
        this.jmeno = jmeno;
        this.context = context;
        this.heslo = heslo;
        ActCallback = callback;

    }

    public void getResult() {

        StringRequest stringRequest = new StringRequest(url + "/login.aspx?gethx=" + jmeno, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                new ParserTask(new Callback() {
                    @Override
                    public void onCallbackFinish(final Object resultToken) {

                        final String[] resultTokenArray = (String[]) resultToken;

                        if (resultTokenArray != null){

                            StringRequest stringRequest = new StringRequest(url + "/login.aspx?hx="+resultTokenArray[0]+"&pm=login", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    new VerifyTask(new Callback() {
                                        @Override
                                        public void onCallbackFinish(Object result) {
                                            if (result != null) {

                                                String[] resultArr = (String[]) result;
                                                SharedPrefHandler.setString(Login.this.context, "tokenBase", resultTokenArray[1]);
                                                SharedPrefHandler.setString(Login.this.context, "loginJmeno", resultArr[0]);
                                                SharedPrefHandler.setString(Login.this.context, "loginSkola", resultArr[1]);
                                                SharedPrefHandler.setString(Login.this.context, "loginTrida", resultArr[2]);
                                                SharedPrefHandler.setString(Login.this.context, "loginRocnik", resultArr[3]);
                                                SharedPrefHandler.setString(Login.this.context, "loginModuly", resultArr[4]);
                                                SharedPrefHandler.setString(Login.this.context, "loginTyp", resultArr[5]);
                                                SharedPrefHandler.setString(Login.this.context, "loginStrtyp", resultArr[6]);
                                                SharedPrefHandler.setString(Login.this.context, "bakalariUrl", url);

                                                Login.result = "success";
                                            }
                                            else {
                                                Login.result = "Špatné heslo";
                                            }

                                            ActCallback.onCallbackFinish(Login.result);
                                        }
                                    }).execute(response);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Login.result = "Nelze se spojit se serverem";
                                }
                            });

                            NetworkRequests.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

                        } else {
                            ActCallback.onCallbackFinish(Login.result);
                        }

                    }
                }).execute(response, jmeno, heslo);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Login.result = "Nelze se spojit se serverem";
            }
        });

        NetworkRequests.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }


    private static class ParserTask extends AsyncTask<String, Void, String[]> {

        Callback callback;
        private ParserTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String tokenBase;
            try{
                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myParser = xmlFactoryObject.newPullParser();
                InputStream is = new ByteArrayInputStream(strings[0].getBytes("UTF-8"));
                myParser.setInput(is, null);

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);

                Element element=doc.getDocumentElement();
                element.normalize();

                String response = getValue("res", element);
                Log.d("result", response);

                switch (response) {
                    case "02":
                        Login.result = "Uživatel nenalezen";
                        return null;
                    case "01":
                        //Generating SHA-512 Base64 hash of the password here
                        String hashPasswd = getValue("salt", element) + getValue("ikod", element) + getValue("typ", element) + strings[2];
                        hashPasswd = BakaTools.getSha512(hashPasswd);

                        //We still to generate the token though
                        tokenBase = "*login*" + strings[1] + "*pwd*" + hashPasswd + "*sgn*ANDR";
                        //continue

                        return new String[] {
                                BakaTools.generateToken(tokenBase),
                                tokenBase
                        };
                    default:
                        Login.result = "Neznámá chyba";
                        return null;
                }


            } catch (XmlPullParserException | ParserConfigurationException
                    | IOException | SAXException | NoSuchAlgorithmException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            callback.onCallbackFinish(result);
        }
    }

    private static class VerifyTask extends AsyncTask<String, Void, String[]> {

        Callback callback;
        private VerifyTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected String[] doInBackground(String... strings) {
            try{
                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myParser = xmlFactoryObject.newPullParser();
                InputStream is = new ByteArrayInputStream(strings[0].getBytes("UTF-8"));
                myParser.setInput(is, null);

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);

                Element element=doc.getDocumentElement();
                element.normalize();


                String response = getValue("result", element);

                if(response.equals("-1")) {

                    return null;
                }

                return new String[]{
                        getValue("jmeno", element),
                        getValue("skola", element),
                        getValue("trida", element),
                        getValue("rocnik", element),
                        getValue("moduly", element),
                        getValue("typ", element),
                        getValue("strtyp", element)
                };

            } catch (IOException | XmlPullParserException | ParserConfigurationException | SAXException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            callback.onCallbackFinish(result);
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }


}
