package org.bakalab.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.bakalab.app.R;
import org.bakalab.app.adapters.ZnamkyBasicAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.items.znamky.Znamka;
import org.bakalab.app.items.znamky.ZnamkaPredmetyList;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.ItemClickSupport;

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
    private ZnamkyBasicAdapter adapter = new ZnamkyBasicAdapter(znamkaList);

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
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        recyclerView = view.findViewById(R.id.recycler);

        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (clickable) {
                    boolean expanded = adapter.znamkyList.get(position).isExpanded();
                    adapter.znamkyList.get(position).setExpanded(!expanded);
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

        clickable = false;

        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(adapter)
                .load(R.layout.list_item_skeleton)
                .count(10)
                .show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BakaTools.getUrl(this.getContext()))
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();

        BakalariAPI bakalariAPI = retrofit.create(BakalariAPI.class);

        Call<ZnamkaPredmetyList> call = bakalariAPI.getZnamky(BakaTools.getToken(this.getContext()));

        call.enqueue(new retrofit2.Callback<ZnamkaPredmetyList>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<ZnamkaPredmetyList> call, Response<ZnamkaPredmetyList> response) {
                if (!response.isSuccessful()) {
                    Log.d("Error", response.message());
                    return;
                }

                znamkaList.clear();

                znamkaList.addAll(response.body().getSortedZnamky());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                skeletonScreen.hide();
                clickable = true;
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<ZnamkaPredmetyList> call, Throwable t) {
                Log.d("Error", t.getMessage());

            }
        });
    }
}