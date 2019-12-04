package org.bakalab.app.fragments;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.bakalab.app.R;
import org.bakalab.app.interfaces.Callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import retrofit2.Call;
import retrofit2.Response;

public class MainScreenFragment extends BakalabRefreshableFragment {

    private TextView nextHourTitle, nextHourDescription;

    private ConstraintLayout rootLayout;

    public MainScreenFragment() {
        super(R.layout.fragment_main_screen);
    }

    @Override
    public void onRefreshableViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nextHourTitle = view.findViewById(R.id.next_title);
        nextHourDescription = view.findViewById(R.id.next_desc);
        rootLayout = view.findViewById(R.id.root);
        //TODO Create and set call
        createRequest();

    }

    @Override
    public void onRequestCompleted(Call<Object> call, Response<Object> response) {
        // TODO Show next hour
    }

}
