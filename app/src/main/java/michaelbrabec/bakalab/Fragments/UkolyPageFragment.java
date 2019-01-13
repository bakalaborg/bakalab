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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import michaelbrabec.bakalab.Adapters.UkolyBasicAdapter;
import michaelbrabec.bakalab.Interfaces.Callback;
import michaelbrabec.bakalab.ItemClasses.UkolItem;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.Utils.BakaTools;
import michaelbrabec.bakalab.Utils.ItemClickSupport;
import michaelbrabec.bakalab.Utils.Utils;

public class UkolyPageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback {

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<UkolItem> ukolList = new ArrayList<>();
    private UkolyBasicAdapter adapter = new UkolyBasicAdapter(ukolList);
    private Context context;
    private boolean todo;

    public void setTodo(boolean todo) {
        this.todo = todo;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.tab_content_ukoly, container, false);
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
                boolean expanded = adapter.ukolyList.get(position).isExpanded();
                adapter.ukolyList.get(position).setExpanded(!expanded);
                adapter.notifyItemChanged(position);
            }
        });
        makeRequest();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        adapter.notifyItemRangeRemoved(0, ukolList.size());
        ukolList.clear();
        makeRequest();
    }

    private void makeRequest() {

        UkolyPageFragment.GetUkolyTask getUkolyTask = new UkolyPageFragment.GetUkolyTask(this);
        getUkolyTask.execute(BakaTools.getUrl(context) + "/login.aspx?hx=" + BakaTools.getToken(context) + "&pm=ukoly");
    }

    @Override
    @SuppressWarnings("unchecked")
    // this is probably safe since it works and I want the compiler to shut up
    public void onCallbackFinish(Object result) {

        if (result != null) {

            ukolList.clear();
            List<UkolItem> resultList = (List<UkolItem>) result;

            for(UkolItem ukolItem : resultList)
                if ((ukolItem.getStatus().equals("probehlo") && todo) || (!ukolItem.getStatus().equals("probehlo") && !todo))
                    ukolList.add(ukolItem);

            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

        } else {
            Toast.makeText(context, "Chyba při zpracovávání úkolů", Toast.LENGTH_SHORT).show();
        }
    }

    private static class GetUkolyTask extends AsyncTask<String, Void, List<UkolItem>> {

        Callback callback;

        GetUkolyTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected List<UkolItem> doInBackground(String... url) {

            List<UkolItem> ukoly = new ArrayList<>();

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

                UkolItem ukol = new UkolItem();

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
                                case "predmet":
                                    ukol.setPredmet(tagContent);
                                    break;
                                case "nakdy":
                                    ukol.setNakdy(Utils.parseDate(tagContent, "yyMMddHHmm", "dd. MM. yyyy"));
                                    break;
                                case "popis":
                                    ukol.setPopis(tagContent.replace("<br />", "\n"));
                                    break;
                                case "status":
                                    ukol.setStatus(tagContent);
                                    ukoly.add(ukol);
                                    ukol = new UkolItem();
                                    break;
                            }
                            break;
                    }

                    event = parser.next();
                }

                Collections.sort(ukoly, new Comparator<UkolItem>() {
                    DateFormat f = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);

                    @Override
                    public int compare(UkolItem o1, UkolItem o2) {
                        try {
                            return f.parse(o2.getNakdy()).compareTo(f.parse(o1.getNakdy()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

            } catch (XmlPullParserException | IOException e) {
                return null;
            }

            return ukoly;
        }

        @Override
        protected void onPostExecute(List<UkolItem> list) {
            super.onPostExecute(list);
            callback.onCallbackFinish(list);
        }
    }
}