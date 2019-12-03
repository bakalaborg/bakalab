package org.bakalab.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import org.bakalab.app.R;
import org.bakalab.app.adapters.ZnamkyAdapter;
import org.bakalab.app.adapters.ZnamkyPredmetAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.items.znamky.Predmet;
import org.bakalab.app.items.znamky.Znamka;
import org.bakalab.app.items.znamky.ZnamkyRoot;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.ItemClickSupport;
import org.bakalab.app.utils.SharedPrefHandler;

import java.util.ArrayList;
import java.util.List;

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


public class ZnamkyFragment extends RefreshableFragment {

    private List<Znamka> znamkaList = new ArrayList<>();

    private ZnamkyAdapter znamkyAdapter;

    private RecyclerView recyclerView;

    public ZnamkyFragment() {
        super(R.layout.fragment_znamky);
    }

    @Override
    public void onUserRefresh() {
        makeRequest();
    }

    @Override
    public void onRefreshableViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        znamkyAdapter = new ZnamkyAdapter(znamkaList) {
            @Override
            public void onItemClick(int position) {
                boolean expanded = znamkyAdapter.dataSet.get(position).isExpanded();
                znamkyAdapter.dataSet.get(position).setExpanded(!expanded);
                znamkyAdapter.notifyItemChanged(position);
            }
        };

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(znamkyAdapter);

        makeRequest();
    }

    private void makeRequest() {

        setRefresh(true);

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
                setRefresh(false);
                if (!response.isSuccessful()) {
                    Log.d("Error", response.message());
                    return;
                }

                znamkaList.clear();

                znamkaList.addAll(response.body().getSortedZnamky());
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