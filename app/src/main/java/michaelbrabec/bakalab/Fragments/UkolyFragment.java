package michaelbrabec.bakalab.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import michaelbrabec.bakalab.Adapters.UkolyPagerAdapter;
import michaelbrabec.bakalab.Interfaces.Callback;
import michaelbrabec.bakalab.Interfaces.UkolyInterface;
import michaelbrabec.bakalab.ItemClasses.UkolItem;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.Utils.BakaTools;
import michaelbrabec.bakalab.Utils.SharedPrefHandler;
import michaelbrabec.bakalab.Utils.Utils;


public class UkolyFragment extends Fragment implements Callback, UkolyInterface {

    private Context context;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    Callback callback;

    public UkolyFragment() { }

    @Override
    @SuppressWarnings("unchecked")
    // this is probably safe since it works and I want the compiler to shut up
    public void onPageRefresh() {
        makeRequest();
    }

    @Override
    @SuppressWarnings("unchecked")
    // this is probably safe since it works and I want the compiler to shut up
    public void onCallbackFinish(Object result) {

        if (result != null) {

            List<UkolItem> resultList = (List<UkolItem>) result;

            List<UkolItem> listTodo = new ArrayList<UkolItem>();
            List<UkolItem> listFinished = new ArrayList<UkolItem>();

            for(UkolItem ukolItem : resultList)
                if (ukolItem.getStatus().equals("probehlo") || (SharedPrefHandler.getDefaultBool(context, "ukoly_done") && ukolItem.getStatus().equals("pozde")))
                    listFinished.add(ukolItem);
                else
                    listTodo.add(ukolItem);

            UkolyPagerAdapter pagerAdapter = (UkolyPagerAdapter)mPagerAdapter;
            UkolyPageFragment activeTab = (UkolyPageFragment)pagerAdapter.getItem(0);
            activeTab.onCallbackFinish(listTodo);
            UkolyPageFragment finishedTab = (UkolyPageFragment)pagerAdapter.getItem(1);
            finishedTab.onCallbackFinish(listFinished);


        } else {
            Toast.makeText(context, "Chyba při zpracovávání úkolů", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof UkolyPageFragment) {
            UkolyPageFragment ukolyPageFragment = (UkolyPageFragment) fragment;
            ukolyPageFragment.setUkolyInterface(this);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ukoly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPager = view.findViewById(R.id.pager);
        mPagerAdapter = new UkolyPagerAdapter(context, getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        UkolyPagerAdapter pagerAdapter = (UkolyPagerAdapter)mPagerAdapter;
        UkolyPageFragment tab = (UkolyPageFragment)pagerAdapter.getItem(0);
        tab.setUkolyInterface(this);
        tab = (UkolyPageFragment)pagerAdapter.getItem(1);
        tab.setUkolyInterface(this);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mPager);

        onPageRefresh();
    }

    public void makeRequest(){
        UkolyFragment.GetUkolyTask getUkolyTask = new UkolyFragment.GetUkolyTask(this);
        getUkolyTask.execute(BakaTools.getUrl(context) + "/login.aspx?hx=" + BakaTools.getToken(context) + "&pm=ukoly");
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

