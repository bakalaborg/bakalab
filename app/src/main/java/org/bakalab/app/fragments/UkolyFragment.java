package org.bakalab.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import org.bakalab.app.R;
import org.bakalab.app.adapters.UkolyPagerAdapter;
import org.bakalab.app.interfaces.BakalariAPI;
import org.bakalab.app.interfaces.Callback;
import org.bakalab.app.interfaces.UkolyInterface;
import org.bakalab.app.items.ukoly.Ukol;
import org.bakalab.app.items.ukoly.UkolyList;
import org.bakalab.app.utils.BakaTools;
import org.bakalab.app.utils.SharedPrefHandler;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.internal.EverythingIsNonNull;


public class UkolyFragment extends Fragment implements UkolyInterface {

    Callback callback;
    private Context context;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    public UkolyFragment() {
    }

    @Override
    public void onPageRefresh() {
        makeRequest();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof UkolyPageFragment) {
            UkolyPageFragment ukolyPageFragment = (UkolyPageFragment) fragment;
            ukolyPageFragment.setUkolyInterface(this);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ukoly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPager = view.findViewById(R.id.pager);
        mPagerAdapter = new UkolyPagerAdapter(context, getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        UkolyPagerAdapter pagerAdapter = (UkolyPagerAdapter) mPagerAdapter;
        UkolyPageFragment tab = (UkolyPageFragment) pagerAdapter.getItem(0);
        tab.setUkolyInterface(this);
        tab = (UkolyPageFragment) pagerAdapter.getItem(1);
        tab.setUkolyInterface(this);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mPager);

        onPageRefresh();
    }

    private void makeRequest() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BakaTools.getUrl(this.getContext()))
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();

        BakalariAPI bakalariAPI = retrofit.create(BakalariAPI.class);

        Call<UkolyList> call = bakalariAPI.getUkoly(BakaTools.getToken(this.getContext()));

        call.enqueue(new retrofit2.Callback<UkolyList>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<UkolyList> call, Response<UkolyList> response) {
                if (!response.isSuccessful() && response.body() != null) {
                    Log.e("Error", response.message());
                    return;
                }

                List<Ukol> listTodo = new ArrayList<>();
                List<Ukol> listFinished = new ArrayList<>();

                for(Ukol ukol : response.body().getUkoly())
                    if (ukol.getStatus().equals("probehlo") || (SharedPrefHandler.getDefaultBool(context, "ukoly_done") && ukol.getStatus().equals("pozde")))
                        listFinished.add(ukol);
                    else
                        listTodo.add(ukol);

                UkolyPagerAdapter pagerAdapter = (UkolyPagerAdapter) mPagerAdapter;
                UkolyPageFragment activeTab = (UkolyPageFragment) pagerAdapter.getItem(0);
                activeTab.onCallbackFinish(listTodo);
                UkolyPageFragment finishedTab = (UkolyPageFragment) pagerAdapter.getItem(1);
                finishedTab.onCallbackFinish(listFinished);

            }

            @Override
            public void onFailure(Call<UkolyList> call, Throwable t) {
                Log.e("Error", t.getMessage());

            }
        });
    }
}

