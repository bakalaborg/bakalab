package org.bakalab.app.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bakalab.app.R;
import org.bakalab.app.adapters.RozvrhBasicAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.items.rozvrh.Rozvrh;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.rozvrh.RozvrhHodina;
import org.bakalab.app.items.rozvrh.RozvrhRoot;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.internal.EverythingIsNonNull;


public class RozvrhFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<Object> rozvrhList = new ArrayList<>();
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
        /*ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (clickable) {
                    boolean expanded = adapter.rozvrhList.get(position).isExpanded();
                    adapter.rozvrhList.get(position).setExpanded(!expanded);
                    adapter.notifyItemChanged(position);
                }
            }
        });*/

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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BakaTools.getUrl(this.getContext()))
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
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

                Rozvrh rozvrh = response.body().getRozvrh();

                for(RozvrhDen den : rozvrh.getDny()){
                    try{
                        List<RozvrhHodina> hodiny = den.getHodiny(); //supposedly there isn't a better way to check if the day actually exists
                        if(hodiny.isEmpty())
                            break;
                        den.fixTimes(rozvrh.getHodiny());
                        rozvrhList.add(den);

                        //iterating from the end to remove redundant free classes at the end of the day
                        //TODO: checking for free classes at the beginning of the day in a smart way
                        //TODO: třídnická hodina
                        ListIterator<RozvrhHodina> i = hodiny.listIterator(hodiny.size());
                        while (i.hasPrevious()) {
                            RozvrhHodina hodina = i.previous();
                            if(!hodina.getTyp().equals("X"))
                                break;

                            i.remove();
                        }

                        rozvrhList.addAll(hodiny);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }

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
}
