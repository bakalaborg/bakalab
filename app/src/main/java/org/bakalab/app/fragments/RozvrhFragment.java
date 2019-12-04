package org.bakalab.app.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.bakalab.app.R;
import org.bakalab.app.adapters.RozvrhAdapter;
import org.bakalab.app.adapters.RozvrhBasicAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.items.rozvrh.Rozvrh;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.rozvrh.RozvrhRoot;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.internal.EverythingIsNonNull;


public class RozvrhFragment extends RefreshableFragment{

    private List<Object> rozvrhList = new ArrayList<>();
    private RozvrhAdapter adapter;

    private RecyclerView recyclerView;

    public RozvrhFragment() {
        super(R.layout.fragment_rozvrh);
    }

    @Override
    public void onUserRefresh() {
        makeRequest();
    }

    @Override
    public void onRefreshableViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        adapter = new RozvrhAdapter(rozvrhList) {
            @Override
            public void onItemClick(View v, int position) {
                /*
                boolean expanded = adapter.rozvrhList.get(position).isExpanded();
                adapter.rozvrhList.get(position).setExpanded(!expanded);
                adapter.notifyItemChanged(position);
                 */
            }
        };

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        makeRequest();
    }

    private void makeRequest() {

        setRefreshing(true);

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

                int position = 0;

                rozvrhList.clear();

                Rozvrh rozvrh = response.body().getRozvrh();

                for(RozvrhDen den : rozvrh.getDny()){
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
                    Date date = new Date();
                    if(den.getDatum().equals(dateFormat.format(date)))
                        position = rozvrhList.size() + den.getCurrentLessonInt() - 2;

                    rozvrhList.add(den);
                    rozvrhList.addAll(den.getHodiny());
                }

                adapter.notifyDataSetChanged();
                setRefreshing(false);

                recyclerView.scrollToPosition(position);
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<RozvrhRoot> call, Throwable t) {
                Log.d("Errorsss", t.getMessage());

            }
        });
    }
}
