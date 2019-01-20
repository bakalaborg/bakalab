package michaelbrabec.bakalab.fragments;


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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
        getRozvrhTask.execute(BakaTools.getUrl(context) + "/login.aspx?hx=" + BakaTools.getToken(context) + "&pm=rozvrh&pmd=" + Utils.getCurrentMonday());
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
                                    if(tagContent.equals("X")){
                                        //FREE CLASS | VOLNA HODINA

                                        if(!lastType.equals("X"))
                                            emptyRozvrhList = new ArrayList<>();

                                        try{
                                            RozvrhTimeItem currentClass = rozvrhTimeList.get(iteration);
                                            rozvrh.setBegintime(currentClass.getBegintime());
                                            rozvrh.setEndtime(currentClass.getEndtime());
                                            emptyRozvrhList.add(rozvrh);
                                            rozvrh = new RozvrhItem();

                                            /*we add to a separate array so we can throw it all away if we need to
                                              bakaláři likes to mark all classes as existant even if they don't
                                              exist, so adding them to the rozvrh array directly would just make
                                              us annoy the user by spamming them with empty classes */

                                        }catch(NullPointerException e){
                                            e.printStackTrace();
                                        }catch(NumberFormatException e){
                                            e.printStackTrace();
                                        }catch(IndexOutOfBoundsException e){
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
                                    if(!lastNonXType.equals("Date") && !emptyRozvrhList.isEmpty())
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

}
