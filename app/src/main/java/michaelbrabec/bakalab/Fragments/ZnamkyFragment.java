package michaelbrabec.bakalab.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import michaelbrabec.bakalab.Adapters.ZnamkyBasicAdapter;
import michaelbrabec.bakalab.Interfaces.Callback;
import michaelbrabec.bakalab.ItemClasses.ZnamkaItem;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.Utils.BakaTools;
import michaelbrabec.bakalab.Utils.Utils;


public class ZnamkyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback {
    private List<ZnamkaItem> znamkaList = new ArrayList<>();
    private ZnamkyBasicAdapter adapter = new ZnamkyBasicAdapter(znamkaList);


    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;

    public ZnamkyFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_znamky, container, false);
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(true);
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
        adapter.notifyItemRangeRemoved(0, znamkaList.size());
        znamkaList.clear();
        makeRequest();
    }

    private void makeRequest() {

        GetZnamkyTask getZnamkyTask = new GetZnamkyTask(this);
        getZnamkyTask.execute(BakaTools.getUrl(context) + "/login.aspx?hx=" + BakaTools.getToken(context) + "&pm=znamky");
    }

    @Override
    @SuppressWarnings("unchecked")
    // this is probably safe since it works and I want the compiler to shut up
    public void onCallbackFinish(Object result) {

        if (result != null) {

            znamkaList.clear();
            znamkaList.addAll((List<ZnamkaItem>) result);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

        } else {
            Toast.makeText(context, "Chyba při zpracovávání známek", Toast.LENGTH_SHORT).show();
        }
    }

    private static class GetZnamkyTask extends AsyncTask<String, Void, List<ZnamkaItem>> {

        Callback callback;

        GetZnamkyTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected List<ZnamkaItem> doInBackground(String... url) {

            List<ZnamkaItem> znamky = new ArrayList<>();

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
                            switch (tagName) {
                                case "pred":
                                    znamka.setPredmet(tagContent);
                                    break;
                                case "zn":
                                    znamka.setZnamka(tagContent);
                                    break;
                                case "datum":
                                    znamka.setDatum(Utils.parseDate(tagContent, "yyMMdd", "dd. MM. yyyy"));
                                    break;
                                case "vaha":
                                    znamka.setVaha(tagContent);
                                    break;
                                case "caption":
                                    znamka.setPopis(tagContent.trim());
                                    znamky.add(znamka);
                                    znamka = new ZnamkaItem();
                                    break;
                            }
                            break;
                    }

                    event = parser.next();
                }

                Collections.sort(znamky, new Comparator<ZnamkaItem>() {
                    DateFormat f = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);

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
                return null;
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