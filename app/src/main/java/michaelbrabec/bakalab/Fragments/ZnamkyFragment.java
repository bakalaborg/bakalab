package michaelbrabec.bakalab.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import michaelbrabec.bakalab.Interfaces.Callback;
import michaelbrabec.bakalab.ItemClasses.ZnamkaItem;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.RecyclerAdapters.ZnamkyBasicAdapter;
import michaelbrabec.bakalab.Utils.BakaTools;
import michaelbrabec.bakalab.Utils.NetworkRequests;
import michaelbrabec.bakalab.Utils.Utils;


public class ZnamkyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    List<ZnamkaItem> znamkaList = new ArrayList<>();
    ZnamkyBasicAdapter adapter = new ZnamkyBasicAdapter(znamkaList);


    Context context;

    SwipeRefreshLayout swipeRefreshLayout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ZnamkyFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ZnamkyFragment newInstance(String param1, String param2) {
        ZnamkyFragment fragment = new ZnamkyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_znamky, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(true);
        makeRequest();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        adapter.notifyItemRangeRemoved(0, znamkaList.size());
        znamkaList.clear();
        makeRequest();
    }

    private void makeRequest() {

        StringRequest stringRequest = new StringRequest(BakaTools.getUrl(context) + "/login.aspx?hx=" + BakaTools.getToken(context) + "&pm=znamky",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        XmlParseTask xmlParseTask = new XmlParseTask(new Callback() {
                            @Override
                            public void onCallbackFinish(Object result) {

                                znamkaList.clear();
                                znamkaList.addAll((List<ZnamkaItem>)result);
                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        xmlParseTask.execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        NetworkRequests.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private static class XmlParseTask extends AsyncTask<String, Void, List<ZnamkaItem>> {

        Callback callback;
        XmlParseTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected List<ZnamkaItem> doInBackground(String... xml) {
            List<ZnamkaItem> znamky = new ArrayList<>();
            XmlPullParserFactory parserFactory;
            Log.d("PARSE", xml[0]);

            try {
                parserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserFactory.newPullParser();
                InputStream is = new ByteArrayInputStream(xml[0].getBytes("UTF-8"));
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);

                String tagName, tagContent = "";
                int event = parser.getEventType();

                ZnamkaItem znamka = new ZnamkaItem();

                while (event != XmlPullParser.END_DOCUMENT) {
                    tagName = parser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.TEXT:
                            tagContent = parser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            switch(tagName) {
                                case "pred": znamka.setPredmet(tagContent);
                                    break;
                                case "zn": znamka.setZnamka(tagContent);
                                    break;
                                case "udeleno": znamka.setDatum(Utils.parseDate(tagContent));
                                    break;
                                case "vaha": znamka.setVaha(tagContent);
                                    break;
                                case "caption": znamka.setPopis(tagContent.trim());
                                    znamky.add(znamka);
                                    znamka = new ZnamkaItem();
                                    break;
                            }
                            break;
                    }

                    event = parser.next();
                }

                Collections.sort(znamky, new Comparator<ZnamkaItem>() {
                    DateFormat f = new SimpleDateFormat("dd. MM. yyyy HH:mm", Locale.ENGLISH);
                    @Override
                    public int compare(ZnamkaItem o1, ZnamkaItem o2) {
                        try {
                            return f.parse(o2.getDatum()).compareTo(f.parse(o1.getDatum()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

            } catch (XmlPullParserException | IOException e) {

            }

            return znamky;
        }

        @Override
        protected void onPostExecute(List<ZnamkaItem> list) {
            super.onPostExecute(list);
            callback.onCallbackFinish(list);
        }
    }


}
