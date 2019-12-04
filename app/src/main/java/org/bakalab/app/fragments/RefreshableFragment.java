package org.bakalab.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.bakalab.app.R;

public abstract class RefreshableFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private int layout;

    RefreshableFragment(int layout) {
        this.layout = layout;
    }

    public abstract void onUserRefresh();
    public abstract void onRefreshableViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refreshable, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::onUserRefresh);

        LinearLayout inflatable = view.findViewById(R.id.inflatable_view);
        View requestedLayout = getLayoutInflater().inflate(layout, inflatable, false);
        inflatable.addView(requestedLayout);
        onRefreshableViewCreated(view, savedInstanceState);
    }

    void setRefreshing(boolean refresh) {
        swipeRefreshLayout.setRefreshing(refresh);
    }
}
