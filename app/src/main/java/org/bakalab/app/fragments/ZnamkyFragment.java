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

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.bakalab.app.R;
import org.bakalab.app.adapters.ZnamkyBasicAdapter;
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


public class ZnamkyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<Znamka> znamkaList = new ArrayList<>();
    private ZnamkyBasicAdapter znamkyAdapter = new ZnamkyBasicAdapter(znamkaList);

    private List<Predmet> predmetList = new ArrayList<>();
    private ZnamkyPredmetAdapter predmetAdapter = new ZnamkyPredmetAdapter(predmetList);

    private RecyclerView.Adapter currentAdapter;

    private boolean clickable;

    private Context context;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private SkeletonScreen skeletonScreen;

    public ZnamkyFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_znamky, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);

        predmetAdapter.setResourceString(getString(R.string.predmety_popis));

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        recyclerView = view.findViewById(R.id.recycler);

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (clickable) {
                    if (currentAdapter.getClass() == ZnamkyBasicAdapter.class) {
                        boolean expanded = znamkyAdapter.znamkyList.get(position).isExpanded();
                        znamkyAdapter.znamkyList.get(position).setExpanded(!expanded);
                        znamkyAdapter.notifyItemChanged(position);
                    } else {
                        Toast.makeText(getContext(), "Later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        String last_view = SharedPrefHandler.getString(getContext(), "znamky_view");
        switch (last_view) {
            case "predmety":
                currentAdapter = predmetAdapter;
                break;
            case "znamky":
            default:
                currentAdapter = znamkyAdapter;
                break;
        }

        recyclerView.setAdapter(currentAdapter);

        makeRequest();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_znamky, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (clickable) {
            int id = item.getItemId();
            switch (id) {
                case R.id.change_view:
                    if (currentAdapter.getClass() == ZnamkyBasicAdapter.class) {
                        currentAdapter = predmetAdapter;
                        SharedPrefHandler.setString(getContext(), "znamky_view", "predmety");
                    } else {
                        currentAdapter = znamkyAdapter;
                        SharedPrefHandler.setString(getContext(), "znamky_view", "znamky");
                    }
                    break;
            }
            recyclerView.setAdapter(currentAdapter);
        }
        return super.onOptionsItemSelected(item);
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

        clickable = false;

        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(currentAdapter)
                .load(R.layout.list_item_skeleton)
                .count(10)
                .show();

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
                if (!response.isSuccessful()) {
                    Log.d("Error", response.message());
                    return;
                }

                znamkaList.clear();
                predmetList.clear();

                znamkaList.addAll(response.body().getSortedZnamky());
                znamkyAdapter.notifyDataSetChanged();

                predmetList.addAll(response.body().getPredmety());
                predmetAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);
                skeletonScreen.hide();
                clickable = true;
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