package org.bakalab.app.fragments;

import android.content.Context;
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

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public abstract class BakalabRefreshableFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private int layout;
    private Call<Object> call = null;

    BakalabRefreshableFragment(int layout) {
        this.layout = layout;
    }

    protected void setCall(Call<Object> call) {
        this.call = call;
    }

    public abstract void onRefreshableViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);
    public abstract void onRequestCompleted(Call<Object> call, Response<Object> response);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refreshable, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);

        LinearLayout inflatable = view.findViewById(R.id.inflatable_view);
        View requestedLayout = getLayoutInflater().inflate(layout, inflatable, false);
        inflatable.addView(requestedLayout);
        onRefreshableViewCreated(view, savedInstanceState);

    }

    private void onRefresh() {
        createRequest();
    }

    void createRequest() {

        if (call == null) {
            return;
        }

        setRefreshing(true);

        call.enqueue(new retrofit2.Callback<Object>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (!response.isSuccessful()) {
                    // TODO proper error
                    Log.d("Error", response.message());
                    return;
                }

                setRefreshing(false);
                onRequestCompleted(call, response);

            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Object> call, Throwable t) {
                // TODO Proper error
                Log.d("Errorsss", t.getMessage());

            }
        });
    }

    void setRefreshing(boolean refresh) {
        swipeRefreshLayout.setRefreshing(refresh);
    }
}
