package org.bakalab.app.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import org.bakalab.app.R;
import org.bakalab.app.activities.MainActivity;

import java.lang.ref.WeakReference;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
