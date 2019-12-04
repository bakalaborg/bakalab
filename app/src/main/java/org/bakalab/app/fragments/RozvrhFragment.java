package org.bakalab.app.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.bakalab.app.App;
import org.bakalab.app.R;
import org.bakalab.app.adapters.RozvrhAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.interfaces.Api;
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

@SuppressWarnings("unchecked")
public class RozvrhFragment extends BakalabRefreshableFragment {

    private List<Object> rozvrhList = new ArrayList<>();
    private RozvrhAdapter adapter;

    private RecyclerView recyclerView;

    public RozvrhFragment() {
        super(R.layout.fragment_rozvrh);

        BakalariAPI bakalariAPI = Api.getInstance(App.getAppContext()).getBakalariAPI();
        Call<RozvrhRoot> call = bakalariAPI.getRozvrh(
                BakaTools.getToken(this.getContext()), Utils.getCurrentMonday());
        setCall((Call<Object>)(Call<?>) call);

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
        createRequest();
    }

    @Override
    public void onRequestCompleted(Call<Object> call, Response<Object> response) {

        Response<RozvrhRoot> castedResponse = (Response<RozvrhRoot>)(Response<?>)response;

        int position = 0;

        rozvrhList.clear();

        Rozvrh rozvrh = castedResponse.body().getRozvrh();

        for(RozvrhDen den : rozvrh.getDny()){
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            Date date = new Date();
            if(den.getDatum().equals(dateFormat.format(date)))
                position = rozvrhList.size() + den.getCurrentLessonInt() - 2;

            rozvrhList.add(den);
            rozvrhList.addAll(den.getHodiny());
        }

        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(position);
    }
}
