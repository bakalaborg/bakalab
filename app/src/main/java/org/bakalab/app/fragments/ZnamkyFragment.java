package org.bakalab.app.fragments;

import android.os.Bundle;
import android.view.View;


import org.bakalab.app.R;
import org.bakalab.app.adapters.ZnamkyAdapter;
import org.bakalab.app.interfaces.Api;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.items.rozvrh.RozvrhRoot;
import org.bakalab.app.items.znamky.Znamka;
import org.bakalab.app.items.znamky.ZnamkyRoot;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Response;

@SuppressWarnings("unchecked")
public class ZnamkyFragment extends BakalabRefreshableFragment {

    private List<Znamka> dataSet = new ArrayList<>();

    private ZnamkyAdapter znamkyAdapter;

    public ZnamkyFragment() {
        super(R.layout.fragment_znamky);
        BakalariAPI bakalariAPI = Api.getInstance(getContext()).getBakalariAPI();
        Call<ZnamkyRoot> call = bakalariAPI.getZnamky(BakaTools.getToken(this.getContext()));
        setCall((Call<Object>)(Call<?>) call);
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

        createRequest();
    }

    @Override
    public void onRequestCompleted(Call<Object> call, Response<Object> response) {
        Response<ZnamkyRoot> castedResponse = (Response<ZnamkyRoot>)(Response<?>)response;
        dataSet.clear();
        dataSet.addAll(castedResponse.body().getSortedZnamky());
        znamkyAdapter.notifyDataSetChanged();
    }
}