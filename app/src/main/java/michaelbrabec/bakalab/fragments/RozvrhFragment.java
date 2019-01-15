package michaelbrabec.bakalab.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.adapters.RozvrhBasicAdapter;
import michaelbrabec.bakalab.interfaces.Callback;
import michaelbrabec.bakalab.items.RozvrhItem;
import michaelbrabec.bakalab.items.RozvrhTimeItem;
import michaelbrabec.bakalab.utils.BakaTools;
import michaelbrabec.bakalab.utils.ItemClickSupport;
import michaelbrabec.bakalab.utils.Utils;


public class RozvrhFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback {

    private List<RozvrhItem> rozvrhList = new ArrayList<>();
    private RozvrhBasicAdapter adapter = new RozvrhBasicAdapter(rozvrhList);

    private boolean clickable;

    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;

    public RozvrhFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
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

        RozvrhFragment.GetRozvrhTask getRozvrhTask = new RozvrhFragment.GetRozvrhTask(this);
        getRozvrhTask.execute(BakaTools.getUrl(context) + "/login.aspx?hx=" + BakaTools.getToken(context) + "&pm=rozvrh&pmd=20190115");
    }

    @Override
    @SuppressWarnings("unchecked")
    // this is probably safe since it works and I want the compiler to shut up
    public void onCallbackFinish(Object result) {

        if (result != null) {
            clickable = false;
            rozvrhList.clear();
            rozvrhList.addAll((List<RozvrhItem>) result);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            clickable = true;

        } else {
            Toast.makeText(context, "Chyba při zpracovávání známek", Toast.LENGTH_SHORT).show();
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
            List<RozvrhTimeItem> rozvrhTimeList = new ArrayList<>();
            rozvrhTimeList.add(null); //hopefully the "0th class" will still work properly with this

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
                InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);

                String tagName, tagContent = "", caption = "";
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
                                case "caption":
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
                                    rozvrh.setDatum(Utils.parseDate(tagContent, "yyMMdd", "dd"));
                                    rozvrh.setItemType(1);
                                    rozvrhList.add(rozvrh);
                                    rozvrh = new RozvrhItem();
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
                                    rozvrh.setTema(tagContent);
                                    break;
                                case "notice":
                                    rozvrh.setBegintime(rozvrhTimeList.get(Integer.parseInt(caption)).getBegintime());
                                    rozvrh.setEndtime(rozvrhTimeList.get(Integer.parseInt(caption)).getEndtime());
                                    rozvrhList.add(rozvrh);
                                    rozvrh = new RozvrhItem();
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

}
