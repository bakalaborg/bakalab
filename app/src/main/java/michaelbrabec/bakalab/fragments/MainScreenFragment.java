package michaelbrabec.bakalab.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.adapters.UkolyBasicAdapter;
import michaelbrabec.bakalab.adapters.ZnamkyBasicAdapter;
import michaelbrabec.bakalab.interfaces.Callback;
import michaelbrabec.bakalab.items.UkolItem;
import michaelbrabec.bakalab.items.ZnamkaItem;
import michaelbrabec.bakalab.utils.BakaTools;
import michaelbrabec.bakalab.utils.ItemClickSupport;
import michaelbrabec.bakalab.utils.Utils;

public class MainScreenFragment extends Fragment implements Callback, SwipeRefreshLayout.OnRefreshListener {

    private static class Result {
        List<UkolItem> ukolItems = new ArrayList<>();
        List<ZnamkaItem> znamkaItems = new ArrayList<>();
        Predmet predmet;

        List<UkolItem> getUkolItems() {
            return ukolItems;
        }

        void setUkolItems(List<UkolItem> ukolItems) {
            this.ukolItems = ukolItems;
        }

        List<ZnamkaItem> getZnamkaItems() {
            return znamkaItems;
        }

        void setZnamkaItems(List<ZnamkaItem> znamkaItems) {
            this.znamkaItems = znamkaItems;
        }

        Predmet getPredmet() {
            return predmet;
        }

        void setPredmet(Predmet predmet) {
            this.predmet = predmet;
        }
    }

    private static class Predmet {
        Predmet(boolean free) {
            this.free = free;
        }

        Predmet() {

        }

        private String nazev = "", ucitel = "", mistnost = "", cas = "";
        boolean free = false;

        String getCas() {
            return cas;
        }

        void setCas(String cas) {
            this.cas = cas;
        }

        String getNazev() {
            return nazev;
        }

        void setNazev(String nazev) {
            this.nazev = nazev;
        }

        String getUcitel() {
            return ucitel;
        }

        void setUcitel(String ucitel) {
            this.ucitel = ucitel;
        }

        String getMistnost() {
            return mistnost;
        }

        void setMistnost(String mistnost) {
            this.mistnost = mistnost;
        }

        boolean isFree(){
            return free;
        }
    }

    private List<ZnamkaItem> znamkaList = new ArrayList<>();
    private ZnamkyBasicAdapter znamkyBasicAdapter = new ZnamkyBasicAdapter(znamkaList);

    private List<UkolItem> ukolList = new ArrayList<>();
    private UkolyBasicAdapter ukolyBasicAdapter = new UkolyBasicAdapter(ukolList);

    private RecyclerView ukolyRec;
    private RecyclerView znamkyRec;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SkeletonScreen skeletonScreenUkoly;
    private SkeletonScreen skeletonScreenZnamky;
    private SkeletonScreen skeletonScreenNext;

    private TextView nextTitle, nextDesc;

    private ConstraintLayout root;

    private boolean blockClick = true;


    public MainScreenFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_ukoly).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.ukolyFragment));
        view.findViewById(R.id.button_rozvrh).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.rozvrhFragment));
        view.findViewById(R.id.button_znamky).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.znamkyFragment));

        ukolyRec = view.findViewById(R.id.ukoly_list);
        znamkyRec = view.findViewById(R.id.znamky_list);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        nextTitle = view.findViewById(R.id.next_title);
        nextDesc = view.findViewById(R.id.next_desc);
        root = view.findViewById(R.id.root);

        swipeRefreshLayout.setOnRefreshListener(this);

        ukolyRec.setHasFixedSize(true);
        znamkyRec.setHasFixedSize(true);

        LinearLayoutManager layoutManagerUkoly = new LinearLayoutManager(getContext());
        LinearLayoutManager layoutManagerZnamky = new LinearLayoutManager(getContext());

        ukolyRec.setLayoutManager(layoutManagerUkoly);
        znamkyRec.setLayoutManager(layoutManagerZnamky);

        ukolyRec.setAdapter(ukolyBasicAdapter);
        znamkyRec.setAdapter(znamkyBasicAdapter);

        ItemClickSupport.addTo(ukolyRec).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (!blockClick) {
                    boolean expanded = ukolyBasicAdapter.ukolyList.get(position).isExpanded();
                    ukolyBasicAdapter.ukolyList.get(position).setExpanded(!expanded);
                    ukolyBasicAdapter.notifyItemChanged(position);
                }
            }
        });

        ItemClickSupport.addTo(znamkyRec).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (!blockClick) {
                    boolean expanded = znamkyBasicAdapter.znamkyList.get(position).isExpanded();
                    znamkyBasicAdapter.znamkyList.get(position).setExpanded(!expanded);
                    znamkyBasicAdapter.notifyItemChanged(position);
                }
            }
        });

        makeRequest();
    }

    private void showSkeletons() {

        skeletonScreenUkoly = Skeleton.bind(ukolyRec)
                .adapter(ukolyBasicAdapter)
                .load(R.layout.list_item_skeleton)
                .count(3)
                .show();

        skeletonScreenZnamky = Skeleton.bind(znamkyRec)
                .adapter(znamkyBasicAdapter)
                .load(R.layout.list_item_skeleton)
                .count(3)
                .show();

        skeletonScreenNext = Skeleton.bind(root)
                .load(R.layout.skeleton_next_card)
                .show();
    }

    @Override
    public void onRefresh() {
        makeRequest();
    }

    private void makeRequest() {
        showSkeletons();
        blockClick = true;
        GetAllTask getZnamkyTask = new GetAllTask(this);
        getZnamkyTask.execute(BakaTools.getUrl(getContext()) + "/login.aspx?hx=" + BakaTools.getToken(getContext()) + "&pm=all");
    }

    @Override
    public void onCallbackFinish(Object callResult) {
        if (callResult != null) {
            Result result = (Result) callResult;

            ukolList.clear();
            znamkaList.clear();
            ukolList.addAll(result.getUkolItems());
            znamkaList.addAll(result.getZnamkaItems());

            ukolyBasicAdapter.notifyDataSetChanged();
            znamkyBasicAdapter.notifyDataSetChanged();

            if (!result.getPredmet().isFree()) {
                nextTitle.setText(result.getPredmet().getNazev());
                nextDesc.setText("Začína v " + result.getPredmet().getCas() + ", učebna " + result.getPredmet().getMistnost() + ", " + result.getPredmet().getUcitel());
                nextDesc.setVisibility(View.VISIBLE);
            } else {
                nextTitle.setText(getString(R.string.free_hour));
                nextDesc.setVisibility(View.GONE);

            }

            skeletonScreenUkoly.hide();
            skeletonScreenZnamky.hide();
            skeletonScreenNext.hide();

            blockClick = false;

            swipeRefreshLayout.setRefreshing(false);

        } else {
            Toast.makeText(getContext(), "Chyba při zpracovávání", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setVisibility(View.GONE);
        }
    }


    private static class GetAllTask extends AsyncTask<String, Void, Result> {

        Callback callback;

        private class Hodina {
            private String poradi = "", beginTime = "", den = "";

            String getBeginTime() {
                return beginTime;
            }

            void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }

            String getPoradi() {
                return poradi;
            }

            void setPoradi(String poradi) {
                this.poradi = poradi;
            }

            String getDen() {
                return den;
            }

            void setDen(String den) {
                this.den = den;
            }
        }


        GetAllTask(Callback callback) {
            this.callback = callback;
        }

        private List<ZnamkaItem> parseZnamky(XmlPullParser parser) {
            List<ZnamkaItem> znamky = new ArrayList<>();
            try {
                String tagName, tagContent = "", predmet = "";
                int event = parser.getEventType();

                ZnamkaItem znamka = new ZnamkaItem();

                whileloop:
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
                                case "xmlznamky":
                                    break whileloop;
                                case "nazev":
                                    predmet = tagContent;
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
                                    if (tagContent.trim().isEmpty()) {
                                        znamka.setPopis(predmet);
                                    } else {
                                        znamka.setPopis(tagContent.trim());
                                    }
                                    break;
                                case "poznamka":
                                    if (tagContent.trim().isEmpty()) {
                                        znamka.setPoznamka(predmet);
                                    } else {
                                        znamka.setPoznamka(predmet + " — " + tagContent.trim());
                                    }
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

            } catch (XmlPullParserException |
                    IOException e) {
                return null;
            }

            return znamky.subList(0, 3);
        }

        private List<UkolItem> parseUkoly(XmlPullParser parser) {
            List<UkolItem> ukoly = new ArrayList<>();

            try {
                String tagName, tagContent = "";
                int event = parser.getEventType();

                UkolItem ukol = new UkolItem();
                whileloop:
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
                                case "xmlukoly":
                                    break whileloop;
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

            return ukoly.subList(0, 3);

        }

        private Hodina parseHodiny(XmlPullParser parser) {

            try {

                String tagName, tagContent = "", caption = "";
                int event = parser.getEventType();

                String lastBeginTime = "0:0";
                String currentTime = new SimpleDateFormat("H:m", Locale.US).format(new Date());

                whileloop:
                while (event != XmlPullParser.END_DOCUMENT) {
                    tagName = parser.getName();

                    switch (event) {
                        case XmlPullParser.TEXT:
                            tagContent = parser.getText();

                            break;

                        case XmlPullParser.END_TAG:
                            switch (tagName) {

                                case "hodiny":
                                    break whileloop;
                                case "caption":
                                    caption = tagContent;
                                    break;
                                case "begintime":
                                    if (Utils.minutesOfDay(lastBeginTime) < Utils.minutesOfDay(currentTime)
                                            && Utils.minutesOfDay(currentTime) < Utils.minutesOfDay(tagContent)) {
                                        Hodina hodina = new Hodina();
                                        hodina.setPoradi(caption);
                                        hodina.setBeginTime(tagContent);
                                        hodina.setDen(new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date()));
                                        return hodina;
                                    }

                                    break;
                            }
                            break;
                    }

                    event = parser.next();
                }

            } catch (XmlPullParserException | IOException e) {
                return null;
            }

            return null;

        }

        private Predmet parseRozvrh(XmlPullParser parser) {

            try {

                String tagName, tagContent = "";
                int event = parser.getEventType();

                Hodina hodina = new Hodina();
                Predmet predmet = new Predmet();
                boolean hasHodina = false;
                boolean correctDate = false;
                whileloop:
                while (event != XmlPullParser.END_DOCUMENT) {
                    tagName = parser.getName();

                    switch (event) {
                        case XmlPullParser.TEXT:
                            tagContent = parser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            switch (tagName) {
                                case "xmlrozvrhakt":
                                    break whileloop;
                                case "nazevcyklu":
                                    hodina = parseHodiny(parser);
                                    if (hodina == null) {
                                        return new Predmet(true);
                                    }
                                    hasHodina = true;
                                    predmet.setCas(hodina.getBeginTime());
                                    break;
                                case "datum":
                                    if (hodina.getDen().equals(tagContent)) {
                                        correctDate = true;
                                    }
                                    break;
                                case "pr":
                                    predmet.setNazev(tagContent);
                                    break;
                                case "uc":
                                    predmet.setUcitel(tagContent);
                                    break;
                                case "zkrmist":
                                    predmet.setMistnost(tagContent);
                                    break;
                                case "caption":
                                    if (hasHodina && correctDate && tagContent.equals(hodina.getPoradi())) {
                                        return predmet;
                                    }
                                    break;

                            }
                            break;
                    }

                    event = parser.next();
                }

            } catch (XmlPullParserException | NullPointerException | IOException e) {

                return null;
            }
            return null;

        }

        @Override
        protected Result doInBackground(String... url) {

            Result finalResult = new Result();


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

                String tagName;
                int event = parser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    tagName = parser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            switch (tagName) {
                                case "xmlznamky":
                                    finalResult.setZnamkaItems(parseZnamky(parser));
                                    break;
                                case "xmlukoly":
                                    finalResult.setUkolItems(parseUkoly(parser));
                                    break;
                                case "xmlrozvrhakt":
                                    Predmet predmet = parseRozvrh(parser);
                                    finalResult.setPredmet(predmet);
                                    break;
                            }
                            break;
                    }

                    event = parser.next();
                }


            } catch (XmlPullParserException | IOException e) {
                return null;
            }

            return finalResult;
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            callback.onCallbackFinish(result);
        }
    }
}
