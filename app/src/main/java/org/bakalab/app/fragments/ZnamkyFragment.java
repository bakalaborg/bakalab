package org.bakalab.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;


import org.bakalab.app.R;
import org.bakalab.app.adapters.ZnamkyAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.items.znamky.Znamka;
import org.bakalab.app.items.znamky.ZnamkyRoot;
import org.bakalab.app.utils.BakaTools;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.internal.EverythingIsNonNull;


public class ZnamkyFragment extends RefreshableFragment {

    private List<Znamka> dataSet = new ArrayList<>();

    private ZnamkyAdapter znamkyAdapter;

    public ZnamkyFragment() {
        super(R.layout.fragment_znamky);
    }

    @Override
    public void onUserRefresh() {
        makeRequest();
    }

    @Override
    public void onRefreshableViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        znamkyAdapter = new ZnamkyAdapter(dataSet) {
            @Override
            public void onItemClick(View v, int position) {
                boolean expanded = znamkyAdapter.dataSet.get(position).isExpanded();
                znamkyAdapter.dataSet.get(position).setExpanded(!expanded);
                znamkyAdapter.notifyItemChanged(position);
            }
        };

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(znamkyAdapter);

        makeRequest();
    }

    private void makeRequest() {

        setRefreshing(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BakaTools.getUrl(this.getContext()))
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();

        BakalariAPI bakalariAPI = retrofit.create(BakalariAPI.class);

        Call<ZnamkyRoot> call = bakalariAPI.getZnamky(BakaTools.getToken(this.getContext()));

        call.enqueue(new retrofit2.Callback<ZnamkyRoot>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ZnamkyRoot> call, Response<ZnamkyRoot> response) {
                setRefreshing(false);
                if (!response.isSuccessful()) {
                    Log.d("Error", response.message());
                    return;
                }

                dataSet.clear();
                dataSet.addAll(response.body().getSortedZnamky());
                znamkyAdapter.notifyDataSetChanged();
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ZnamkyRoot> call, Throwable t) {
                t.getCause().printStackTrace();
                Log.d("Error", t.getMessage());

            }
        });
    }
}