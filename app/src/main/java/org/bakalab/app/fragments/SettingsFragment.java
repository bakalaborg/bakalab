package org.bakalab.app.fragments;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import org.bakalab.app.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
