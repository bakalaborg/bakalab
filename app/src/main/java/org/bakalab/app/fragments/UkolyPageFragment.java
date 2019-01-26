package org.bakalab.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.bakalab.app.R;
import org.bakalab.app.adapters.UkolyBasicAdapter;
import org.bakalab.app.interfaces.Callback;
import org.bakalab.app.interfaces.UkolyInterface;
import org.bakalab.app.items.ukoly.Ukol;
import org.bakalab.app.utils.ItemClickSupport;

public class UkolyPageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback {

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Ukol> ukolList = new ArrayList<>();
    private UkolyBasicAdapter adapter = new UkolyBasicAdapter(ukolList);
    private Context context;
    private boolean clickable;
    private UkolyInterface mUkolyInterface;
    private SkeletonScreen skeletonScreen;
    private  RecyclerView recyclerView;

    void setUkolyInterface(UkolyInterface fragment) {
        mUkolyInterface = fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.tab_content_ukoly, container, false);
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
                    boolean expanded = adapter.ukolyList.get(position).isExpanded();
                    adapter.ukolyList.get(position).setExpanded(!expanded);
                    adapter.notifyItemChanged(position);
                }
            }
        });

        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(adapter)
                .load(R.layout.list_item_skeleton)
                .count(10)
                .show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(adapter)
                .load(R.layout.list_item_skeleton)
                .count(10)
                .show();
        mUkolyInterface.onPageRefresh();
    }

    @Override
    @SuppressWarnings("unchecked")
    // this is probably safe since it works and I want the compiler to shut up
    public void onCallbackFinish(Object result) {
        // TODO If there is nothing add placeholder
        if (result != null) {
            clickable = false;
            ukolList.clear();
            ukolList.addAll((List<Ukol>) result);
            adapter.notifyDataSetChanged();
            skeletonScreen.hide();
            swipeRefreshLayout.setRefreshing(false);
            clickable = true;
        } else {
            Toast.makeText(context, "Chyba při zpracovávání úkolů", Toast.LENGTH_SHORT).show();
        }
    }
}