package org.bakalab.app.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bakalab.app.R;
import org.bakalab.app.adapters.RozvrhBasicAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.interfaces.Callback;
import org.bakalab.app.items.RozvrhItem;
import org.bakalab.app.items.RozvrhTimeItem;
import org.bakalab.app.items.rozvrh.Rozvrh;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.rozvrh.RozvrhHodina;
import org.bakalab.app.items.rozvrh.RozvrhRoot;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.ItemClickSupport;
import org.bakalab.app.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.internal.EverythingIsNonNull;


public class RozvrhFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback {

    private List<RozvrhHodina> rozvrhList = new ArrayList<>();
    private RozvrhBasicAdapter adapter = new RozvrhBasicAdapter(rozvrhList);

    private boolean clickable;

    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;

    public RozvrhFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rozvrh, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(true);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (clickable) {
                    boolean expanded = adapter.rozvrhList.get(position).isExpanded();
                    adapter.rozvrhList.get(position).setExpanded(!expanded);
                    adapter.notifyItemChanged(position);
                }
            }
        });

        makeRequest();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        makeRequest();
    }

    private void makeRequest() {
        OkHttpClient okHttpClient = getUnsafeOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BakaTools.getUrl(this.getContext()))
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .client(okHttpClient)
                .build();

        BakalariAPI bakalariAPI = retrofit.create(BakalariAPI.class);

        Call<RozvrhRoot> call = bakalariAPI.getRozvrh(BakaTools.getToken(this.getContext()), Utils.getCurrentMonday());

        call.enqueue(new retrofit2.Callback<RozvrhRoot>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<RozvrhRoot> call, Response<RozvrhRoot> response) {
                if (!response.isSuccessful()) {
                    Log.d("Error", response.message());
                    return;
                }

                clickable = false;

                rozvrhList.clear();
                //Log.d("bakalabcu", response.body().getRozvrh().getTyp());
                rozvrhList.addAll(response.body().getRozvrh().getDny().get(1).getHodiny());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                clickable = true;
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<RozvrhRoot> call, Throwable t) {
                Log.d("Errorsss", t.getMessage());

            }
        });
    }

    private void makeRequestOld() {

        RozvrhFragment.GetRozvrhTask getRozvrhTask = new RozvrhFragment.GetRozvrhTask(this);
        getRozvrhTask.execute(BakaTools.getUrl(context) + "/login.aspx?hx=" + BakaTools.getToken(context) + "&pm=rozvrh&pmd=" + Utils.getCurrentMonday());
    }

    @Override
    @SuppressWarnings("unchecked")
    // this is probably safe since it works and I want the compiler to shut up
    public void onCallbackFinish(Object result) {

        if (result != null) {
            clickable = false;
            rozvrhList.clear();
            //rozvrhList.addAll((List<RozvrhItem>) result);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            clickable = true;

        } else {
            Toast.makeText(context, "Chyba při zpracovávání rozvrhu", Toast.LENGTH_SHORT).show();
        }
    }

    private static class GetRozvrhTask extends AsyncTask<String, Void, List<RozvrhItem>> {

        Callback callback;

        GetRozvrhTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected List<RozvrhItem> doInBackground(String... url) {

            List<RozvrhItem> rozvrhList = new ArrayList<>();
            List<RozvrhItem> emptyRozvrhList = new ArrayList<>();
            List<RozvrhTimeItem> rozvrhTimeList = new ArrayList<>();

            try {

                XmlPullParserFactory parserFactory;
                URL u;

                u = new URL(url[0]);

                String xml = Utils.getWebContent(u);
                if (xml == null) {
                    return null;
                }
                parserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserFactory.newPullParser();
                InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);

                String tagName, tagContent = "", caption = "", lastType = "", lastNonXType = "";
                int iteration = 0;
                int event = parser.getEventType();

                RozvrhItem rozvrh = new RozvrhItem();
                RozvrhTimeItem rozvrhTimeItem = new RozvrhTimeItem();

                while (event != XmlPullParser.END_DOCUMENT) {
                    tagName = parser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.TEXT:
                            tagContent = parser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            switch (tagName) {
                                case "typ":
                                    if (tagContent.equals("X")) {
                                        //FREE CLASS | VOLNA HODINA

                                        if (!lastType.equals("X"))
                                            emptyRozvrhList = new ArrayList<>();

                                        try {
                                            RozvrhTimeItem currentClass = rozvrhTimeList.get(iteration);
                                            rozvrh.setBegintime(currentClass.getBegintime());
                                            rozvrh.setEndtime(currentClass.getEndtime());
                                            emptyRozvrhList.add(rozvrh);
                                            rozvrh = new RozvrhItem();

                                            /*we add to a separate array so we can throw it all away if we need to
                                              bakaláři likes to mark all classes as existant even if they don't
                                              exist, so adding them to the rozvrh array directly would just make
                                              us annoy the user by spamming them with empty classes */

                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }
                                        iteration++;

                                    }

                                    lastType = tagContent;
                                    break;
                                case "caption":
                                    //this is universal, we don't know if this is from a time item or a lesson item, so we just save the string and go on
                                    caption = tagContent;
                                    break;
                                case "begintime":
                                    rozvrhTimeItem.setBegintime(tagContent);
                                    break;
                                case "endtime":
                                    rozvrhTimeItem.setEndtime(tagContent);
                                    rozvrhTimeItem.setCaption(caption);
                                    rozvrhTimeList.add(rozvrhTimeItem);
                                    rozvrhTimeItem = new RozvrhTimeItem();
                                    break;
                                case "zkratka":
                                    rozvrh.setZkratka(tagContent);
                                    break;
                                case "datum":
                                    rozvrh.setDatum(Utils.parseDate(tagContent, "yyyyMMdd", "d"));
                                    rozvrh.setItemType(1);
                                    rozvrhList.add(rozvrh);
                                    rozvrh = new RozvrhItem();
                                    lastType = lastNonXType = "Date";
                                    iteration = 0;
                                    break;
                                case "pr":
                                    rozvrh.setPr(tagContent);
                                    break;
                                case "zkruc":
                                    rozvrh.setZkruc(tagContent);
                                    break;
                                case "zkrmist":
                                    rozvrh.setZkrmist(tagContent);
                                    break;
                                case "tema":
                                    rozvrh.setTema(tagContent.trim());
                                    break;
                                case "notice":
                                    /* the lesson number is labeled as caption
                                       this is consistent in both the time items and the lesson items
                                       i just found doing this by manually counting the iterations easier
                                     */

                                    rozvrh.setBegintime(rozvrhTimeList.get(iteration).getBegintime());
                                    rozvrh.setEndtime(rozvrhTimeList.get(iteration).getEndtime());

                                    //here we add the empty lessons if they're not at the beginning or the end of the day
                                    if (!lastNonXType.equals("Date") && !emptyRozvrhList.isEmpty())
                                        rozvrhList.addAll(emptyRozvrhList);

                                    //cleaning up etc.
                                    emptyRozvrhList = new ArrayList<>();
                                    rozvrhList.add(rozvrh);
                                    rozvrh = new RozvrhItem();
                                    lastNonXType = lastType;
                                    iteration++;
                                    break;
                            }
                            break;
                    }

                    event = parser.next();
                }

            } catch (XmlPullParserException | IOException e) {
                return null;
            }

            return rozvrhList;
        }

        @Override
        protected void onPostExecute(List<RozvrhItem> list) {
            super.onPostExecute(list);
            callback.onCallbackFinish(list);
        }
    }
    public OkHttpClient getUnsafeOkHttpClient() {
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
