package org.bakalab.app.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.bakalab.app.R;
import org.bakalab.app.interfaces.Callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainScreenFragment extends RefreshableFragment implements Callback {

    private TextView nextHourTitle, nextHourDescription;

    private ConstraintLayout rootLayout;

    public MainScreenFragment() {
        super(R.layout.fragment_main_screen);
    }


    @Override
    public void onUserRefresh() {
        makeRequest();
    }

    @Override
    public void onRefreshableViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nextHourTitle = view.findViewById(R.id.next_title);
        nextHourDescription = view.findViewById(R.id.next_desc);
        rootLayout = view.findViewById(R.id.root);

        makeRequest();

    }



    private void makeRequest() {

        //TODO make the actual request
    }

    @Override
    public void onCallbackFinish(Object callResult) {
        if (callResult != null) {

        } else {
            Toast.makeText(getContext(), "Chyba při zpracovávání", Toast.LENGTH_SHORT).show();
            setRefresh(false);
        }
    }

}
